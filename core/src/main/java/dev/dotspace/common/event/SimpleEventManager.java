package dev.dotspace.common.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SimpleEventManager implements EventManager {

  private final @NotNull Map<Class<? extends Event>, List<EventNode<? extends Event>>> events = new HashMap<>();

  @Override
  public @NotNull <EVENT extends Event> EventNode<EVENT> registerEvent(@Nullable Class<EVENT> eventClass,
                                                                       int priority,
                                                                       @Nullable EventConsumer<EVENT> eventConsumer) {
    return this.registerEventImplementation(eventClass, priority, eventConsumer);
  }

  @Override
  public @NotNull <EVENT extends Event> EventNode<EVENT> registerEvent(@Nullable Class<EVENT> eventClass,
                                                                       @Nullable EventConsumer<EVENT> eventConsumer) {
    return this.registerEvent(eventClass, 0, eventConsumer);
  }

  /**
   *
   * @param eventClass
   * @param priority
   * @param eventConsumer
   * @return
   * @throws NullPointerException if eventClass or eventConsumer is null ({@link EventNode#EventNode(int, EventConsumer)}).
   * @param <EVENT>
   */
  private synchronized <EVENT extends Event> @NotNull EventNode<EVENT> registerEventImplementation(@Nullable Class<EVENT> eventClass,
                                                                                                   int priority,
                                                                                                   @Nullable EventConsumer<EVENT> eventConsumer) {
    final EventNode<EVENT> eventNode = new EventNode<>(priority, eventConsumer);

    if (!this.events.containsKey(eventClass)) {
      this.events.put(eventClass, new ArrayList<>(Collections.singletonList(eventNode)));
    } else {
      this.events.get(eventClass).add(eventNode);
    }

    return eventNode;
  }
}
