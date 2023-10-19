package dev.dotspace.common;

import dev.dotspace.common.annotation.LibraryInformation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DateTimeException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Class with time and duration operations
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
public final class SpaceTime {
  /**
   * Get the current system time in other time format
   *
   * @param timeUnit to convert to get currentTimeMillis in
   * @return currentTime from {@link System#currentTimeMillis()} converted with timeUnit
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static long currentTimeAs(@NotNull final TimeUnit timeUnit) {
    return timeUnit.convert(Duration.ofMillis(System.currentTimeMillis()));
  }

  /**
   * Create a new {@link Timestamp} with a set time as reference.
   *
   * @param timeNanos create a timestamp with another time than {@link System#nanoTime()}.
   *                  This value should be supplied as nanoseconds.
   * @return created {@link Timestamp} object
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull Timestamp timestamp(final long timeNanos) {
    return new Timestamp(timeNanos);
  }

  /**
   * Create a timestamp with current time. This method fills {@link SpaceTime#timestamp(long)}
   * with the value of {@link System#currentTimeMillis()}
   *
   * @return created {@link Timestamp} object
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull Timestamp timestampNow() {
    return SpaceTime.timestamp(System.nanoTime());
  }

  //records

  /**
   * Class to store a long for a timestamp and calculate with it.
   * <p>
   * Example:
   * Initialize a class
   * <pre><code>
   * final Timestamp timestamp = SpaceTime.timestampNow();
   *
   * //Do stuff here
   *
   * System.out.format("Process took: %d ms.", timestamp.pastTime());
   * System.out.format("Process took: %d  seconds.", timestamp.pastTimeFormatted(TimeUnit.SECONDS));
   * </code></pre>
   *
   * @param timestamp used as reference for the stamp in ns.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public record Timestamp(long timestamp) {
    /**
     * Get the pastTime since the {@link Timestamp} was created and now
     *
     * @return the difference as milliseconds
     * @throws DateTimeException if the difference is negative.
     */
    @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.6")
    public long pastTime() {
      final long diff = System.nanoTime() - this.timestamp; //Return different between then and now
      if (diff < 0) {
        throw new DateTimeException("Difference between times is negative!"); //Error if negative -> can't calculate the past
      }
      return diff;
    }

    /**
     * Get the pastTime since the {@link Timestamp} was created and now formatted
     *
     * @param timeUnit to format the pastime to
     * @return the difference formatted with {@link TimeUnit}
     * @throws NullPointerException if timeUnit is null.
     * @throws DateTimeException    if the difference is negative.
     */
    @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.6", updated = "1.0.9")
    public long pastTimeFormatted(@Nullable final TimeUnit timeUnit) {
      return SpaceObjects.throwIfNull(timeUnit).convert(Duration.ofNanos(this.pastTime()));
    }
  }
}
