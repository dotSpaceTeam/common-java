package dev.dotspace.common.exception;

import org.jetbrains.annotations.Nullable;

/**
 * This Exception is a {@link RuntimeException}.
 * <br>
 * The {@link EmptyArrayException} is used when an array is empty but an operation is still to be conducted.
 */
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
