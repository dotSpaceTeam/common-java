package dev.dotspace.common.wrapper.method;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

public record WrapperInstanceMethod(@NotNull Type returnType,
                                    @NotNull Type[] arguments,
                                    @NotNull String name,
                                    @NotNull Method javaMethod) {

  @Override
  public String toString() {
    return "WrapperInstanceMethod{" +
      "returnType=" + returnType +
      ", arguments=" + Arrays.toString(arguments) +
      ", name='" + name + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WrapperInstanceMethod wrapperMethod = (WrapperInstanceMethod) o;
    return this.returnType.equals(wrapperMethod.returnType()) &&
      Arrays.equals(arguments, wrapperMethod.arguments()) &&
      name.equals(wrapperMethod.name());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(returnType, name);
    result = 31 * result + Arrays.hashCode(arguments);
    return result;
  }
}
