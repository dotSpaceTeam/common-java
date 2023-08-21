package dev.dotspace.common.response;

import dev.dotspace.common.annotation.LibraryInformation;
import dev.dotspace.common.function.ThrowableConsumer;
import dev.dotspace.common.service.Service;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Optional class to use and build unified response instances. Can be ideally used for unified error handling.
 */
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
@Builder
public final class ResponseService implements Service {
  /**
   *
   */
  private final @Nullable ThrowableConsumer<Throwable> exceptionConsumer;

  /**
   * @param processType
   * @param exceptionConsumer
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  private ResponseService(@Nullable final ThrowableConsumer<Throwable> exceptionConsumer) {
    this.exceptionConsumer = exceptionConsumer;
  }

  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public <TYPE> @NotNull Response<TYPE> newInstance() {
    final CompletableResponse<TYPE> response = new CompletableResponse<>();

    //Store in variable for thread safe operation.
    final ThrowableConsumer<Throwable> localConsumer = this.exceptionConsumer;
    //Check if local consumer is present
    if (localConsumer != null) {
        response.ifExceptionallyAsync(localConsumer);
    }

    return response;
  }
}
