package dev.dotspace.common.function;

import dev.dotspace.common.annotation.LibraryInformation;

@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
@FunctionalInterface
public interface ThrowableRunnable {
  /**
   * Function to execute. Similar to {@link Runnable#run()}.
   *
   * @throws Throwable if something throws a {@link Throwable} in run method.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  void run() throws Throwable;

}
