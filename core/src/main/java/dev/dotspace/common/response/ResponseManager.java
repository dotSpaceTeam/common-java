package dev.dotspace.common.response;

import dev.dotspace.common.annotation.LibraryInformation;
import dev.dotspace.common.function.ThrowableConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Optional class to use and build unified response instances. Can be ideally used for unified error handling.
 */
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
public final class ResponseManager {
  private @Nullable ThrowableConsumer<Throwable> exceptionConsumer;

  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public @NotNull ResponseManager defineGlobalExceptionHandle(@Nullable final ThrowableConsumer<Throwable> consumer) {
    this.exceptionConsumer = consumer;
    return this;
  }

  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public <TYPE> @NotNull Response<TYPE> newInstance() {
    final CompletableResponse<TYPE> response = new CompletableResponse<>();

    final ThrowableConsumer<Throwable> localConsumer = this.exceptionConsumer;
    if (localConsumer != null) {
      response.ifExceptionallyAsync(localConsumer);
    }

    return response;
  }


}
