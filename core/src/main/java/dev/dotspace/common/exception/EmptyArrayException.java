package dev.dotspace.common.exception;

import dev.dotspace.common.annotation.LibraryInformation;
import org.jetbrains.annotations.Nullable;

/**
 * This Exception is a {@link RuntimeException}.
 * <br>
 * The {@link EmptyArrayException} is used when an array is empty but an operation is still to be conducted.
 * Use cases:
 * <ul>
 *   <li>Process an array with no values but methods needs a response.</li>
 * </ul>
 */
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
public class EmptyArrayException extends RuntimeException {
  /**
   * Creates an instance with message.
   *
   * @param message error message. Can be obtained with {@link Exception#getMessage()}.
   */
  public EmptyArrayException(@Nullable final String message) {
    super(message);
  }

  /**
   * Standard constructor.
   */
  public EmptyArrayException() {
    super();
  }
}
