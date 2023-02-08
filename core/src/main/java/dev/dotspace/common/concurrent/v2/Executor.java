package dev.dotspace.common.concurrent.v2;


import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;

public interface Executor<TYPE> {

  void run(@NotNull final ExecutorService executorService);

}
