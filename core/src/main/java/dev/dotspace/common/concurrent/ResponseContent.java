package dev.dotspace.common.concurrent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hold an object or throw an {@link Exception} on get.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class ResponseContent<TYPE> {
  /**
   * Object holding the information of this response.
   */
  private @Nullable TYPE content;
  /**
   * {@link Throwable} causing an empty content of this class.
   */
  private @Nullable Throwable throwable;

  /**
   * Create new {@link ResponseContent} with and start content.
   *
   * @param content to set as information.
   */
  public ResponseContent(@Nullable final TYPE content) {
    this.content = content;
  }

  /**
   * Similar to {@link ResponseContent}, this sets 'null' as default.
   */
  public ResponseContent() {
    this(null);
  }
}
