package dev.dotspace.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpaceObjects {
  /**
   * Throw a supplied {@link NullPointerException} if object is null.
   *
   * @param object            to check if it is null
   * @param exceptionSupplier supply {@link NullPointerException} used if absent
   * @param <T>               generic type of object, needed for return parameter
   * @return object if present and not null
   * @throws NullPointerException if object is null
   */
  public static <T> @NotNull T throwIfNull(@Nullable final T object,
                                           @Nullable Supplier<NullPointerException> exceptionSupplier) {
    if (object != null) { //Return object if not null
      return object;
    }
    throw Optional
      .ofNullable(exceptionSupplier)
      .map(Supplier::get)
      .orElseGet(NullPointerException::new); //Throw exception
  }

  /**
   * Throw {@link NullPointerException} if object is null with given message.
   *
   * @param object  to check if it is null
   * @param message to throw as error.
   * @param <T>     generic type of object, needed for return parameter
   * @return object if present and not null
   * @throws NullPointerException if object is null
   */
  public static <T> @NotNull T throwIfNull(@Nullable final T object,
                                           @Nullable String message) {
    return SpaceObjects.throwIfNull(object, message == null ? null : () -> new NullPointerException(message));
  }

  /**
   * Throw {@link NullPointerException} if object is null.
   *
   * @param object to check if it is null
   * @param <T>    generic type of object, needed for return parameter
   * @return object if present and not null
   * @throws NullPointerException if object is null
   */
  public static <T> @NotNull T throwIfNull(@Nullable final T object) throws NullPointerException {
    return SpaceObjects.throwIfNull(object, "Object is null!");
  }

  /**
   * Consume object if not null
   *
   * @param t        object to consume
   * @param consumer consumer of object
   * @param <T>      generic type of object
   */
  public static <T> void acceptIfPresent(@Nullable final T t,
                                         @Nullable final Consumer<T> consumer) {
    if (t != null && consumer != null) {
      consumer.accept(t);
    }
  }
}
