package dev.dotspace.common.concurrent;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;

/**
 * @param <TYPE>
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
