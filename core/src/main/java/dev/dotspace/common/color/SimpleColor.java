package dev.dotspace.common.color;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @param red   value of red
 * @param green value of green
 * @param blue  value of blue
 */
public record SimpleColor(short red,
                          short green,
                          short blue) {
  private final static int MIN_VALUE;
  private final static int MAX_VALUE;

  static {
    MIN_VALUE = 0x0;
    MAX_VALUE = 0xFF;
  }

  /**
   * Get value of color in one integer
   *
   * @return value of color as one integer.
   */
  public int value() {
    return ((this.red() & 0xFF) << 16) | ((this.green() & 0xFF) << 8) | ((this.blue() & 0xFF));
  }

  /**
   * Create hex string from {@link SimpleColor#value()}
   *
   * @return value as hex string
   */
  public @NotNull String hex() {
    return String.format("#%06x", this.value());
  }

  /**
   * Create {@link Color} from red, green, blue
   *
   * @return locale values as {@link Color} object
   */
  public @NotNull Color awtColor() {
    return new Color(this.red, this.green, this.blue);
  }

  /**
   * Map color into another color
   *
   * @param function to convert local color values to another {@link Color}
   * @param <COLOR>  generic type of color
   * @return converted COLOR
   */
  public <COLOR> @NotNull COLOR map(@NotNull final ColorFunction<COLOR> function) {
    return function.apply(this.red, this.green, this.blue);
  }

  //static

  /**
   * Create new color
   *
   * @param red   value of red
   * @param green value of green
   * @param blue  value of blue
   * @return created {@link SimpleColor} from values of red, green and blue
   */
  public static @NotNull SimpleColor create(final int red,
                                            final int green,
                                            final int blue) {
    return new SimpleColor((short) SimpleColor.validateColorValue(red),
      (short) SimpleColor.validateColorValue(green),
      (short) SimpleColor.validateColorValue(blue));
  }

  /**
   * Check if color value is inside range
   *
   * @param value to validate
   * @return value if inside range
   * @throws IllegalStateException if color is outside the range {@link SimpleColor#MIN_VALUE} and {@link SimpleColor#MAX_VALUE}
   */
  public static int validateColorValue(final int value) {
    if (value < MIN_VALUE || value > MAX_VALUE) {
      throw new IllegalStateException("Value for one color can be between " + MIN_VALUE + " and " + MAX_VALUE + ". Set: " + value);
    }
    return value;
  }

  /**
   * Consumer map red, green and blue to color
   *
   * @param <COLOR> generic type of color to map values to
   */
  interface ColorFunction<COLOR> {
    @NotNull COLOR apply(final int red,
                         final int green,
                         final int blue);
  }
}
