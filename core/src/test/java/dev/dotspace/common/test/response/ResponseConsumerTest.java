package dev.dotspace.common.test.response;

import dev.dotspace.common.response.ResponseConsumer;
import dev.dotspace.common.response.State;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test interface {@link dev.dotspace.common.response.ResponseConsumer}.
 */
@NoArgsConstructor
public final class ResponseConsumerTest {
  private final static @NotNull String COMPONENT = "TEST";

  /**
   * Test {@link dev.dotspace.common.response.ResponseConsumer#accept(State, Object, Throwable)}.
   */
  @Test
  public void testAccept() {
    final ResponseConsumer<String> consumer = (state, s, throwable) -> {
      Assertions.assertEquals(state, State.COMPLETED_DEFAULT);
      Assertions.assertEquals(s, COMPONENT);
      Assertions.assertInstanceOf(RuntimeException.class, throwable);
    };

    try {
      consumer.accept(State.COMPLETED_DEFAULT, COMPONENT, new RuntimeException());
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
