package dev.dotspace.common.test.response;

import dev.dotspace.common.response.ResponseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test {@link dev.dotspace.common.response.ResponseService}
 */
public final class ResponseServiceTest {
  @Test
  public void testBuilder() {
    Assertions.assertDoesNotThrow(ResponseService::simple);
    Assertions.assertDoesNotThrow(() -> ResponseService.handled(null));
    Assertions.assertDoesNotThrow(() -> ResponseService.handled(throwable -> {
    }));
  }
}
