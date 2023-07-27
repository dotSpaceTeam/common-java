package dev.dotspace.common.test;

import dev.dotspace.common.SpaceTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * Test Class {@link SpaceTime}.
 */
public class SpaceTimeTest {

  @Test
  public void testTime() {
    Assertions.assertEquals(System.currentTimeMillis() / 1000, SpaceTime.currentTimeAs(TimeUnit.SECONDS));
    Assertions.assertNotEquals((System.currentTimeMillis() / 1000) - 1, SpaceTime.currentTimeAs(TimeUnit.SECONDS));
  }

  @Test
  public void testTimeStamp() {
    final SpaceTime.Timestamp timestamp = SpaceTime.timestampNow();

    Assertions.assertTrue(timestamp.pastTime() < 1000000); //Time in nanoseconds.
    try {
      Thread.sleep(2000L);
    } catch (final InterruptedException ignore) {
    }
    Assertions.assertNotEquals(0L, timestamp.pastTime());
    Assertions.assertTrue(timestamp.pastTime() > 1000);
    Assertions.assertTrue(timestamp.pastTimeFormatted(TimeUnit.SECONDS) > 1);
  }
}
