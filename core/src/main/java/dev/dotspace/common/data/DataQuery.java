package dev.dotspace.common.data;

import dev.dotspace.common.SpaceArrays;
import dev.dotspace.common.SpaceObjects;
import dev.dotspace.common.annotation.LibraryInformation;
import dev.dotspace.common.exception.EmptyArrayException;
import dev.dotspace.common.response.CompletableResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
public final class DataQuery<WRAPPER extends Wrapper> implements Query<WRAPPER> {

  private final @NotNull WRAPPER[] wrappers;
  private final @NotNull WrapperType wrapperType;

  protected DataQuery(@NotNull final WRAPPER[] wrappers,
                      @NotNull final WrapperType wrapperType) {
    this.wrapperType = wrapperType;
    if (wrappers.length == 0) {
      throw new EmptyArrayException("Wrapper array empty!");
    }
    this.wrappers = wrappers;
  }

  /**
   * @see Query#first(Function).
   */
  @Override
  @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
  public @NotNull <RESPONSE> CompletableResponse<RESPONSE> first(@Nullable Function<WRAPPER, CompletableResponse<RESPONSE>> function) {
    return CompletableResponse.firstType(this.responseMap(function));
  }

  /**
   * @see Query#collect(Function).
   */
  @Override
  @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
  public @NotNull <RESPONSE> CompletableResponse<RESPONSE> collect(@Nullable Function<WRAPPER, CompletableResponse<RESPONSE>> function) {
    return CompletableResponse.equalType(this.responseMap(function));
  }

  @Override
  public boolean persistentOnly() {
    return false;
  }

  /**
   * @param function
   * @param <RESPONSE>
   * @return
   */
  @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
  private @NotNull <RESPONSE> CompletableResponse<RESPONSE>[] responseMap(@Nullable Function<WRAPPER, CompletableResponse<RESPONSE>> function) {
    SpaceObjects.throwIfNull(function);

    CompletableResponse<RESPONSE>[] responses = (CompletableResponse<RESPONSE>[]) new CompletableResponse[0];

    for (final WRAPPER wrapper : this.wrappers) {
      responses = SpaceArrays.push(responses, function.apply(wrapper));
    }
    return responses;
  }
}
