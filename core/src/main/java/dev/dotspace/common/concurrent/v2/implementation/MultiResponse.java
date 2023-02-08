package dev.dotspace.common.concurrent.v2.implementation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MultiResponse<TYPE>(@NotNull List<TYPE> responseList,
                                  @NotNull List<Throwable> throwableList) {

  public int values() {
    return this.responseList.size() + this.throwableList.size();
  }

}
