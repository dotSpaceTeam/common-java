package dev.dotspace.common.wrapper.manager;

import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Accessors(fluent = true)
public final class QueryOptions {
  public final static @NotNull QueryOptions DEFAULT = new QueryOptions();

  private boolean asynchronous = false;
  private boolean disableAutoCache = false;
  private boolean disableLogger = false;

  /**
   * Create default query options with async.
   *
   * @return query
   */
  public static @NotNull QueryOptions async() {
    return new QueryOptions().asynchronous(true);
  }
}
