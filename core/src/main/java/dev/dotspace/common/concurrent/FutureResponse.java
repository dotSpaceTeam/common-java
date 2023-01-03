package dev.dotspace.common.concurrent;

import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class depends on an instance of an {@link CompletableFuture}.
 *
 * @param <TYPE> generic type of response object.
 */
@Accessors(fluent = true)
public class FutureResponse<TYPE> {
  /**
   * {@link CompletableFuture} instance(reference) to use functions from.
   */
  private final CompletableFuture<TYPE> completableFuture;

  /**
   * Construct a new {@link FutureResponse} instance with an completableFuture.
   *
   * @param completableFuture to use as reference for {@link FutureResponse#completableFuture}.
   */
  public FutureResponse(@NotNull final CompletableFuture<TYPE> completableFuture) {
    this.completableFuture = completableFuture;
  }

  /**
   * Construct a new {@link FutureResponse} instance with a typeClass.
   * The typeClass is unused but this value defines the TYPE.
   * This constructor creates a new {@link CompletableFuture} instance.
   */
  public FutureResponse(@Nullable final Class<TYPE> typeClass) {
    this(new CompletableFuture<>());
  }

  /**
   * Construct a new {@link FutureResponse} instance no parameters.
   * This constructor creates a new {@link CompletableFuture} instance.
   * Generic type must be defined with this method.
   */
  public FutureResponse() {
    this(new CompletableFuture<>());
  }

  /**
   * Get the {@link CompletableFuture} reference and work with it.
   *
   * @return present {@link CompletableFuture}.
   */
  public @NotNull CompletableFuture<TYPE> future() {
    return this.completableFuture;
  }

  /**
   * Complete {@link CompletableFuture} of this instance.
   *
   * @param type to complete future with.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> complete(@Nullable final TYPE type) {
    this.completableFuture.complete(type);
    return this;
  }

  /**
   * Complete {@link CompletableFuture} of this instance async.
   *
   * @param typeSupplier to complete future with.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> completeAsync(@NotNull final Supplier<TYPE> typeSupplier) {
    this.completableFuture.completeAsync(typeSupplier);
    return this;
  }

  /**
   * Complete {@link CompletableFuture} of this instance async. This method give also the response instance itself,
   * so the response could be {@link FutureResponse#completeExceptionally(Throwable)}.
   *
   * @param function to complete future with.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> completeAsync(@NotNull final Function<FutureResponse<TYPE>, TYPE> function) {
    this.completableFuture.completeAsync(() -> function.apply(this));
    return this;
  }

  /**
   * Compose response.
   *
   * @param startValue to start composition.
   * @param consumer   consumer to edit composition.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> composeContent(@Nullable final TYPE startValue,
                                                      @NotNull final Consumer<ResponseContent<TYPE>> consumer) {
    this.completableFuture.complete(this.composeContentImplementation(startValue, consumer));
    return this;
  }

  /**
   * Construct {@link CompletableFuture#complete(Object)}.
   *
   * @param consumer to apply {@link ResponseContent}.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> composeContent(@NotNull final Consumer<ResponseContent<TYPE>> consumer) {
    return this.composeContent(null, consumer);
  }

  /**
   * Compose resonse asnyc.
   *
   * @param startValue to start composition.
   * @param consumer   consumer to edit composition.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> composeContentAsync(@Nullable final TYPE startValue,
                                                           @NotNull final Consumer<ResponseContent<TYPE>> consumer) {
    this.completableFuture.completeAsync(() -> this.composeContentImplementation(startValue, consumer));
    return this;
  }

  /**
   * Construct {@link CompletableFuture#completeAsync(Supplier)}.
   *
   * @param consumer to apply {@link ResponseContent}.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> composeContentAsync(@NotNull final Consumer<ResponseContent<TYPE>> consumer) {
    return this.composeContentAsync(null, consumer);
  }

  /**
   * Implementation for {@link FutureResponse#composeContent(Consumer)} and {@link FutureResponse#composeContentAsync(Consumer)}.
   *
   * @param consumer to apply {@link ResponseContent}.
   * @return response after {@link Consumer}.
   */
  private @Nullable TYPE composeContentImplementation(@Nullable final TYPE startValue,
                                                      @NotNull final Consumer<ResponseContent<TYPE>> consumer) {
    final ResponseContent<TYPE> responseContent = new ResponseContent<>(startValue);
    if (consumer != null) {
      consumer.accept(responseContent);
    }
    Optional.ofNullable(responseContent.throwable()).ifPresent(this::completeExceptionally); //Complete with throwable if ResponseContent has throwable.
    return responseContent.content();
  }

