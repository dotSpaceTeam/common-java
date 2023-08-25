package dev.dotspace.common.response;

import dev.dotspace.common.annotation.LibraryInformation;
import dev.dotspace.common.function.ThrowableConsumer;
import dev.dotspace.common.function.ThrowableSupplier;
import dev.dotspace.common.service.Service;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;

/**
 * Optional class to use and build unified response instances. Can be ideally used for unified error handling.
 */
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
@Builder
public final class ResponseService implements Service {
  /**
   * Service to handle all {@link Response} objects.
   */
  private final @Nullable ExecutorService executorService;
  /**
   * Consumer to hand over responses created.
   */
  private final @Nullable ThrowableConsumer<Response<?>> createConsumer;
  /**
   * Consumer to hand over responses completed.
   */
  private final @Nullable ThrowableConsumer<Response<?>> completeConsumer;
  /**
   * Consumer to hand errors from any {@link Response} created by this class.
   */
  private final @Nullable ThrowableConsumer<Throwable> exceptionConsumer;

  /**
   * @param processType
   * @param executorService
   * @param createConsumer
   * @param completeConsumer
   * @param exceptionConsumer
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  private ResponseService(@Nullable final ExecutorService executorService,
                          @Nullable final ThrowableConsumer<Response<?>> createConsumer,
                          @Nullable final ThrowableConsumer<Response<?>> completeConsumer,
                          @Nullable final ThrowableConsumer<Throwable> exceptionConsumer) {
    this.executorService = executorService;
    this.createConsumer = createConsumer;
    this.completeConsumer = completeConsumer;
    this.exceptionConsumer = exceptionConsumer;
  }

  /**
   * Create without generic key.
   *
   * @param <TYPE> generic type of response. If not given -> {@link Object}.
   * @return new instance of response.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.9")
  public <TYPE> @NotNull Response<TYPE> newInstance() {
    return this.newInstance(null);
  }

  /**
   * Create with generic key.
   *
   * @param typeClass class to create generic response from.
   * @param <TYPE>    generic type of response. If null -> {@link Object}.
   * @return new instance of response.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8", updated = "1.0.9")
  public <TYPE> @NotNull Response<TYPE> newInstance(@Nullable final Class<TYPE> typeClass) {
    //Create new uncompleted response.
    final CompletableResponse<TYPE> response = new CompletableResponse<>(this.executorService);

    //Invoke service content.
    this.invoke(response);

    return response;
  }

  /**
   * Create new instance and complete with supplier.
   *
   * @param supplier to complete async. See {@link Response#completeAsync(ThrowableSupplier)}.
   * @param <TYPE>   generic type of response.
   * @return response.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.9")
  public <TYPE> @NotNull Response<TYPE> response(@Nullable final ThrowableSupplier<TYPE> supplier) {
    final Response<TYPE> response = new CompletableResponse<>(this.executorService);

    //Invoke service content.
    this.invoke(response);

    return response.completeAsync(supplier);
  }

  private <TYPE> void invoke(@NotNull final Response<TYPE> reference) {
    //Store in variable for thread safe operation.
    final ThrowableConsumer<Response<?>> localCreateConsumer = this.createConsumer;
    final ThrowableConsumer<Response<?>> localCompleteConsumer = this.completeConsumer;
    final ThrowableConsumer<Throwable> localExceptionConsumer = this.exceptionConsumer;

    //Check if local create is present
    if (localCreateConsumer != null) {
      try {
        //Accept response.
        localCreateConsumer.accept(reference);
      } catch (final Throwable exception) {
        throw new RuntimeException("Error while handling response create. ", exception);
      }
    }

    //Check if local complete is present
    if (localCompleteConsumer != null) {
      reference.run(() -> localCompleteConsumer.accept(reference));
    }

    //Check if local consumer is present
    if (localExceptionConsumer != null) {
      reference.ifExceptionally(localExceptionConsumer);
    }
  }
}
