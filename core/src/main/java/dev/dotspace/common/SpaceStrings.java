package dev.dotspace.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpaceStrings {
  /**
   * Plain string with no characters
   */
  private final static @NotNull String PLAIN;

  static {
    PLAIN = "";
  }

  /**
   * Plain string
   *
   * @return plain String ''
   */
  public static @NotNull String plain() {
    return PLAIN;
  }

  /**
   * Checks if string is null or {@link String#isBlank()}
   *
   * @param string to check if null or blank
   * @return true, if string is null or blank
   */
  public static boolean isNullOrBlank(@Nullable final String string) {
    return string == null || string.isBlank();
  }

  /**
   * Checks if string is null or {@link String#isEmpty()}
   *
   * @param string to check if null or empty
   * @return true, if string is null or empty
   */
  public static boolean isNullOrEmpty(@Nullable final String string) {
    return string == null || string.isEmpty();
  }
}
