package dev.dotspace.common.concurrent.v2;

import dev.dotspace.common.concurrent.v2.implementation.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ResponseConsumer<TYPE> {

  void accept(@NotNull final State state,
              @Nullable final TYPE type,
              @Nullable final Throwable throwable);

}
