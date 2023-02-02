package dev.dotspace.data.wrapper.instance;

import dev.dotspace.data.wrapper.method.WrapperInstanceMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record WrapperInstance<WRAPPER extends Wrapper>(@NotNull WRAPPER wrapper,
                                                       @NotNull String name,
                                                       @NotNull WrapperType wrapperType,
                                                       byte priority,
                                                       @NotNull List<WrapperInstanceMethod> methods) {

}
