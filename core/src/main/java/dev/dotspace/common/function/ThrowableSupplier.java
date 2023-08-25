package dev.dotspace.common.function;

import dev.dotspace.common.annotation.LibraryInformation;
import org.jetbrains.annotations.Nullable;

/**
 * {@link java.util.function.Supplier} with throwing possibility.
 *
 * @param <T> the type of results supplied by this supplier.
 */
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
@FunctionalInterface
public interface ThrowableSupplier<T> {
  /**
   * {@link java.util.function.Supplier}
   *
   * @return value of supplier.
   * @throws Throwable if something went wrong while get.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  @Nullable T get() throws Throwable;
}
