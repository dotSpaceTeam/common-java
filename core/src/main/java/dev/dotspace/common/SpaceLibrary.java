package dev.dotspace.common;

import dev.dotspace.annotation.SpaceApi;
import dev.dotspace.common.response.CompletableResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SpaceApi(state = SpaceApi.State.WORK_IN_PROGRESS, since = "1.0.6", updated = "1.0.6")
public final class SpaceLibrary {
  /**
   * Create a new {@link CompletableResponse} instance.
   *
   * @param <TYPE> type to use for response.
   * @return new instance of {@link CompletableResponse}.
   */
  public static <TYPE> @NotNull CompletableResponse<TYPE> response() {
    return new CompletableResponse<>();
  }

  /**
   * @param typeClass
   * @param <TYPE>
   * @return
   */
  public static <TYPE> @NotNull CompletableResponse<TYPE> response(@Nullable final Class<TYPE> typeClass) {
    return new CompletableResponse<>(typeClass);
  }

  /**
   * @param type
   * @param <TYPE>
   * @return
   */
  public static <TYPE> @NotNull CompletableResponse<TYPE> completeResponse(@Nullable TYPE type) {
    return new CompletableResponse<TYPE>().complete(type);
  }

  /**
   * @param typeSupplier
   * @param <TYPE>
   * @return
   * @throws NullPointerException if typeSupplier is null.
   */
  public static <TYPE> @NotNull CompletableResponse<TYPE> completeResponseAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return new CompletableResponse<TYPE>().completeAsync(typeSupplier);
  }
}
