package dev.dotspace.common.response;

import dev.dotspace.common.annotation.LibraryInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This consumer passes the information of a {@link ResponseConsumer}.
 * A normal {@link java.util.function.Consumer} was not usable, because this one now passes a value.
 *
 * @param <TYPE> defines the type of {@link Response} type.
 */
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
@FunctionalInterface
public interface ResponseConsumer<TYPE> {
  /**
   * This method is filled in by the {@link Response}.
   * Typically, this method is used when completing the answer.
   *
   * @param state     the current {@link State} of the response.
   * @param type      keeps the value of the answer if any. Otherwise, null.
   *                  (Not null if state equals {@link State#COMPLETED_DEFAULT})
   * @param throwable keeps the completed error message if available. Otherwise, null.
   *                  (Can only be present if state equals {@link State#COMPLETED_EXCEPTIONALLY})
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  void accept(@NotNull final State state,
              @Nullable final TYPE type,
              @Nullable final Throwable throwable) throws Throwable;
}
