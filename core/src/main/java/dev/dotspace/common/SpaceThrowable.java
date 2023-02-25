package dev.dotspace.common;

import dev.dotspace.common.annotation.LibraryInformation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
public final class SpaceThrowable {
  /**
   * Print stacktrace of {@link Throwable} if present.
   * Method checks if {@link Throwable} is not null, if so {@link Throwable#printStackTrace()}.
   *
   * @param throwable to print stacktrace if present.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static void printStackTrace(@Nullable final Throwable throwable) {
    if (throwable != null) { //Online print stack trace if throwable present.
      throwable.printStackTrace();
    }
  }
}
