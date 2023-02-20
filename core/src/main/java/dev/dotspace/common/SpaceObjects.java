package dev.dotspace.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpaceObjects {
  /**
   * Check object if present. If object is absent try to get alternate object trough absentSupplier.
   *
   * @param object         to get if present.
   * @param absentSupplier to use as supplier if object is null.
   * @param <TYPE>         generic type of object and absentSupplier.
   * @return present object.
   * @throws NullPointerException when object or absentSupplier(also the supplied object) is null.
   */
  public static <TYPE> @NotNull TYPE ifAbsentUse(@Nullable final TYPE object,
                                                 @Nullable final Supplier<@Nullable TYPE> absentSupplier) {
    if (object != null) { //Return object if present.
      return object;
    }

    if (absentSupplier == null) {
      throw new NullPointerException("Given absent supplier is null!");
    }

    @Nullable final TYPE absentObject = absentSupplier.get();

    if (absentObject == null) {
      throw new NullPointerException("Supplied object for absent value is null.");
    }

    return absentObject;
  }

  /**
   * Throw any bug that inherits from class {@link Throwable}.
   *
   * @param object            to check if it is null.
   * @param throwableSupplier supply {@link Throwable} used if absent.
   * @param <TYPE>            generic type of object, needed for return parameter.
   * @param <THROWABLE>       type of error.
   * @return object if present and not null.
   * @throws NullPointerException if object is null and throwableSupplier or it's response is null.
   * @throws THROWABLE            if object is null and error supplier is present.
   */
  public static <TYPE, THROWABLE extends Throwable> @NotNull TYPE throwIfNull(@Nullable final TYPE object,
                                                                              @Nullable final Supplier<@Nullable THROWABLE> throwableSupplier) throws THROWABLE {
    if (object != null) { //Return object if not null
      return object;
    }

    if (throwableSupplier == null) { //Throw NullPointerException if throwableSupplier is null.
      throw new NullPointerException("Object is null but also the given supplier.");
    }

    @Nullable THROWABLE throwable = throwableSupplier.get(); //Get throwable from supplier.

    if (throwable == null) { //Throw error if supplied throwable of throwableSupplier is null.
      throw new NullPointerException("Throwable is null.");
    }

    throw throwable; //Throwable error of throwableSupplier.
  }

  /**
   * Throw {@link NullPointerException} if object is null with given message.
   *
   * @param object  to check if it is null
   * @param message to throw as error.
   * @param <TYPE>  generic type of object, needed for return parameter
   * @return object if present and not null
   * @throws NullPointerException if object is null
   */
  public static <TYPE> @NotNull TYPE throwIfNull(@Nullable final TYPE object,
                                                 @Nullable final String message) throws NullPointerException {
    if (object == null) { //Trow error if null.
      throw new NullPointerException(message);
    }
    return object;
  }

  /**
   * Throw {@link NullPointerException} if object is null.
   *
   * @param object to check if it is null
   * @param <TYPE> generic type of object, needed for return parameter
   * @return object if present and not null
   * @throws NullPointerException if object is null
   */
  public static <TYPE> @NotNull TYPE throwIfNull(@Nullable final TYPE object) throws NullPointerException {
    return SpaceObjects.throwIfNull(object, (String) null);
  }

  /**
   * Consume object if not null
   *
   * @param object   object to consume
   * @param consumer consumer of object
   * @param <TYPE>   generic type of object
   */
  public static <TYPE> void acceptIfPresent(@Nullable final TYPE object,
                                            @Nullable final Consumer<@NotNull TYPE> consumer) {
    if (object != null && consumer != null) {
      consumer.accept(object);
    }
  }
}
