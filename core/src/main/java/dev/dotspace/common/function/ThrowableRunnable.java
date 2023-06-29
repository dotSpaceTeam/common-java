package dev.dotspace.common.function;

import dev.dotspace.common.annotation.LibraryInformation;

@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
@FunctionalInterface
public interface ThrowableRunnable {
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  void run();

}
