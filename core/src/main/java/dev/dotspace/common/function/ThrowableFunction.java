package dev.dotspace.common.function;

import dev.dotspace.common.annotation.LibraryInformation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
@FunctionalInterface
public interface ThrowableFunction<T, R> {
  /**
   * Function with give and get value. Similar to {@link Function#apply(Object)}.
   *
   * @param t to give as start value.
   * @return to get as end value.
   * @throws Throwable if something throws error in function.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  @Nullable R apply(@Nullable final T t) throws Throwable;

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
