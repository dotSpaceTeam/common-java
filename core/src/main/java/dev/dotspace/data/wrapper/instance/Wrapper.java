package dev.dotspace.data.wrapper.instance;

import org.jetbrains.annotations.NotNull;

public interface Wrapper {

  default void latestProcessedObject(@NotNull final Object object) throws IllegalArgumentException, ClassCastException{
    throw new IllegalArgumentException("Method not implemented.");
  }

}