  /**
   * Do not use!
   */
  @Deprecated
  public @NotNull FutureResponse<TYPE> completeAsync(@NotNull final Consumer<ResponseContent<TYPE>> consumer) {
    return this.composeContentAsync(consumer);
  }

  /**
   * Complete {@link CompletableFuture} with throwable.
   *
   * @param throwable to complete future with.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> completeExceptionally(@NotNull final Throwable throwable) {
    this.completableFuture.completeExceptionally(throwable);
    return this;
  }

  /**
   * Get the Response of the {@link CompletableFuture}.
   * Further information about the {@link ExecutionException} and {@link InterruptedException} see {@link CompletableFuture#get()}.
   */
  public @NotNull Optional<@Nullable TYPE> get() throws ExecutionException, InterruptedException {
    return Optional.ofNullable(this.completableFuture.get());
  }

  /**
   * Get the Response of the {@link CompletableFuture}.
   *
   * @param absentSupplier value to set as return if absent.
   */
  public @NotNull TYPE getNow(@NotNull final Supplier<TYPE> absentSupplier) {
    return this.completableFuture.getNow(absentSupplier.get());
  }

  /**
   * Consume completed value if present.
   *
   * @param typeConsumer to fill if value is present on {@link CompletableFuture#thenAccept(Consumer)}.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> ifPresent(@NotNull final Consumer<TYPE> typeConsumer) {
    this.completableFuture.thenAccept(type -> this.presentImplementation(type, typeConsumer));
    return this;
  }

  /**
   * Consume completed async value if present.
   *
   * @param typeConsumer to fill if value is present on {@link CompletableFuture#thenAcceptAsync(Consumer)}.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> ifPresentAsync(@NotNull final Consumer<TYPE> typeConsumer) {
    this.completableFuture.thenAcceptAsync(type -> this.presentImplementation(type, typeConsumer));
    return this;
  }

  /**
   * Implementation for {@link FutureResponse#ifPresent(Consumer)} and {@link FutureResponse#ifPresentAsync(Consumer)}.
   *
   * @param type     to check if present.
   * @param consumer to fill with type if it is present.
   */
  private void presentImplementation(@Nullable final TYPE type,
                                     @NotNull final Consumer<TYPE> consumer) {
    if (type != null && consumer != null) {
      consumer.accept(type);
    }
  }

  /**
   * Map this response to another type. (With a {@link Function}).
   * This function creates a new {@link FutureResponse} while mapping. (Response is another instance than this one).
   *
   * @param function to map the response.
   * @param <MAP>    generic type of new response.
   * @return mapped response.
   */
  public @NotNull <MAP> FutureResponse<MAP> map(@NotNull final Function<? super TYPE, ? extends MAP> function) {
    return new FutureResponse<>(this.completableFuture.thenApply(function));
  }

  /**
   * Map this response async to another type. (With a {@link Function}).
   * This function creates a new {@link FutureResponse} while mapping. (Response is another instance than this one).
   *
   * @param function to map the response.
   * @param <MAP>    generic type of new response.
   * @return mapped response.
   */
  public @NotNull <MAP> FutureResponse<MAP> mapAsync(@NotNull final Function<? super TYPE, ? extends MAP> function) {
    return new FutureResponse<>(this.completableFuture.thenApplyAsync(function));
  }

