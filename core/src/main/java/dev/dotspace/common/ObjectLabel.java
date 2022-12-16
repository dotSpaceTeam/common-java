package dev.dotspace.common;

import org.jetbrains.annotations.NotNull;

/**
 * Label an object with a label. The hashCode of the object is used for hashCode of the {@link ObjectLabel} instance.
 *
 * @param label    to set as identifier.
 * @param object   to bind to label.
 * @param <LABEL>  Generic type of the label.
 * @param <OBJECT> Generic type of the object.
 */
public record ObjectLabel<LABEL, OBJECT>(@NotNull LABEL label,
                                         @NotNull OBJECT object) {
  /**
   * See: {@link Object#hashCode()}.
   */
  @Override
  public int hashCode() {
    return this.object().hashCode();
  }
}
