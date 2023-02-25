package dev.dotspace.common;

import dev.dotspace.common.annotation.LibraryInformation;
import dev.dotspace.common.response.CompletableResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Methods to create the individual possible objects of this library.
 * <dl>
 *   <dt>{@link CompletableResponse}</dt>
 *   <dd>Similar to {@link java.util.concurrent.CompletableFuture} but implemented our way.</dd>
 * </dl>
 */
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE) //Block default constructor.
public final class SpaceLibrary {
  /**
   * Create a new {@link CompletableResponse} instance.
   *
   * @param <TYPE> type to use for response.
   * @return new instance of {@link CompletableResponse}.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static <TYPE> @NotNull CompletableResponse<TYPE> response() {
    return new CompletableResponse<>();
  }

  /**
   * Create a new {@link CompletableResponse} instance. Class is used to define type.
   *
   * @param typeClass @param typeClass defines the TYPE of the instance.
   * @param <TYPE>    type to use for response.
   * @return new instance of {@link CompletableResponse}.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static <TYPE> @NotNull CompletableResponse<TYPE> response(@Nullable final Class<TYPE> typeClass) {
    return new CompletableResponse<>(typeClass);
  }

  /**
   * Create response and complete.
   *
   * @param type   to complete response with.
   * @param <TYPE> type to use for response.
   * @return new instance of {@link CompletableResponse}.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static <TYPE> @NotNull CompletableResponse<TYPE> completeResponse(@Nullable TYPE type) {
    return new CompletableResponse<TYPE>().complete(type);
  }

  /**
   * Create response and complete asynchronous.
   *
   * @param typeSupplier to complete asynchronous.
   * @param <TYPE>       type to use for response.
   * @return new instance of {@link CompletableResponse}.
   * @throws NullPointerException if typeSupplier is null.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static <TYPE> @NotNull CompletableResponse<TYPE> completeResponseAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return new CompletableResponse<TYPE>().completeAsync(typeSupplier);
  }
}
