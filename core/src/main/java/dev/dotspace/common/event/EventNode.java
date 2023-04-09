package dev.dotspace.common.event;

import dev.dotspace.common.SpaceObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EventNode<EVENT extends Event> implements Node<EVENT> {
  private final int priority;
  private final @NotNull EventConsumer<EVENT> eventConsumer;

  /**
   * @param priority
   * @param eventConsumer
   * @throws NullPointerException if eventClass or eventEventConsumer is null.
   */
  public EventNode(final int priority,
                   @Nullable final EventConsumer<EVENT> eventConsumer) {
    this.priority = priority;
    this.eventConsumer = SpaceObjects.throwIfNull(eventConsumer);
  }

  @Override
  public @NotNull Node<EVENT> addChild(@Nullable Node<EVENT> eventNode) {
    return this;
  }

  @Override
  public @NotNull Node<EVENT> removeChild(@Nullable Node<EVENT> eventNode) {
    return this;
  }
}
