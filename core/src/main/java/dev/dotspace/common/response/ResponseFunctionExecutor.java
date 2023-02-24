package dev.dotspace.common.response;

import dev.dotspace.common.SpaceObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * Implementation of {@link ResponseFunction}, used for {@link CompletableResponse}.
 *
 * @see ResponseFunction
 */
final class ResponseFunctionExecutor<TYPE> implements ResponseFunction<TYPE> {
  private final @Nullable Supplier<Boolean> checkIfExecutable;
  private final @NotNull Runnable runnable;
  private final boolean async;

  /**
   * Creates instance with all needed values.
   *
   * @param checkIfExecutable condition to check on execute.
   * @param runnable          to run.
   * @param async             true, if {@link Runnable} should be executed asynchronous.
   */
  ResponseFunctionExecutor(@Nullable final Supplier<Boolean> checkIfExecutable,
                           @NotNull Runnable runnable,
                           final boolean async) {
    this.checkIfExecutable = SpaceObjects.throwIfNull(checkIfExecutable);
    this.runnable = runnable;
    this.async = async;
  }

  /**
   * Create instance with no checkIfExecutable.
   *
   * @param runnable to run.
   * @param async    true, if {@link Runnable} should be executed asynchronous.
   */
  ResponseFunctionExecutor(@NotNull Runnable runnable,
                           final boolean async) {
    this(null, runnable, async);
  }

  /**
   * @see ResponseFunction#run(ExecutorService)
   */
  @Override
  public void run(@Nullable final ExecutorService executorService) {
    if (this.checkIfExecutable != null && !this.checkIfExecutable.get()) { //Do not execute if checkIfExecutable is present an result is true.
      return;
    }
    if (this.async && executorService != null) { //If async is true and executorService is present, run with executorService.
      executorService.execute(this.runnable);
    } else {
      this.runnable.run(); //Run default.
    }
  }
}
