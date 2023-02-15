package dev.dotspace.common.wrapper.instance;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrapperData {
  /**
   * Name of the specific wrapper.
   *
   * @return name of the wrapper.
   */
  @NotNull String name();

  /**
   * Type of wrapper.
   *
   * @return type class with this annotation.
   */
  @NotNull WrapperType wrapperType();

  /**
   * Priority of wrapper.
   * Range of priority os byte range (-128...127).
   * -> 127 will go first.
   *
   * @return priority of {@link Wrapper}.
   */
  byte priority() default 0;
}
