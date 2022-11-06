package dev.dotspace.common.test;

import dev.dotspace.common.SpaceTime;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class SpaceTimeTest {

  @Test
  public void testTime() {
    final long systemCurrentMills = System.currentTimeMillis();
    final long timeAsSeconds = SpaceTime.currentTimeAs(TimeUnit.SECONDS);

    Assertions.assertEquals(systemCurrentMills / 1000, timeAsSeconds);
  }

  @Test
  public void testWrongTime() {
    final long systemCurrentMills = System.currentTimeMillis();
    final long timeAsSeconds = SpaceTime.currentTimeAs(TimeUnit.SECONDS);

    Assertions.assertNotEquals((systemCurrentMills / 1000) - 1, timeAsSeconds);
  }

  @Test
  @SneakyThrows
  public void testTimeStamp() {
    final SpaceTime.Timestamp timestamp = SpaceTime.timestampNow();

    Assertions.assertEquals(0L, timestamp.pastTime());
    Thread.sleep(2000L);
    Assertions.assertNotEquals(0L, timestamp.pastTime());
    Assertions.assertTrue(timestamp.pastTime() > 1000);
    Assertions.assertTrue(timestamp.pastTimeFormatted(TimeUnit.SECONDS) > 1);
  }
}
