package dev.dotspace.common.data;

import dev.dotspace.common.annotation.LibraryInformation;
import dev.dotspace.common.response.CompletableResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
public interface Query<WRAPPER extends Wrapper> {

  /**
   * @param function
   * @param <RESPONSE>
   * @return
   */
  @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
  @NotNull <RESPONSE> CompletableResponse<RESPONSE> first(@Nullable final Function<WRAPPER, CompletableResponse<RESPONSE>> function);

  /**
   * @param function
   * @param <RESPONSE>
   * @return
   */
  @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
  @NotNull <RESPONSE> CompletableResponse<RESPONSE> collect(@Nullable final Function<WRAPPER, CompletableResponse<RESPONSE>> function);

  boolean persistentOnly();


//  @NotNull <RESPONSE> CompletableResponse<RESPONSE> run(@Nullable final Function<WRAPPER, CompletableResponse<RESPONSE>> function);

}
