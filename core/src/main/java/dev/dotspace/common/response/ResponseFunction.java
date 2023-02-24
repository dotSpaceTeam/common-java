package dev.dotspace.common.response;

import dev.dotspace.annotation.LibraryInformation;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;

/**
 * Function to execute Function if {@link Response} is completed.
 *
 * @param <TYPE> type of response. (Also the same type as the Response)
 */
@SuppressWarnings("unused") //Some methods are meant to be for the library -> Suppress idea warnings.
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
@FunctionalInterface
public interface ResponseFunction<TYPE> {
  /**
   * Executes the function of the class.
   *
   * @param executorService when a thread service is needed.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  void run(@Nullable final ExecutorService executorService);

  /**
   * Execute without service, only synchronized operations are possible.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  default void run() {
    this.run(null /*No service for run process.*/);
  }
}
