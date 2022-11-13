package dev.dotspace.common.test.list;

import dev.dotspace.common.SpaceCollections;
import dev.dotspace.common.list.BasicPagination;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

public class BasicPaginationTest {
  private final static @NotNull Set<Integer> NUMBERS_LIST = new HashSet<>();
  private final static int ELEMENTS_PER_PAGE = 3;

  @BeforeAll
  public static void setup() {
    for (int i = 0; i < 10; i++) {
      NUMBERS_LIST.add(i);
    }
  }

  @Test
  public void testConstructor() {
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> new BasicPagination<>(-1)); //Check if a negative number can be used as elementsPerPage
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> new BasicPagination<>(0));//Check if a zero can be used as elementsPerPage
    Assertions.assertDoesNotThrow(() -> new BasicPagination<>(1)); //Check if a positive number can be used as elementsPerPage -> success
  }

  @Test
  public void testContent() {
    final BasicPagination<Integer> basicPagination = new BasicPagination<>(ELEMENTS_PER_PAGE);
    final List<Integer> newContent = List.of(1, 2, 3);

    basicPagination.content(newContent);

    Assertions.assertEquals(basicPagination.list(), List.of(1, 2, 3));
    Assertions.assertNotEquals(basicPagination.list(), new ArrayList<>(NUMBERS_LIST));
  }

  @Test
  public void testElements() {
    final BasicPagination<Integer> basicPagination = new BasicPagination<>(ELEMENTS_PER_PAGE);
    basicPagination.content(NUMBERS_LIST);

    Assertions.assertEquals(basicPagination.elements(0), List.of(0, 1, 2));
    Assertions.assertEquals(basicPagination.elements(3), Collections.singletonList(9));
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> basicPagination.elements(4));

    Assertions.assertEquals(basicPagination.elementsChecked(0), List.of(0, 1, 2));
    Assertions.assertEquals(basicPagination.elementsChecked(3), Collections.singletonList(9));
    Assertions.assertEquals(basicPagination.elementsChecked(4), Collections.singletonList(9));
  }
}
