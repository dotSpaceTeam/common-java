package dev.dotspace.common.storage.container;

import dev.dotspace.common.wrapper.instance.Wrapper;
import dev.dotspace.common.wrapper.instance.WrapperType;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StorageData {
  /**
   * Name of the specific wrapper.
   *
   * @return name of the wrapper.
   */
  @NotNull String name();

  /**
   * Type of storage.
   *
   * @return storage type class with this annotation.
   */
  @NotNull StorageType storageType();

  /**
   * Priority of wrapper.
   * Range of priority os byte range (-128...127).
   * -> 127 will go first.
   *
   * @return priority of {@link Wrapper}.
   */
  byte priority() default 0;
}