  /**
   * Execute runnable, if {@link CompletableFuture} was complete but value is null.
   *
   * @param runnable to run if null is complete.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> ifAbsent(@NotNull final Runnable runnable) {
    this.completableFuture.thenAccept(type -> this.absentImplementation(type, runnable));
    return this;
  }

  /**
   * Execute runnable async, if {@link CompletableFuture} was complete but value is null.
   *
   * @param runnable to run if null is complete.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> ifAbsentAsync(@NotNull final Runnable runnable) {
    this.completableFuture.thenAcceptAsync(type -> this.absentImplementation(type, runnable));
    return this;
  }

  /**
   * Implementation for {@link FutureResponse#ifAbsent(Runnable)} and {@link FutureResponse#ifAbsent(Runnable)}.
   *
   * @param type     to check if null.
   * @param runnable to run if type is null.
   */
  private void absentImplementation(@Nullable final TYPE type,
                                    @NotNull final Runnable runnable) {
    if (type == null && runnable != null) {
      runnable.run();
    }
  }

  /**
   * Consume throwable if {@link CompletableFuture#completeExceptionally(Throwable)} was called.
   *
   * @param throwableConsumer to accept the throwable of completion.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> ifExceptionally(@NotNull final Consumer<Throwable> throwableConsumer) {
    this.completableFuture.handle((type, throwable) -> {
      this.exceptionallyImplementation(throwable, throwableConsumer);
      return null;
    });
    return this;
  }

  /**
   * Consume throwable async if {@link CompletableFuture#completeExceptionally(Throwable)} was called.
   *
   * @param throwableConsumer to accept the throwable of completion.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> ifExceptionallyAsync(@NotNull final Consumer<Throwable> throwableConsumer) {
    this.completableFuture.handleAsync((type, throwable) -> {
      this.exceptionallyImplementation(throwable, throwableConsumer);
      return null;
    });
    return this;
  }

  /**
   * Implementation for {@link FutureResponse#ifExceptionally(Consumer)} and {@link FutureResponse#ifExceptionallyAsync(Consumer)}.
   *
   * @param throwable         to check if present.
   * @param throwableConsumer to fill if throwable is present.
   */
  private void exceptionallyImplementation(@Nullable final Throwable throwable,
                                           @NotNull final Consumer<Throwable> throwableConsumer) {
    if (throwable != null && throwableConsumer != null) {
      throwableConsumer.accept(throwable);
    }
  }

  /**
   * Run runnable if {@link CompletableFuture#complete(Object)} was completed with null or
   * consume throwable if {@link CompletableFuture#completeExceptionally(Throwable)}.
   *
   * @param runnable          to execute if type is null.
   * @param throwableConsumer to consume if throwable is present.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> ifAbsentOrExceptionally(@NotNull final Runnable runnable,
                                                               @NotNull final Consumer<Throwable> throwableConsumer) {
    this.completableFuture.handle((type, throwable) -> {
      this.absentOrExceptionallyImplementation(type, throwable, runnable, throwableConsumer);
      return null;
    });
    return this;
  }

  /**
   * Run runnable async if {@link CompletableFuture#complete(Object)} was completed with null or
   * consume throwable async if {@link CompletableFuture#completeExceptionally(Throwable)}.
   *
   * @param runnable          to execute if type is null.
   * @param throwableConsumer to consume if throwable is present.
   * @return class instance.
   */
  public @NotNull FutureResponse<TYPE> ifAbsentOrExceptionallyAsync(@NotNull final Runnable runnable,
                                                                    @NotNull final Consumer<Throwable> throwableConsumer) {
    this.completableFuture.handleAsync((type, throwable) -> {
      this.absentOrExceptionallyImplementation(type, throwable, runnable, throwableConsumer);
      return null;
    });
    return this;
  }

  /**
   * Implementation for {@link FutureResponse#ifAbsentOrExceptionally(Runnable, Consumer)} and {@link FutureResponse#ifAbsentOrExceptionallyAsync(Runnable, Consumer)}.
   *
   * @param type              to check if null.
   * @param throwable         to check if present.
   * @param runnable          to run if type is absent.
   * @param throwableConsumer to consume if throwable is present.
   */
  private void absentOrExceptionallyImplementation(@Nullable final TYPE type,
                                                   @Nullable final Throwable throwable,
                                                   @NotNull final Runnable runnable,
                                                   @NotNull final Consumer<Throwable> throwableConsumer) {
    if (type == null && runnable != null) {
      runnable.run();
    }
    if (throwable != null && throwableConsumer != null) {
      throwableConsumer.accept(throwable);
    }
  }
}