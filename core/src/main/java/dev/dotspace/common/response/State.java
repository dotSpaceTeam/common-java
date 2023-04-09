package dev.dotspace.common.response;

import dev.dotspace.common.annotation.JUnitVerification;
import dev.dotspace.common.annotation.LibraryInformation;

/**
 * This enum can be used to express the status of a {@link Response}.
 */
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
@JUnitVerification
public enum State {
  /**
   * Used when the response has not yet been processed. (Default value)
   */
  UNCOMPLETED,
  /**
   * If the answer was canceled.
   */
  CANCELLED,
  /**
   * If the answer was completed normally -> with value.
   */
  COMPLETED_DEFAULT,
  /**
   * If the answer was completed but with null.
   */
  COMPLETED_NULL,
  /**
   * If the answer was completed with an error.
   */
  COMPLETED_EXCEPTIONALLY;

  /**
   * Indicates whether the state is completed.  If the value is true, the state is complete.
   *
   * @return true, if state is not UNCOMPLETED.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  @JUnitVerification
  public boolean done() {
    return this != UNCOMPLETED;
  }
}
