package dev.dotspace.common.wrapper.instance;

import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface Wrapper {

  /**
   * @param object
   * @return
   * @throws IllegalArgumentException
   * @throws ClassCastException
   */
  default boolean latestProcessedObject(@Nullable final Object object) throws IllegalArgumentException, ClassCastException {
    throw new IllegalArgumentException("Method not implemented.");
  }
}
