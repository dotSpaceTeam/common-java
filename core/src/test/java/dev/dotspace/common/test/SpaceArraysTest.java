package dev.dotspace.common.test;

import dev.dotspace.common.SpaceArrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link dev.dotspace.common.SpaceArrays}.
 */
public final class SpaceArraysTest {
  /**
   * Tests for {@link dev.dotspace.common.SpaceArrays#push(Object[], Object[])}
   */
  @Test
  public void pushTest() {
    Assertions.assertArrayEquals(new String[]{"This", "is", "a", "test."},
      SpaceArrays.push(new String[]{"This", "is", "a"}, "test."));
    Assertions.assertArrayEquals(new Integer[]{1, 2, 3, 4},
      SpaceArrays.push(new Integer[]{1, 2, 3}, 4));
  }
}
