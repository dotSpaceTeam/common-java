package dev.dotspace.common.test;

import dev.dotspace.common.SpaceObjects;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

/**
 * Test {@link dev.dotspace.common.SpaceObjects}.
 */
public final class SpaceObjectsTest {
  private final static @NotNull String TEST_STRING = "test";
  private final static @NotNull String NULL_MESSAGE = "Object is null!";

  /**
   * Test {@link SpaceObjects#throwIfNull(Object, String)}.
   */
  @Test
  public void throwIfNullMessageTest() {
    Assertions.assertDoesNotThrow(() -> SpaceObjects.throwIfNull(TEST_STRING, NULL_MESSAGE));
    Assertions.assertDoesNotThrow(() -> SpaceObjects.throwIfNull(TEST_STRING, (String) null));

    Assertions.assertThrows(NullPointerException.class, () -> SpaceObjects.throwIfNull(null, NULL_MESSAGE));
    Assertions.assertThrows(NullPointerException.class, () -> SpaceObjects.throwIfNull(null, (String) null));
  }

  /**
   * Test {@link SpaceObjects#throwIfNull(Object)}.
   */
  @Test
  public void throwIfNull() {
    Assertions.assertDoesNotThrow(() -> SpaceObjects.throwIfNull(TEST_STRING));
    Assertions.assertThrows(NullPointerException.class, () -> SpaceObjects.throwIfNull(null));
  }

  /**
   * Test {@link SpaceObjects#throwIfNull(Object, Supplier)}.
   */
  @Test
  public void throwIfNullThrowable() {
    Assertions.assertDoesNotThrow(() -> SpaceObjects.throwIfNull(TEST_STRING, (Supplier<? extends Throwable>) null));

    Assertions.assertThrows(NullPointerException.class, () -> SpaceObjects.throwIfNull(null, (Supplier<? extends Throwable>) null));
    Assertions.assertThrows(NullPointerException.class, () -> SpaceObjects.throwIfNull(null, () -> null));

    Assertions.assertThrows(Throwable.class, () -> SpaceObjects.throwIfNull(null, Throwable::new));
    Assertions.assertThrows(IllegalArgumentException.class, () -> SpaceObjects.throwIfNull(null, IllegalArgumentException::new));
    Assertions.assertThrows(ClassNotFoundException.class, () -> SpaceObjects.throwIfNull(null, ClassNotFoundException::new));
    Assertions.assertThrows(Exception.class, () -> SpaceObjects.throwIfNull(null, Exception::new));
  }

  /**
   * Test {@link SpaceObjects#ifAbsentUse(Object, Supplier)}.
   */
  @Test
  public void ifAbsentUseTest() {
    Assertions.assertEquals(SpaceObjects.ifAbsentUse(TEST_STRING, null), TEST_STRING);

    Assertions.assertThrowsExactly(NullPointerException.class, () -> SpaceObjects.ifAbsentUse(null, null), "Supplier is null!");
    Assertions.assertThrowsExactly(NullPointerException.class, () -> SpaceObjects.ifAbsentUse(null, () -> null), "Supplied object for absent value is null.");

    Assertions.assertEquals(SpaceObjects.ifAbsentUse(null, () -> TEST_STRING), TEST_STRING);
  }

}
