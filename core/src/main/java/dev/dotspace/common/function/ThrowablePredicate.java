package dev.dotspace.common.function;

import dev.dotspace.common.annotation.LibraryInformation;
import org.jetbrains.annotations.Nullable;

@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
@FunctionalInterface
public interface ThrowablePredicate<T> {

  /**
   * Evaluates this predicate on the given argument.
   *
   * @param t the input argument
   * @return {@code true} if the input argument matches the predicate,
   * otherwise {@code false}
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  boolean test(@Nullable final T t);

}
