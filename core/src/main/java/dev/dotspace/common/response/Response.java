package dev.dotspace.common.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Our way for {@link java.util.concurrent.Future}.
 *
 * @param <TYPE> type to be processed by the response.
 */
public interface Response<TYPE> {
  /**
   * Creates a new uncompleted {@link Response} with the same type.
   *
   * @return the newly created {@link Response}.
   */
  @NotNull Response<TYPE> newUncompleted();

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
   * @param throwableSupplier requests the error (Child of {@link Throwable}).
   * @return instance of this response.
   * @throws NullPointerException if throwableSupplier is null.
   */
  @NotNull Response<TYPE> completeExceptionallyAsync(@Nullable final Supplier<Throwable> throwableSupplier);

  /**
   * With this method, information can be tapped.
   * The {@link ResponseConsumer} of this method is also filled during completion.
   * This method does not affect the parameters of the response.
   *
   * @param responseConsumer is filled with the information and values when completing.
   * @return instance of this response.
   * @throws NullPointerException if responseConsumer is null.
   */
  @NotNull Response<TYPE> sniff(@Nullable final ResponseConsumer<TYPE> responseConsumer);

  /**
   * This method can be used to retrieve information asynchronously.
   * The {@link ResponseConsumer} of this method is also filled during completion.
   * This method does not affect the parameters of the response.
   *
   * @param responseConsumer is filled with the information and values when completing.
   * @return instance of this response.
   * @throws NullPointerException if responseConsumer is null.
   */
  @NotNull Response<TYPE> sniffAsync(@Nullable final ResponseConsumer<TYPE> responseConsumer);

  /**
   * Executes the {@link Runnable} if the answer is completed in any way.
   *
   * @param runnable which is to be executed.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> run(@Nullable final Runnable runnable);

  /**
   * Executes the {@link Runnable} asynchronously if the answer is completed in any way.
   *
   * @param runnable which is to be executed asynchronously.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> runAsync(@Nullable final Runnable runnable);

  /**
   * Passes the response in the {@link Consumer} if it exists and is not null.
   *
   * @param consumer is filled with the response.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifPresent(@Nullable final Consumer<@NotNull TYPE> consumer);

  /**
   * Passes the response asynchronously in the {@link Consumer} if it is present and not null.
   *
   * @param consumer is filled with the response.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifPresentAsync(@Nullable final Consumer<@NotNull TYPE> consumer);

  /**
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param function is invoked to transform the response.
   * @param <MAP>    the type into which the response should be converted.
   * @return new instance created by the map method.
   */
  @NotNull <MAP> Response<MAP> map(@Nullable final Function<TYPE, MAP> function);

  /**
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param function is invoked to transform the response.
   * @param <MAP>    the type into which the response should be converted.
   * @return new instance created by the map method.
   */
  @NotNull <MAP> Response<MAP> mapAsync(@Nullable final Function<TYPE, MAP> function);

  /**
   * Filter the {@link Response} if it is present and not null.
   * If typePredict is null, the new {@link Response} is completed with an error.
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param typePredicate checks if the answer is kept ({@link Predicate#test(Object)} -> true).
   * @return new instance of {@link Response} created with the filtered value.
   */
  @NotNull Response<TYPE> filter(@Nullable final Predicate<TYPE> typePredicate);

  /**
   * Filter the {@link Response} asynchronously if it is present and not null.
   * If typePredict is null, the new {@link Response} is completed with an error.
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param typePredicate checks if the answer is kept ({@link Predicate#test(Object)} -> true).
   * @return new instance of {@link Response} created with the filtered value.
   */
  @NotNull Response<TYPE> filterAsync(@Nullable final Predicate<TYPE> typePredicate);

  /**
   * Executes the {@link Runnable} if the answer is completed with null.
   *
   * @param runnable which is to be executed.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifAbsent(@Nullable final Runnable runnable);

  /**
   * Executes the {@link Runnable} asynchronously if the answer is completed with null.
   *
   * @param runnable which is to be executed.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifAbsentAsync(@Nullable final Runnable runnable);

  /**
   * Gives the {@link Throwable} that completed the answer. The {@link Throwable} can also be null.
   *
   * @param consumer will be completed with the error.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifExceptionally(@Nullable final Consumer<@Nullable Throwable> consumer);

  /**
   * Returns the {@link Throwable} asynchronously that completed the answer. The {@link Throwable} can also be null.
   *
   * @param consumer will be completed with the error.
   * @return instance of this response.
   */
  @NotNull Response<TYPE> ifExceptionallyAsync(@Nullable final Consumer<@Nullable Throwable> consumer);

  /**
   * Use the {@link Supplier}'s object if the {@link Response} was completed with null.
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param typeSupplier response to use if completed with null.
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> useIfAbsent(@Nullable final Supplier<TYPE> typeSupplier);


  /**
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param typeSupplier response to use if completed with null.
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> useIfAbsentAsync(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param typeSupplier response to use if completed with an error.
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> useIfExceptionally(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param typeSupplier response to use if completed with an error.
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> useIfExceptionallyAsync(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param typeSupplier response to use if completed with null or an error.
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> elseUse(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * <br>
   * <b>This method creates a new instance in response.
   * Since asynchronous methods are also used, the application on this instance is not possible.</b>
   * <br>
   *
   * @param typeSupplier response to use if completed with null or an error.
   * @return instance of this response.
   * @throws NullPointerException if typeSupplier is null.
   */
  @NotNull Response<TYPE> elseUseAsync(@Nullable final Supplier<TYPE> typeSupplier);

  /**
   * Check if the {@link Response} has been completed in any way.
   *
   * @return true, if completed. ({@link State} is not {@link State#UNCOMPLETED})
   */
  boolean done();

  /**
   * Check if the {@link Response} has canceled.
   *
   * @return true, if cancelled. ({@link State} is {@link State#CANCELLED})
   */
  boolean canceled();

  /**
   * Check if the {@link Response} has been completed exceptionally.
   *
   * @return true if, exceptionally. ({@link State} is {@link State#COMPLETED_EXCEPTIONALLY})
   */
  boolean exceptionally();
}