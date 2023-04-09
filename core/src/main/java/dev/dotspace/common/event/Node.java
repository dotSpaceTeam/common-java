package dev.dotspace.common.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Node<EVENT extends Event> {

  @NotNull Node<EVENT> addChild(@Nullable final Node<EVENT> eventNode);

  @NotNull Node<EVENT> removeChild(@Nullable final Node<EVENT> eventNode);

}
