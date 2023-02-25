package dev.dotspace.common.test.concurrent;

import dev.dotspace.common.response.CompletableResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.function.Supplier;

/**
 * Test class for {@link CompletableResponse} methods.
 */
public class CompletableResponseTest {

  private final static @NotNull String DEFAULT_STRING = "Test";

  /**
   * Test: {@link CompletableResponse#complete(Object)}.
   */
  @Test
  public void testComplete() {
    final CompletableResponse<String> response = new CompletableResponse<>();
    response.ifPresent(s -> Assertions.assertEquals(s, DEFAULT_STRING));
    response.complete(DEFAULT_STRING);
  }

  /**
   * Test: {@link CompletableResponse#completeAsync(Supplier)}.
   */
  @Test
  public void testCompleteAsync() {
    final Timer block = new Timer(); //Block main thread to wait for test result.
    final CompletableResponse<String> response = new CompletableResponse<>();
    response.ifPresent(s -> {
      Assertions.assertEquals(s, DEFAULT_STRING);
      block.cancel();
    });
    response.completeAsync(() -> DEFAULT_STRING);
  }


}
