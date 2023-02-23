package dev.dotspace.common.test;

import dev.dotspace.common.SpaceObjects;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test {@link dev.dotspace.common.SpaceObjects}.
 */
public final class SpaceObjectsTest {
  private final static @NotNull String TEST_STRING = "test";

  @Test
  public void ifAbsentUseTest() {
    Assertions.assertEquals(SpaceObjects.ifAbsentUse(TEST_STRING, null), TEST_STRING);

    Assertions.assertThrowsExactly(NullPointerException.class,
      () -> SpaceObjects.ifAbsentUse(null, null),
      "Supplier is null!");


    Assertions.assertThrowsExactly(NullPointerException.class,
      () -> SpaceObjects.ifAbsentUse(null, () -> null),
      "Supplied object for absent value is null.");

    Assertions.assertEquals(SpaceObjects.ifAbsentUse(null, () -> TEST_STRING), TEST_STRING);
  }

}
