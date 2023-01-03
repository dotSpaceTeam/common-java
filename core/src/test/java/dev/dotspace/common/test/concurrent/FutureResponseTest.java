package dev.dotspace.common.test.concurrent;

import dev.dotspace.common.concurrent.FutureResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FutureResponseTest {

  /**
   * Test a simple {@link Integer} response.
   */
  @Test
  public void testIntegerFuture() {
    final int valueToComplete = 5;

    final FutureResponse<Integer> futureResponse = new FutureResponse<>(Integer.class); //Create with class info.
    futureResponse.ifPresent(integer -> Assertions.assertEquals(integer, valueToComplete));
    futureResponse.composeContent(integerResponseContent -> integerResponseContent.content(valueToComplete)); //Complete future.
    futureResponse
      .map(integer -> Integer.toString(integer))
      .ifPresent(mappedValue -> Assertions.assertEquals(mappedValue, Integer.toString(valueToComplete)));
  }

  @Test
  public void testIntegerCompose() {

  }
}
