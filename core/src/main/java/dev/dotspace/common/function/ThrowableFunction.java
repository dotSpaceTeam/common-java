package dev.dotspace.common.function;

import dev.dotspace.common.annotation.LibraryInformation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
@FunctionalInterface
public interface ThrowableFunction<T, R> {
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  @Nullable R apply(@Nullable final T t);

  /**
   * Returns a function that always returns its input argument.
   * Source {@link Function#identity()}.
   *
   * @param <T> the type of the input and output objects to the function
   * @return a function that always returns its input argument
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  static <T> Function<T, T> identity() {
    return t -> t;
  }

}
