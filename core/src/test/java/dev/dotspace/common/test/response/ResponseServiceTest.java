package dev.dotspace.common.test.response;

import dev.dotspace.common.concurrent.ProcessType;
import dev.dotspace.common.response.ResponseConsumer;
import dev.dotspace.common.response.ResponseService;
import dev.dotspace.common.response.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test {@link dev.dotspace.common.response.ResponseService}
 */
public final class ResponseServiceTest {
  @Test
  public void testBuilder() {
    Assertions.assertDoesNotThrow(ResponseService::simple);
    Assertions.assertDoesNotThrow(() -> ResponseService.handled(ProcessType.MULTI, null));
    Assertions.assertDoesNotThrow(() -> ResponseService.handled(ProcessType.MULTI, throwable -> {
    }));

    Assertions.assertThrows(NullPointerException.class, () -> ResponseService.handled(null, null));
  }


}
