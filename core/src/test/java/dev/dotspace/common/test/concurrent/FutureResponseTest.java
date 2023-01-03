package dev.dotspace.common.test.concurrent;

import dev.dotspace.common.concurrent.FutureResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FutureResponseTest {

  private final static int INTEGER_TO_COMPLETE = 5;

  /**
   * Test a simple {@link Integer} response.
   */
  @Test
  public void testIntegerFuture() {
    final FutureResponse<Integer> futureResponse = new FutureResponse<>(Integer.class); //Create with class info.
    futureResponse.ifPresent(integer -> Assertions.assertEquals(integer, INTEGER_TO_COMPLETE));
    futureResponse.composeContent(integerResponseContent -> integerResponseContent.content(INTEGER_TO_COMPLETE)); //Complete future.
    futureResponse
      .map(integer -> Integer.toString(integer))
      .ifPresent(mappedValue -> Assertions.assertEquals(mappedValue, Integer.toString(INTEGER_TO_COMPLETE)));

    futureResponse
      .map(integer -> integer * 2)
      .ifPresent(integer -> Assertions.assertEquals(integer, INTEGER_TO_COMPLETE * 2));
  }


  /**
   * Test a simple {@link Integer} response. If present value is null
   */
  @Test
  public void testIntegerFutureAbsent() {
    final FutureResponse<Integer> futureResponse = new FutureResponse<>(Integer.class); //Create with class info.
    futureResponse.ifPresent(integer -> Assertions.fail());

    futureResponse.composeContent(integerResponseContent -> {
    });
  }

  @Test
  public void testIntegerCompose() {

  }
}
