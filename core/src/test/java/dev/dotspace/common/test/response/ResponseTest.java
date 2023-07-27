package dev.dotspace.common.test.response;

import dev.dotspace.common.response.CompletableResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * Test {@link ResponseTest}
 */
public final class ResponseTest {
  /**
   * String to test.
   */
  private final static String COMPETE_STRING = "TEST";

  @Test
  public void testPresent() {
    final long threadId = Thread.currentThread().getId();
    //Sync
    new CompletableResponse<String>()
      .ifPresent(s -> {
        Assertions.assertEquals(s, COMPETE_STRING);
        Assertions.assertEquals(threadId, Thread.currentThread().getId());
      })
      .complete(COMPETE_STRING);

    //Async
    new CompletableResponse<String>()
      .ifPresentAsync(s -> {
        Assertions.assertEquals(s, COMPETE_STRING);
        Assertions.assertNotEquals(threadId, Thread.currentThread().getId());
      })
      .complete(COMPETE_STRING);

    //Get
    Assertions.assertDoesNotThrow(() -> {
      final String getValue = new CompletableResponse<String>()
        .complete(COMPETE_STRING)
        .get();

      Assertions.assertEquals(getValue, COMPETE_STRING);
    });

    //Get with interrupt.
    Assertions.assertThrows(InterruptedException.class, () -> {
      final String getValue = new CompletableResponse<String>()
        .get(1, TimeUnit.SECONDS);

      Assertions.assertEquals(getValue, COMPETE_STRING);
    });
  }

  @Test
  public void testAbsent() {
    //Sync
    new CompletableResponse<String>()
      .ifAbsent(() -> Assertions.assertTrue(true))
      .ifPresent(s -> Assertions.fail())
      .complete(null);

    //Async
    new CompletableResponse<String>()
      .ifAbsentAsync(() -> Assertions.assertTrue(true))
      .ifPresentAsync(s -> Assertions.fail())
      .complete(null);
  }
}
