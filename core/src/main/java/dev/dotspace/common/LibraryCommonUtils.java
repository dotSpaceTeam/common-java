package dev.dotspace.common;

import dev.dotspace.common.annotation.LibraryInformation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Range;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Methods across classes for the library.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@LibraryInformation(state = LibraryInformation.State.STABLE, access = LibraryInformation.Access.INTERNAL, since = "1.0.6")
final class LibraryCommonUtils {
  /**
   * Get random index.
   *
   * @param arrayLength to get array from of. [between 0 and {@link Integer#MAX_VALUE}]
   * @return random calculated position(index)
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, access = LibraryInformation.Access.INTERNAL, since = "1.0.6")
  static int calculateRandomIndex(@Range(from = 0, to = Integer.MAX_VALUE) final int arrayLength) {
    return arrayLength > 1 ? (int) (ThreadLocalRandom.current().nextDouble() * arrayLength) : 0;  //Calculate random index to get from collection.
  }
}
