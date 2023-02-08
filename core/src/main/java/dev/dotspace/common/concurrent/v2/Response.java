package dev.dotspace.common.concurrent.v2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Our way for {@link java.util.concurrent.Future}.
 *
 * @param <TYPE> type to be processed by the response.
 */
public interface Response<TYPE> {
  /**
   * Waits for the response. Interrupts the thread in which this method is executed until the response or an error.
   *
   * @return if available the response otherwise null.
   * @throws InterruptedException if the process is interrupted.
   */
  @Nullable TYPE get() throws InterruptedException;

  /**
   * Wait a specified time for a response. Suspends the thread in which this method is executed until the response,
   * an error, or the reached timestamp.
   *
   * @param nanos after this time, the wait should be canceled.
   * @return if available the response otherwise null.
   * @throws InterruptedException if the process is interrupted.
   */
  @Nullable TYPE get(final long nanos) throws InterruptedException;

  /**
   * This method calls {@link Response#get(long)}.
   * The time for the method is calculated with {@link TimeUnit#toNanos(long)}.
   *
   * @param duration time to be calculated with timeUnit.
   * @param timeUnit sets the unit of the specified duration.
   * @return if available the response otherwise null.
   * @throws InterruptedException if the process is interrupted.
   * @throws NullPointerException if timeUnit is null.
   */
  @Nullable TYPE get(final long duration,
                     @Nullable final TimeUnit timeUnit) throws InterruptedException;

  /**
   * Gets the value that is currently set as the response.
   * With alternativeValue an alternative value for the response can be given.
   * If alternativeValue and the response are null, null is also returned.
   *
   * @param alternativeValue contains the alternative value.
   * @return if available the response otherwise zero or the response of the alternative.
   */
  @Nullable TYPE getNow(@Nullable Supplier<TYPE> alternativeValue);

  /**
   * Cancels the response and sets all values to zero.
   *
   * @return true if the process was successfully aborted.
   */
  boolean cancel();

  /**
   * Complete the response with a value or null.
   *
   * @param type with which the response is to be completed.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> complete(@Nullable final TYPE type);

  /**
   * Complete the response in another thread to relieve the computational capacity of the main thread.
   *
   * @param typeSupplier with the supplier the response will be completed asynchronously.
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> completeAsync(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * Completes the response with an error.
   *
   * @param throwable specifies the error to be used for completion.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> completeExceptionally(@Nullable final Throwable throwable);

  /**
   * Complete the response with an error in another thread to relieve the computational capacity of the main thread.
   *
   * @param throwableSupplier
   * @return instance of this response.
   * @throws NullPointerException if throwableSupplier is null.
   */
  @NotNull Response<TYPE> completeExceptionally(@Nullable final Supplier<Throwable> throwableSupplier);

  /**
   * @param responseConsumer
   * @return instance of this response.
   * @throws NullPointerException if responseConsumer is null.
   */
  @NotNull Response<TYPE> peak(@Nullable final ResponseConsumer<TYPE> responseConsumer);

  /**
   * @param responseConsumer
   * @return instance of this response.
   * @throws NullPointerException if responseConsumer is null.
   */
  @NotNull Response<TYPE> peakAsync(@Nullable final ResponseConsumer<TYPE> responseConsumer);

  /**
   * @param runnable
   * @return instance of this response.
   */
  @NotNull Response<TYPE> run(@Nullable final Runnable runnable);

  /**
   * @param runnable
   * @return instance of this response.
   */
  @NotNull Response<TYPE> runAsync(@Nullable final Runnable runnable);

  /**
   * @param consumer
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifPresent(@Nullable final Consumer<@NotNull TYPE> consumer);

  /**
   * @param consumer
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifPresentAsync(@Nullable final Consumer<@NotNull TYPE> consumer);

  /**
   * @param function
   * @param <MAP_TYPE>
   * @return new instance created by the map method.
   */
  @NotNull <MAP_TYPE> Response<MAP_TYPE> ifPresentMap(@Nullable final Function<TYPE, MAP_TYPE> function);

  /**
   * @param function
   * @param <MAP_TYPE>
   * @return new instance created by the map method.
   */
  @NotNull <MAP_TYPE> Response<MAP_TYPE> ifPresentMapAsync(@Nullable final Function<TYPE, MAP_TYPE> function);

  /**
   * @param runnable
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifAbsent(@Nullable final Runnable runnable);

  /**
   * @param runnable
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifAbsentAsync(@Nullable final Runnable runnable);

  /**
   * @param consumer
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifExceptionally(@Nullable final Consumer<@Nullable Throwable> consumer);

  /**
   * @param consumer
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifExceptionallyAsync(@Nullable final Consumer<@Nullable Throwable> consumer);

  /**
   * @param typeSupplier
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> ifAbsentUse(@Nullable final Supplier<TYPE> typeSupplier);


  /**
   * @param typeSupplier
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> ifAbsentUseAsync(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * @param typeSupplier
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> ifExceptionallyUse(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * @param typeSupplier
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> ifExceptionallyUseAsync(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * @param typeSupplier
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> ifNotPresentUse(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * @param typeSupplier
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> ifNotPresentUseAsync(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * @return
   */
  boolean done();

  /**
   * @return
   */
  boolean canceled();

  /**
   * @return
   */
  boolean exceptionally();
}