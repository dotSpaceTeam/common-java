package dev.dotspace.common.exception;

import org.jetbrains.annotations.Nullable;

/**
 * This Exception is a {@link RuntimeException}.
 * <br>
 * The {@link MismatchException} is used if Objects are compared and not match.
 * Use cases:
 * <ul>
 *   <li>Compare {@link dev.dotspace.common.response.CompletableResponse} with each other.</li>
 * </ul>
 */
public class MismatchException extends RuntimeException {
  /**
   * Creates an instance with message.
   *
   * @param message error message. Can be obtained with {@link Exception#getMessage()}.
   */
  public MismatchException(@Nullable final String message) {
    super(message);
  }

  /**
   * Standard constructor.
   */
  public MismatchException() {
    super();
  }
}
