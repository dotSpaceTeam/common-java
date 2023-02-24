package dev.dotspace.common;

import dev.dotspace.annotation.LibraryInformation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
public final class SpaceStrings {
  /**
   * Plain string with no characters
   */
  private final static @NotNull String PLAIN;

  static {
    PLAIN = ""; //Define plain string.
  }

  /**
   * Plain string
   *
   * @return plain String ''
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull String plain() {
    return PLAIN;
  }

  /**
   * Checks if string is null or {@link String#isBlank()}
   *
   * @param string to check if null or blank
   * @return true, if string is null or blank
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static boolean isNullOrBlank(@Nullable final String string) {
    return string == null || string.isBlank();
  }

  /**
   * Checks if string is null or {@link String#isEmpty()}
   *
   * @param string to check if null or empty
   * @return true, if string is null or empty
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static boolean isNullOrEmpty(@Nullable final String string) {
    return string == null || string.isEmpty();
  }
}
