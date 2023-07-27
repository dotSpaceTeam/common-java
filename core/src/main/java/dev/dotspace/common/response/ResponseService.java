package dev.dotspace.common.response;

import dev.dotspace.common.SpaceObjects;
import dev.dotspace.common.annotation.LibraryInformation;
import dev.dotspace.common.concurrent.ProcessType;
import dev.dotspace.common.function.ThrowableConsumer;
import dev.dotspace.common.service.Service;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Optional class to use and build unified response instances. Can be ideally used for unified error handling.
 */
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
public final class ResponseService implements Service {
  private final @NotNull ProcessType processType;
  /**
   *
   */
  private final @Nullable ThrowableConsumer<Throwable> exceptionConsumer;

  /**
   * @param processType
   * @param exceptionConsumer
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  private ResponseService(@NotNull final ProcessType processType,
                          @Nullable final ThrowableConsumer<Throwable> exceptionConsumer) {
    this.processType = processType;
    this.exceptionConsumer = exceptionConsumer;
  }

  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public <TYPE> @NotNull Response<TYPE> newInstance() {
    final CompletableResponse<TYPE> response = new CompletableResponse<>();

    //Store in variable for thread safe operation.
    final ThrowableConsumer<Throwable> localConsumer = this.exceptionConsumer;
    //Check if local consumer is present
    if (localConsumer != null) {
      if (this.processType.multiThread()) {
        response.ifExceptionallyAsync(localConsumer);
      } else {
        response.ifExceptionally(localConsumer);
      }
    }

    return response;
  }

  //Static builder

  /**
   * Create service with no function handling.
   *
   * @return created instance of service.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public static @NotNull ResponseService simple() {
    return new ResponseService(ProcessType.SINGLE, null);
  }

  /**
   * Create handeled service.
   *
   * @param exceptionConsumer set as {@link ResponseService#exceptionConsumer}.
   * @return created instance of service.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public static @NotNull ResponseService handled(@Nullable final ProcessType processType,
                                                 @Nullable final ThrowableConsumer<Throwable> exceptionConsumer) {
    return new ResponseService(SpaceObjects.throwIfNull(processType), exceptionConsumer);
  }
}
