package dev.dotspace.common.storage.container;

import org.jetbrains.annotations.Nullable;

public interface Storage {

  /**
   * @param object
   * @return
   * @throws IllegalArgumentException
   * @throws ClassCastException
   */
  default boolean latestObject(@Nullable final Object object) throws IllegalArgumentException, ClassCastException {
    throw new IllegalArgumentException("Method not implemented.");
  }
}
