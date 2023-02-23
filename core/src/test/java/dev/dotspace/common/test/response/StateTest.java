package dev.dotspace.common.test.response;

import dev.dotspace.common.response.State;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test enum {@link dev.dotspace.common.response.State}.
 */
@NoArgsConstructor
public final class StateTest {

  /**
   * Test {@link State#done()}.
   */
  @Test
  public void testDone() {
    Assertions.assertFalse(State.UNCOMPLETED.done());

    Assertions.assertTrue(State.CANCELLED.done());
    Assertions.assertTrue(State.COMPLETED_DEFAULT.done());
    Assertions.assertTrue(State.COMPLETED_NULL.done());
    Assertions.assertTrue(State.COMPLETED_EXCEPTIONALLY.done());
  }
}
