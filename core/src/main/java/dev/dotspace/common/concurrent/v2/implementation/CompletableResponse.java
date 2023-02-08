package dev.dotspace.common.concurrent.v2.implementation;

import dev.dotspace.common.SpaceObjects;
import dev.dotspace.common.concurrent.v2.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class CompletableResponse<TYPE> implements Response<TYPE> {
  private final @NotNull ExecutorService executorService;
  private volatile @NotNull State state;
  private volatile @Nullable TYPE response;
  private volatile @Nullable Throwable throwable;
  private volatile Executor<?>[] executors;

  public CompletableResponse() {
    this(State.UNCOMPLETED);
  }

  private CompletableResponse(@NotNull final State state) {
    this.executorService = Executors.newCachedThreadPool();
    this.state = state;
    this.executors = new Executor[0];
  }

  @Override
  public @Nullable TYPE get() throws InterruptedException {
    return this.getImplementation(-1);
  }

  @Override
  public @Nullable TYPE get(long nanos) throws InterruptedException {
    return this.getImplementation(nanos);
  }

  @Override
  public @Nullable TYPE get(long duration, @Nullable TimeUnit timeUnit) throws InterruptedException {
    return this.getImplementation(SpaceObjects.throwIfNull(timeUnit).toNanos(duration));
  }

  /**
   * Implementation for the get methods. The given time is in nanoseconds.
   * Negative numbers mean there is no interrupt time.
   *
   * @param nanoTimeout the time until the process is to be interrupted.
   * @return returns the completed value of this answer.
   * @throws InterruptedException if the process was interrupted in time. (timestamp of nanoTimeout reached).
   */
  private @Nullable TYPE getImplementation(final long nanoTimeout) throws InterruptedException {
    final boolean unlimited = nanoTimeout < 0; //True if there is no limit.
    final long interruptTime = unlimited ? Long.MAX_VALUE : System.nanoTime() + (nanoTimeout == 0 ? 1 /*Smallest value.*/ : nanoTimeout);

    while (!this.done()) {
      if (this.canceled()) { //Cancels when the response is canceled.
        break;
      }
      if (System.nanoTime() >= interruptTime) { //Throws an error when the time point is reached.
        throw this.completeExceptionallyImplementation(new InterruptedException("No value present!"));
      }
    }
    return this.response; //Returns the value of the class.
  }

  @Override
  public @Nullable TYPE getNow(@Nullable Supplier<TYPE> alternativeValue) {
    if (this.response != null) {
      return this.response;
    }

    TYPE alternative = null;

    if (alternativeValue != null) {
      alternative = alternativeValue.get();
    }

    if (alternative != null) {
      this.completeImplementation(alternative);
    }

    return alternative;
  }

  @Override
  public synchronized boolean cancel() {
    if (this.state.done()) {
      return false;
    }
    this.throwable = new InterruptedException("Response canceled.");
    this.markAsCompleted(State.CANCELLED);
    return true;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> complete(@Nullable TYPE type) {
    this.completeImplementation(type);
    return this;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> completeAsync(@Nullable Supplier<TYPE> typeSupplier) {
    this.executorService.execute(() -> {
      try {
        this.completeImplementation(SpaceObjects.throwIfNull(typeSupplier).get());
      } catch (final Throwable throwable) {
        this.completeExceptionallyImplementation(throwable);
      }
    });
    return this;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> completeExceptionally(@Nullable Throwable throwable) {
    this.completeExceptionallyImplementation(throwable);
    return this;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> completeExceptionally(@Nullable Supplier<Throwable> throwableSupplier) {
    this.executorService.execute(() -> this.completeExceptionallyImplementation(SpaceObjects.throwIfNull(throwableSupplier).get()));
    return this;
  }

  private synchronized void completeImplementation(@Nullable final TYPE completeResponse) {
    if (this.state.done()) {
      return; //Return if done.
    }
    this.response = completeResponse;
    this.markAsCompleted(completeResponse != null ? State.COMPLETED_DEFAULT : State.COMPLETED_NULL);
  }

  private synchronized <ERROR extends Throwable> @Nullable ERROR completeExceptionallyImplementation(@Nullable final ERROR throwable) {
    if (!this.state.done()) {
      this.throwable = throwable;
      this.markAsCompleted(State.COMPLETED_EXCEPTIONALLY);
    }
    return throwable;
  }

  private void markAsCompleted(@NotNull final State state) {
    this.state = state;

    for (final Executor<?> executor : this.executors) {
      executor.run(this.executorService);
    }
  }

  @Override
  public @NotNull CompletableResponse<TYPE> sniff(@Nullable ResponseConsumer<TYPE> responseConsumer) {
    this.sniffImplementation(responseConsumer, false);
    return this;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> sniffAsync(@Nullable ResponseConsumer<TYPE> responseConsumer) {
    this.sniffImplementation(responseConsumer, true);
    return this;
  }

  /**
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#sniff(ResponseConsumer)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#sniffAsync(ResponseConsumer)}</dd>
   * </dl>
   *
   * @param responseConsumer
   * @param async
   */
  private void sniffImplementation(@Nullable final ResponseConsumer<TYPE> responseConsumer,
                                   final boolean async) {
    if (responseConsumer == null) {
      return;
    }

    this.implementExecutor(new ResponseExecutor<>(() -> true, () -> {
      try {
        responseConsumer.accept(this.state, this.response, this.throwable);
      } catch (final Throwable throwable) {
        throwable.printStackTrace();
      }
    }, async));
  }

  /**
   * See {@link Response#run(Runnable)}.
   */
  @Override
  public @NotNull Response<TYPE> run(@Nullable Runnable runnable) {
    this.runImplementation(runnable, false); //Run implementation.
    return this;
  }

  @Override
  public @NotNull Response<TYPE> runAsync(@Nullable Runnable runnable) {
    this.runImplementation(runnable, true); //Run implementation.
    return this;
  }

  /**
   * Implementation to execute {@link Runnable} if completed.
   * <br>
   * Implementation used by:
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#run(Runnable)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#runAsync(Runnable)}</dd>
   * </dl>
   *
   * @param runnable to be executed if {@link Response} is completed.
   * @param async    true, if the runnable is to be executed asynchronously.
   */
  private void runImplementation(@Nullable final Runnable runnable,
                                 final boolean async) {
    if (runnable == null) {
      return; //Return, runnable is null means there is no function to run.
    }

    this.implementExecutor(new ResponseExecutor<>( //Create new executor instance.
      () -> true, //Run in any condition
      () -> {
        try { //Catch possible errors from runnable.
          runnable.run(); //Execute runnable.
        } catch (final Throwable throwable) {
          throwable.printStackTrace(); //Print errors.
        }
      }, async));
  }

  /**
   * See {@link Response#ifPresent(Consumer)}.
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifPresent(@Nullable Consumer<@NotNull TYPE> consumer) {
    this.ifPresentImplementation(consumer, false); //Run implementation.
    return this;
  }

  /**
   * See {@link Response#ifPresentAsync(Consumer)}.
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifPresentAsync(@Nullable Consumer<@NotNull TYPE> consumer) {
    this.ifPresentImplementation(consumer, true); //Run implementation.
    return this;
  }

  /**
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#ifPresent(Consumer)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#ifPresentAsync(Consumer)}</dd>
   * </dl>
   *
   * @param consumer
   * @param async
   */
  private void ifPresentImplementation(@Nullable final Consumer<@NotNull TYPE> consumer,
                                       final boolean async) {
    if (consumer == null) {
      return;
    }

    this.implementExecutor(new ResponseExecutor<>(() -> this.response != null && this.state == State.COMPLETED_DEFAULT,
      () -> {
        try {
          final TYPE threadResponse = this.response;
          if (threadResponse == null) {
            return;
          }
          consumer.accept(threadResponse);
        } catch (final Throwable throwable) {
          throwable.printStackTrace();
        }
      }, async));
  }

  @Override
  public @NotNull <MAP> CompletableResponse<MAP> map(@Nullable Function<TYPE, MAP> function) {
    return this.mapImplementation(function, false);
  }

  @Override
  public @NotNull <MAP> CompletableResponse<MAP> mapAsync(@Nullable Function<TYPE, MAP> function) {
    return this.mapImplementation(function, true);
  }

  /**
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#map(Function)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#mapAsync(Function)}</dd>
   * </dl>
   *
   * @param function
   * @param async
   * @param <MAP>
   * @return
   */
  private <MAP> CompletableResponse<MAP> mapImplementation(@Nullable final Function<TYPE, MAP> function,
                                                           final boolean async) {
    final CompletableResponse<MAP> completableResponse = new CompletableResponse<>();
    this.implementExecutor(new ResponseExecutor<>(() -> this.response != null && this.state == State.COMPLETED_DEFAULT, () -> {
      try {
        completableResponse.complete(SpaceObjects.throwIfNull(function).apply(this.response));
      } catch (final Throwable throwable) {
        completableResponse.completeExceptionally(throwable);
      }
    }, async));
    return completableResponse;
  }

  /**
   * @see Response#filter(Predicate)
   */
  @Override
  public @NotNull Response<TYPE> filter(@Nullable Predicate<TYPE> typePredicate) {
    return this.filterImplementation(typePredicate, false);
  }

  /**
   * @see Response#filterAsync(Predicate)
   */
  @Override
  public @NotNull Response<TYPE> filterAsync(@Nullable Predicate<TYPE> typePredicate) {
    return this.filterImplementation(typePredicate, true);
  }

  private @NotNull CompletableResponse<TYPE> filterImplementation(@Nullable final Predicate<TYPE> typePredicate,
                                                                  final boolean async) {
    final CompletableResponse<TYPE> completableResponse = new CompletableResponse<>();
    this.implementExecutor(new ResponseExecutor<>(
      () -> this.response != null && this.state == State.COMPLETED_DEFAULT,
      () -> {
        try {
          final TYPE currentValue = this.response;
          completableResponse.complete(currentValue != null && SpaceObjects.throwIfNull(typePredicate).test(this.response) ? currentValue : null);
        } catch (final Throwable throwable) {
          completableResponse.completeExceptionally(throwable);
        }
      }, async));
    return completableResponse;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifAbsent(@Nullable Runnable runnable) {
    this.ifAbsentImplementation(runnable, false);
    return this;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifAbsentAsync(@Nullable Runnable runnable) {
    this.ifAbsentImplementation(runnable, true);
    return this;
  }

  /**
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#ifAbsent(Runnable)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#ifAbsentAsync(Runnable)}</dd>
   * </dl>
   *
   * @param runnable
   * @param async
   */
  private void ifAbsentImplementation(@Nullable final Runnable runnable,
                                      final boolean async) {
    if (runnable == null) {
      return;
    }

    this.implementExecutor(new ResponseExecutor<>(() -> this.state == State.COMPLETED_NULL, () -> {
      try {
        runnable.run();
      } catch (final Throwable throwable) {
        throwable.printStackTrace();
      }
    }, async));
  }

  @Override
  public @NotNull CompletableResponse<TYPE> useIfAbsent(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_NULL, false);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> useIfAbsentAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_NULL, true);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> useIfExceptionally(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_EXCEPTIONALLY, false);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> useIfExceptionallyAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_EXCEPTIONALLY, true);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> elseUse(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(
      typeSupplier,
      () -> this.state == State.COMPLETED_NULL || this.state == State.COMPLETED_EXCEPTIONALLY,
      false);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> elseUseAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(
      typeSupplier,
      () -> this.state == State.COMPLETED_NULL || this.state == State.COMPLETED_EXCEPTIONALLY,
      true);
  }

  /**
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#useIfAbsent(Supplier)}</dd>
   *   <dd>{@link CompletableResponse#useIfExceptionally(Supplier)}</dd>
   *   <dd>{@link CompletableResponse#elseUse(Supplier)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#useIfAbsentAsync(Supplier)}</dd>
   *   <dd>{@link CompletableResponse#useIfExceptionallyAsync(Supplier)}</dd>
   *   <dd>{@link CompletableResponse#elseUseAsync(Supplier)}</dd>
   * </dl>
   *
   * @param typeSupplier
   * @param checkIfExecutable
   * @param async
   * @return
   */
  private @NotNull CompletableResponse<TYPE> useImplementation(@Nullable final Supplier<TYPE> typeSupplier,
                                                               @NotNull final Supplier<Boolean> checkIfExecutable,
                                                               final boolean async) {
    final CompletableResponse<TYPE> completableResponse = new CompletableResponse<>();
    this.implementExecutor(new ResponseExecutor<>(checkIfExecutable,
      () -> {
        if (this.response != null) {
          return;
        }
        try {
          completableResponse.complete(SpaceObjects.throwIfNull(typeSupplier).get());
        } catch (final Throwable throwable) {
          completableResponse.completeExceptionally(throwable);
        }
      }, async));

    return completableResponse;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifExceptionally(@Nullable Consumer<@Nullable Throwable> consumer) {
    this.ifExceptionallyImplementation(consumer, false);
    return this;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifExceptionallyAsync(@Nullable Consumer<@Nullable Throwable> consumer) {
    this.ifExceptionallyImplementation(consumer, true);
    return this;
  }

  /**
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#ifExceptionally(Consumer)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#ifExceptionallyAsync(Consumer)}</dd>
   * </dl>
   *
   * @param consumer
   * @param async
   */
  private void ifExceptionallyImplementation(@Nullable final Consumer<Throwable> consumer,
                                             final boolean async) {
    if (consumer == null) { //Return and ignore consumer if null.
      return;
    }

    this.implementExecutor(new ResponseExecutor<>(
      () -> this.state == State.COMPLETED_EXCEPTIONALLY, //Only run if response was completed with error.
      () -> {
        try {
          consumer.accept(this.throwable);
        } catch (final Throwable throwable) {
          throwable.printStackTrace(); //Print error which could potentially be thrown in the consumer.
        }
      }, async));
  }

  /**
   * See {@link Response#done()}.
   */
  @Override
  public boolean done() {
    return this.state.done();
  }

  /**
   * See {@link Response#canceled()}.
   */
  @Override
  public boolean canceled() {
    return this.state == State.CANCELLED;
  }

  @Override
  public boolean exceptionally() {
    return this.state == State.COMPLETED_EXCEPTIONALLY;
  }

  private synchronized void implementExecutor(@NotNull final Executor<?> executor) {
    if (this.done()) { //Directly run executor if already finished.
      executor.run(this.executorService);
    } else { //Add to run later if response is completed.
      this.executors = Arrays.copyOf(this.executors, this.executors.length + 1);
      this.executors[this.executors.length - 1] = executor;
    }
  }

  /**
   * static
   */
  public static @NotNull <TYPE> CompletableResponse<TYPE> exceptionally(@NotNull final Throwable throwable) {
    return new CompletableResponse<TYPE>().completeExceptionally(throwable);
  }

  public static @NotNull <TYPE> CompletableResponse<MultiResponse<TYPE>> combined(@NotNull final List<CompletableResponse<TYPE>> list) {
    final List<CompletableResponse<TYPE>> listClone = new ArrayList<>(list);
    final CompletableResponse<MultiResponse<TYPE>> multiResponseCompletableResponse = new CompletableResponse<>();

    multiResponseCompletableResponse.completeAsync(() -> {
      final MultiResponse<TYPE> typeMultiResponse = new MultiResponse<>(new ArrayList<>(), new ArrayList<>());

      for (final CompletableResponse<TYPE> typeCompletableResponse : listClone) {
        typeCompletableResponse.sniff((state, type, throwable) -> {
          System.out.println(state);
          if (state == State.COMPLETED_DEFAULT && type != null) {
            typeMultiResponse.responseList().add(type);
          } else {
            typeMultiResponse.throwableList().add(throwable != null ? throwable : new NullPointerException("Value is absent."));
          }
        });
      }

      while (typeMultiResponse.values() < listClone.size()) {
        if (multiResponseCompletableResponse.canceled()) {
          multiResponseCompletableResponse.completeExceptionally(new InterruptedException("Already interrupted."));
          return null;
        }
      }

      System.out.println("Done");

      return typeMultiResponse;
    });
    return multiResponseCompletableResponse;
  }

  public static @NotNull <TYPE> CompletableResponse<TYPE> equalResponse(@NotNull final List<CompletableResponse<TYPE>> list) {
    return null;
  }

}
