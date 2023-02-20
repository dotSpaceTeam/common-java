package dev.dotspace.common.response;

import dev.dotspace.common.SpaceObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class CompletableResponse<TYPE> implements Response<TYPE> {
  private final @NotNull ExecutorService executorService;
  private volatile @NotNull State state;
  private volatile @Nullable TYPE response;
  private volatile @Nullable Throwable throwable;
  private volatile @NotNull ResponseFunction<?>[] responseFunctions;

  /**
   * Public constructor to create instance.
   */
  public CompletableResponse() {
    this(State.UNCOMPLETED);
  }

  /**
   * This constructor can be used to create an instance without explicitly defining a generic type.
   * This method is a gimmick, the class is not saved, it is only used to set the type.
   *
   * @param typeClass defines the TYPE of the instance.
   */
  public CompletableResponse(@Nullable final Class<TYPE> typeClass) {
    this();
  }

  /**
   * Create instance with defined state.
   *
   * @param state to set as start {@link State}.
   */
  private CompletableResponse(@NotNull final State state) {
    this.executorService = Executors.newCachedThreadPool();
    this.state = state;
    this.responseFunctions = new ResponseFunction[0];
  }

  /**
   * @see Response#newUncompleted()
   */
  @Override
  public @NotNull CompletableResponse<TYPE> newUncompleted() {
    return new CompletableResponse<TYPE>();
  }

  /**
   * @see Response#get()
   */
  @Override
  public @Nullable TYPE get() throws InterruptedException {
    return this.getImplementation(-1);
  }

  /**
   * @see Response#get(long)
   */
  @Override
  public @Nullable TYPE get(long nanos) throws InterruptedException {
    return this.getImplementation(nanos);
  }

  /**
   * @see Response#get(long, TimeUnit)
   */
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

  /**
   * @see Response#getNow(Supplier)
   */
  @Override
  public synchronized @Nullable TYPE getNow(@Nullable Supplier<TYPE> alternativeValue) {
    if (this.response != null) { //Response is completed.
      return this.response; //Response.
    }

    TYPE alternative = null; //Create variable.

    if (alternativeValue != null) { //If supplier is present get value.
      alternative = alternativeValue.get();
    }

    if (alternative != null) { //If supplier was not null and value is also present.
      this.completeImplementation(alternative); //Complete this response with supplied value.
    }

    return alternative;
  }

  /**
   * @see Response#cancel()
   */
  @Override
  public synchronized boolean cancel() {
    if (this.state.done()) {
      return false;
    }
    this.throwable = new InterruptedException("Response canceled.");
    this.markAsCompleted(State.CANCELLED);
    return true;
  }

  /**
   * @see Response#complete(Object)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> complete(@Nullable TYPE type) {
    this.completeImplementation(type);
    return this;
  }

  /**
   * @see Response#completeAsync(Supplier)
   */
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

  /**
   * @see Response#completeExceptionally(Throwable)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> completeExceptionally(@Nullable Throwable throwable) {
    this.completeExceptionallyImplementation(throwable);
    return this;
  }

  /**
   * @see Response#completeExceptionallyAsync(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> completeExceptionallyAsync(@Nullable Supplier<Throwable> throwableSupplier) {
    this.executorService.execute(() -> {
      try {
        this.completeExceptionallyImplementation(SpaceObjects.throwIfNull(throwableSupplier).get());
      } catch (final NullPointerException nullPointerException) {
        this.completeExceptionally(nullPointerException);
      }
    });
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

    for (final ResponseFunction<?> executor : this.responseFunctions) {
      executor.run(this.executorService);
    }
  }

  /**
   * @see Response#sniff(ResponseConsumer)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> sniff(@Nullable ResponseConsumer<TYPE> responseConsumer) {
    this.sniffImplementation(responseConsumer, false);
    return this;
  }

  /**
   * @see Response#sniffAsync(ResponseConsumer)
   */
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
   * @param async            true, if the runnable is to be executed asynchronously.
   */
  private void sniffImplementation(@Nullable final ResponseConsumer<TYPE> responseConsumer,
                                   final boolean async) {
    if (responseConsumer == null) {
      return; //Return if null. Ignore request
    }

    this.implementExecutor(new ResponseFunctionExecutor<>(
      () -> true, //Run in any condition
      () -> {
        try { //Catch possible errors from runnable.
          responseConsumer.accept(this.state, this.response, this.throwable);
        } catch (final Throwable throwable) {
          throwable.printStackTrace(); //Print errors.
        }
      }, async));
  }

  /**
   * @see Response#run(Runnable)
   */
  @Override
  public @NotNull Response<TYPE> run(@Nullable Runnable runnable) {
    this.runImplementation(runnable, false); //Run implementation.
    return this;
  }

  /**
   * @see Response#runAsync(Runnable)
   */
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

    this.implementExecutor(new ResponseFunctionExecutor<>( //Create new executor instance.
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
   * @see Response#ifPresent(Consumer)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifPresent(@Nullable Consumer<@NotNull TYPE> consumer) {
    this.ifPresentImplementation(consumer, false); //Run implementation.
    return this;
  }

  /**
   * @see Response#ifPresentAsync(Consumer)
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

    this.implementExecutor(new ResponseFunctionExecutor<>(
      () -> this.response != null && this.state == State.COMPLETED_DEFAULT,
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

  /**
   * @see Response#map(Function)
   */
  @Override
  public @NotNull <MAP> CompletableResponse<MAP> map(@Nullable Function<TYPE, MAP> function) {
    return this.mapImplementation(function, false);
  }

  /**
   * @see Response#mapAsync(Function)
   */
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
    this.implementExecutor(new ResponseFunctionExecutor<>(() -> this.response != null && this.state == State.COMPLETED_DEFAULT, () -> {
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
    this.implementExecutor(new ResponseFunctionExecutor<>(
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

  /**
   * @see Response#ifAbsent(Runnable)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifAbsent(@Nullable Runnable runnable) {
    this.ifAbsentImplementation(runnable, false);
    return this;
  }

  /**
   * @see Response#ifAbsentAsync(Runnable)
   */
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

    this.implementExecutor(new ResponseFunctionExecutor<>(() -> this.state == State.COMPLETED_NULL, () -> {
      try {
        runnable.run();
      } catch (final Throwable throwable) {
        throwable.printStackTrace();
      }
    }, async));
  }

  /**
   * @see Response#useIfAbsent(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> useIfAbsent(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_NULL, false);
  }

  /**
   * @see Response#useIfAbsentAsync(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> useIfAbsentAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_NULL, true);
  }

  /**
   * @see Response#useIfExceptionally(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> useIfExceptionally(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_EXCEPTIONALLY, false);
  }

  /**
   * @see Response#useIfExceptionallyAsync(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> useIfExceptionallyAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_EXCEPTIONALLY, true);
  }

  /**
   * @see Response#elseUse(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> elseUse(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(
      typeSupplier,
      () -> this.state == State.COMPLETED_NULL || this.state == State.COMPLETED_EXCEPTIONALLY,
      false);
  }

  /**
   * @see Response#elseUseAsync(Supplier)
   */
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
    this.implementExecutor(new ResponseFunctionExecutor<>(checkIfExecutable,
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

  /**
   * @see Response#ifExceptionally(Consumer)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifExceptionally(@Nullable Consumer<@Nullable Throwable> consumer) {
    this.ifExceptionallyImplementation(consumer, false);
    return this;
  }

  /**
   * @see Response#ifExceptionallyAsync(Consumer)
   */
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

    this.implementExecutor(new ResponseFunctionExecutor<>(
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
   * @see Response#done()
   */
  @Override
  public boolean done() {
    return this.state.done();
  }

  /**
   * @see Response#canceled()
   */
  @Override
  public boolean canceled() {
    return this.state == State.CANCELLED;
  }

  /**
   * @see Response#exceptionally()
   */
  @Override
  public boolean exceptionally() {
    return this.state == State.COMPLETED_EXCEPTIONALLY;
  }

  private synchronized void implementExecutor(@NotNull final ResponseFunction<?> executor) {
    if (this.done()) { //Directly run executor if already finished.
      executor.run(this.executorService);
    } else { //Add to run later if response is completed.
      this.responseFunctions = Arrays.copyOf(this.responseFunctions, this.responseFunctions.length + 1);
      this.responseFunctions[this.responseFunctions.length - 1] = executor;
    }
  }

  private @NotNull ExecutorService service() {
    return this.executorService;
  }

  /**
   * static
   */
  public static @NotNull <TYPE> CompletableResponse<TYPE> exceptionally(@NotNull final Throwable throwable) {
    return new CompletableResponse<TYPE>().completeExceptionally(throwable);
  }

  public static @NotNull <TYPE> CompletableResponse<MultiResponse<TYPE>> collect(@NotNull final List<CompletableResponse<TYPE>> list) {
    final List<CompletableResponse<TYPE>> listClone = new ArrayList<>(list);
    final CompletableResponse<MultiResponse<TYPE>> multiResponseCompletableResponse = new CompletableResponse<>();

    multiResponseCompletableResponse.completeAsync(() -> {
      final MultiResponse<TYPE> typeMultiResponse = new MultiResponse<>(new ArrayList<>(), new ArrayList<>());

      for (final CompletableResponse<TYPE> typeCompletableResponse : listClone) {
        typeCompletableResponse.sniff((state, type, throwable) -> {
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

  /**
   * Use answer which is available first.
   *
   * @param list
   * @param <TYPE>
   * @return
   */
  public static @NotNull <TYPE> CompletableResponse<TYPE> first(@NotNull final List<CompletableResponse<TYPE>> list) {
    final List<CompletableResponse<TYPE>> listClone = new ArrayList<>(list);
    final CompletableResponse<TYPE> firstResponse = new CompletableResponse<>();

    firstResponse.service().execute(() -> {
      final AtomicInteger atomicInteger = new AtomicInteger();

      for (final CompletableResponse<TYPE> typeCompletableResponse : list) {
        typeCompletableResponse
          .ifPresent(firstResponse::complete)
          .ifExceptionally(throwable -> {
            if (throwable != null) { //Print error if thrown from specific response.
              throwable.printStackTrace();
            }
          })
          .run(atomicInteger::incrementAndGet);
      }

      while (atomicInteger.get() < listClone.size()) {
        if (firstResponse.canceled()) {
          firstResponse.completeExceptionally(new InterruptedException("Already interrupted."));
          return;
        }
      }

      firstResponse.completeExceptionally(new NullPointerException("No response present."));
    });

    return firstResponse;
  }


  public static @NotNull <TYPE> CompletableResponse<TYPE> equal(@Nullable final List<CompletableResponse<TYPE>> list) {
    return null;
  }

}
