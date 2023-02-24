package dev.dotspace.common.response;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;

/**
 * Function to execute Function if {@link Response} is completed.
 *
 * @param <TYPE> type of response. (Also the same type as the Response)
 */
@FunctionalInterface
public interface ResponseFunction<TYPE> {
  /**
   * Executes the function of the class.
   *
   * @param executorService when a thread service is needed.
   */
  void run(@Nullable final ExecutorService executorService);

  /**
   * Execute without service, only synchronized operations are possible.
   */
  default void run() {
    this.run(null /*No service for run process.*/);
  }
}
