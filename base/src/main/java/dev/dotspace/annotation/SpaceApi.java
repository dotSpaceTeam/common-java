package dev.dotspace.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpaceApi {
  /**
   * Implemented since.
   *
   * @return
   */
  @NotNull String since();

  /**
   * Latest update
   *
   * @return
   */
  @NotNull String updated();

  /**
   * @return
   */
  @NotNull Access access() default Access.PUBLIC;

  /**
   *
   * @return
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

  enum State {
    WORK_IN_PROGRESS,
    EXPERIMENTAL,
    STABLE,
    DEPRECATED

  }
}
