package dev.dotspace.common.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EventManager {

  @NotNull <EVENT extends Event> EventNode<EVENT> registerEvent(@Nullable final Class<EVENT> eventClass,
                                                                final int priority,
                                                                @Nullable final EventConsumer<EVENT> eventConsumer);

  @NotNull <EVENT extends Event> EventNode<EVENT> registerEvent(@Nullable final Class<EVENT> eventClass,
                                                                @Nullable final EventConsumer<EVENT> eventConsumer);
}
