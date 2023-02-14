package dev.dotspace.common.concurrent;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;

/**
 * @param <TYPE>
 */
public interface ResponseFunction<TYPE> {
  /**
   * Executes the function of the class.
   *
   * @param executorService when a thread service is needed.
   */
  void run(@Nullable final ExecutorService executorService);
}
