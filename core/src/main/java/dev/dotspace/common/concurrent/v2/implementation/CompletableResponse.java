package dev.dotspace.common.concurrent.v2.implementation;

import dev.dotspace.common.SpaceObjects;
import dev.dotspace.common.concurrent.v2.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CompletableResponse<TYPE> implements Response<TYPE> {
  private final @NotNull ExecutorService executorService;

  private volatile @NotNull State state;

  private volatile @Nullable TYPE response;
  private volatile @Nullable Throwable throwable;

  private final List<Executor<?>> executors;

  public CompletableResponse() {
    this(State.UNCOMPLETED);
  }

  private CompletableResponse(@NotNull final State state) {
    this.executorService = Executors.newCachedThreadPool();
    this.state = state;
    this.executors = new ArrayList<>();
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
  public @NotNull CompletableResponse<TYPE> peak(@Nullable ResponseConsumer<TYPE> responseConsumer) {
    this.peakImplementation(responseConsumer, false);
    return this;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> peakAsync(@Nullable ResponseConsumer<TYPE> responseConsumer) {
    this.peakImplementation(responseConsumer, true);
    return this;
  }

  private void peakImplementation(@Nullable final ResponseConsumer<TYPE> responseConsumer,
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

  @Override
  public @NotNull Response<TYPE> run(@Nullable Runnable runnable) {
    this.runImplementation(runnable, false);
    return this;
  }

  @Override
  public @NotNull Response<TYPE> runAsync(@Nullable Runnable runnable) {
    this.executorService.execute(() -> this.runImplementation(runnable, true));
    return this;
  }

  private void runImplementation(@Nullable final Runnable runnable,
                                 final boolean async) {
    if (runnable == null) {
      return;
    }

    this.implementExecutor(new ResponseExecutor<>(() -> true, () -> {
      try {
        runnable.run();
      } catch (final Throwable throwable) {
        throwable.printStackTrace();
      }
    }, async));
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifPresent(@Nullable Consumer<@NotNull TYPE> consumer) {
    this.ifPresentImplementation(consumer, false);
    return this;
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifPresentAsync(@Nullable Consumer<@NotNull TYPE> consumer) {
    this.ifPresentImplementation(consumer, true);
    return this;
  }

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
  public @NotNull <MAP_TYPE> CompletableResponse<MAP_TYPE> ifPresentMap(@Nullable Function<TYPE, MAP_TYPE> function) {
    return this.ifPresentMapImplementation(function, false);
  }

  @Override
  public @NotNull <MAP_TYPE> CompletableResponse<MAP_TYPE> ifPresentMapAsync(@Nullable Function<TYPE, MAP_TYPE> function) {
    return this.ifPresentMapImplementation(function, true);
  }

  private <MAP_TYPE> CompletableResponse<MAP_TYPE> ifPresentMapImplementation(@Nullable Function<TYPE, MAP_TYPE> function,
                                                                              final boolean async) {
    final CompletableResponse<MAP_TYPE> completableResponse = new CompletableResponse<>();
    this.implementExecutor(new ResponseExecutor<>(() -> this.response != null && this.state == State.COMPLETED_DEFAULT, () -> {
      try {
        completableResponse.complete(SpaceObjects.throwIfNull(function).apply(this.response));
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
  public @NotNull CompletableResponse<TYPE> ifAbsentUse(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, false);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifAbsentUseAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, true);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifExceptionallyUse(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, false);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifExceptionallyUseAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, true);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifNotPresentUse(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, false);
  }

  @Override
  public @NotNull CompletableResponse<TYPE> ifNotPresentUseAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, true);
  }

  private @NotNull CompletableResponse<TYPE> useImplementation(@Nullable final Supplier<TYPE> typeSupplier,
                                                               final boolean async) {
    final CompletableResponse<TYPE> completableResponse = new CompletableResponse<>();
    this.implementExecutor(new ResponseExecutor<>(() -> this.response == null && (this.state == State.COMPLETED_NULL || this.state == State.COMPLETED_EXCEPTIONALLY),
      () -> {
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

  @Override
  public boolean done() {
    return this.state.done();
  }

  @Override
  public boolean canceled() {
    return this.state == State.CANCELLED;
  }

  @Override
  public boolean exceptionally() {
    return this.state == State.COMPLETED_EXCEPTIONALLY;
  }

  private void implementExecutor(@NotNull final Executor<?> executor) {
    if (this.done()) { //Directly run executor if already finished.
      executor.run(this.executorService);
    } else { //Add to run later if response is completed.
      this.executors.add(executor);
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
        typeCompletableResponse.peakAsync((state, type, throwable) -> {
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
          throw new RuntimeException("Already interrupted.");
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
