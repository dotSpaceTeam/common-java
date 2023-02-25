package dev.dotspace.common.test;

import dev.dotspace.common.SpaceCollections;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * Test class {@link SpaceCollections}.
 */
public class SpaceCollectionsTest {
  private final static @NotNull Set<Integer> NUMBERS_LIST = new HashSet<>();

  @BeforeAll
  public static void setup() {
    for (int i = 0; i < 10; i++) {
      NUMBERS_LIST.add(i);
    }
  }

  /**
   * Test default, with numbers and get a random number
   */
  @Test
  public void testRandom() {
    Assertions.assertNotNull(SpaceCollections.random(NUMBERS_LIST));
  }

  /**
   * Test with empty list -> {@link Optional#empty()} -> null
   */
  @Test
  public void testRandomEmpty() {
    Assertions.assertNull(SpaceCollections.random(new ArrayList<>()));
  }

  /**
   * Test with null -> {@link Optional#empty()} -> null
   */
  @Test
  public void testRandomNull() {
    Assertions.assertNull(SpaceCollections.random(null));
  }


  /**
   * Test async default, with numbers and get a random number
   */
  @Test
  public void testAsyncRandom() {
    SpaceCollections.randomAsync(NUMBERS_LIST).ifPresent(Assertions::assertNotNull);
  }

  /**
   * Test async with empty list -> {@link Optional#empty()} -> null
   */
  @Test
  public void testAsyncRandomEmpty() {
    SpaceCollections.randomAsync(new ArrayList<>()).ifPresent(Assertions::assertNull);
  }

  /**
   * Test async with null -> {@link Optional#empty()} -> null
   */
  @Test
  public void testAsyncRandomNull() {
    SpaceCollections.randomAsync((Collection<Object>) null).ifPresent(Assertions::assertNull);
  }
}
