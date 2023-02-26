package dev.dotspace.common.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface LibraryInformation {
  /**
   * Implemented since.
   *
   * @return
   */
  @NotNull String since() default "_NA_";

  /**
   * Latest update
   *
   * @return
   */
  @NotNull String updated() default "_NA_";

  /**
   * @return set {@link Access} of annotation.
   */
  @NotNull Access access() default Access.PUBLIC;

  /**
   * @return set {@link State} of annotation.
   */
  @NotNull State state() default State.DEPRECATED;

  /**
   * Indicates the access to the target.
   */
  enum Access {
    /**
     * Intended to declare things only for internal processes
     */
    INTERNAL,
    /**
     * Intended to declare things accessible to all
     */
    PUBLIC;
  }

  /**
   * Indicates the status.
   */
  enum State {
    /**
     * Stands for the current development of the implementation.
     */
    WORK_IN_PROGRESS,
    /**
     * If the implementation has been completed, but it has not been extensively tested.
     */
    EXPERIMENTAL,
    /**
     * Tests have been performed and the implementation is suitable for use.
     */
    STABLE,
    /**
     * Similar to {@link Deprecated}. Code is no longer maintained.
     */
    DEPRECATED
  }
}
