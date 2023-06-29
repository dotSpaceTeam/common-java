package dev.dotspace.common.function;

import dev.dotspace.common.annotation.LibraryInformation;
import org.jetbrains.annotations.Nullable;

/**
 * {@link java.util.function.Consumer} with throwing possibility.
 *
 * @param <T> the type of the input to the operation.
 */
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
@FunctionalInterface
public interface ThrowableConsumer<T> {
  /**
   * {@link java.util.function.Consumer}
   * Performs this operation on the given argument.
   *
   * @param t the input argument
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  void accept(@Nullable final T t);

}
