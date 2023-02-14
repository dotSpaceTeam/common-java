package dev.dotspace.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class LibraryCommonUtils {
  /**
   * Get random index.
   *
   * @param arrayLength
   * @return
   */
  static int calculateRandomIndex(final int arrayLength) {
    return arrayLength > 1 ? (int) (ThreadLocalRandom.current().nextDouble() * arrayLength) : 0;  //Calculate random index to get from collection.
  }
}
