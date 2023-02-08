package dev.dotspace.common.concurrent.v2.implementation;

import dev.dotspace.common.SpaceObjects;
import dev.dotspace.common.concurrent.v2.Executor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public final class ResponseExecutor<TYPE> implements Executor<TYPE> {
  private final @NotNull Supplier<Boolean> checkIfExecutable;
  private final @NotNull Runnable runnable;
  private final boolean async;

  ResponseExecutor(@NotNull final Supplier<Boolean> checkIfExecutable,
                   @NotNull Runnable runnable,
                   final boolean async) {
    this.checkIfExecutable = SpaceObjects.throwIfNull(checkIfExecutable);
    this.runnable = runnable;
    this.async = async;
  }

  @Override
  public void run(@NotNull final ExecutorService executorService) {
    if (!this.checkIfExecutable.get()) {
      return;
    }
    if (this.async) {
      executorService.execute(this.runnable);
    } else {
      this.runnable.run();
    }
  }
}
