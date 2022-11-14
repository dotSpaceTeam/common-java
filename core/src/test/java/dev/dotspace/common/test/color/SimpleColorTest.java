package dev.dotspace.common.test.color;

import dev.dotspace.common.color.SimpleColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleColorTest {

  /**
   * Check {@link SimpleColor#validateColorValue(int)} with different values
   */
  @Test
  public void testColorValidation() {
    Assertions.assertDoesNotThrow(() -> SimpleColor.validateColorValue(0));
    Assertions.assertDoesNotThrow(() -> SimpleColor.validateColorValue(255));

    Assertions.assertThrows(IllegalStateException.class, () -> SimpleColor.validateColorValue(-1));
    Assertions.assertThrows(IllegalStateException.class, () -> SimpleColor.validateColorValue(256));
  }
}
