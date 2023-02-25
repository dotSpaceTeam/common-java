package dev.dotspace.common;

import dev.dotspace.common.annotation.JUnitVerification;
import dev.dotspace.common.annotation.LibraryInformation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Methods to make life with objects easier.
 */
@SuppressWarnings("unused") //Some methods are meant to be for the library -> Suppress idea warnings.
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
public final class SpaceObjects {
  /**
   * Throw {@link NullPointerException} if object is null with given message.
   *
   * @param object  to check if it is null.
   * @param message to throw as error.
   * @param <TYPE>  generic type of object, needed for return parameter.
   * @return object if present and not null.
   * @throws NullPointerException if object is null.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  @JUnitVerification
  public static <TYPE> @NotNull TYPE throwIfNull(@Nullable final TYPE object,
                                                 @Nullable final String message) throws NullPointerException {
    if (object != null) { //Return object if present.
      return object;
    }
    throw new NullPointerException(message); //Throw if null.
  }

  /**
   * Throw {@link NullPointerException} if object is null.
   * Simple calls {@link SpaceObjects#throwIfNull(Object, String)} with null as message.
   *
   * @param object to check if it is null.
   * @param <TYPE> generic type of object, needed for return parameter.
   * @return object if present and not null.
   * @throws NullPointerException if object is null.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  @JUnitVerification
  public static <TYPE> @NotNull TYPE throwIfNull(@Nullable final TYPE object) throws NullPointerException {
    return throwIfNull(object, (String) null);
  }

  /**
   * Throw any bug that inherits from class {@link Throwable}.
   *
   * @param object            to check if it is null.
   * @param throwableSupplier supply {@link Throwable} used if absent.
   * @param <TYPE>            generic type of object, needed for return parameter.
   * @param <THROWABLE>       type of error.
   * @return object if present and not null.
   * @throws NullPointerException if object is null and throwableSupplier, or it's response is null.
   * @throws THROWABLE            if object is null and error supplier is present.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  @JUnitVerification
  public static <TYPE, THROWABLE extends Throwable> @NotNull TYPE throwIfNull(@Nullable final TYPE object,
                                                                              @Nullable final Supplier<@Nullable THROWABLE> throwableSupplier) throws THROWABLE {
    if (object != null) { //Return object if not null
      return object;
    }
    throw throwIfNull(
      throwIfNull(throwableSupplier, "Object is null but also the given supplier." /*Throw if throwableSupplier is null.*/).get() /*Get throwable.*/,
      "Throwable is null." /*If supplied throwable is null.*/);
  }


  /**
   * Check object if present. If object is absent try to get alternate object trough absentSupplier.
   *
   * @param object         to get if present.
   * @param absentSupplier to use as supplier if object is null.
   * @param <TYPE>         generic type of object and absentSupplier.
   * @return present object.
   * @throws NullPointerException when object or absentSupplier(also the supplied object) is null.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  @JUnitVerification
  public static <TYPE> @NotNull TYPE ifAbsentUse(@Nullable final TYPE object,
                                                 @Nullable final Supplier<@Nullable TYPE> absentSupplier) {
    return object != null ? object :
      throwIfNull(throwIfNull(absentSupplier, "Supplier is null!"  /*Error if absentSupplier is null.*/).get() /*If not get value.*/,
        "Supplied object for absent value is null." /*Throw error if supplied object is null.*/);
  }

  /**
   * Consume object if not null.
   *
   * @param object   object to consume.
   * @param consumer consumer of object.
   * @param <TYPE>   generic type of object.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  @JUnitVerification
  public static <TYPE> void ifPresent(@Nullable final TYPE object,
                                      @Nullable final Consumer<@NotNull TYPE> consumer) {
    if (object != null && consumer != null) { //Consume if object and consumer is not null.
      consumer.accept(object);
    }
  }
}