package dev.dotspace.common.event;

import java.util.function.Consumer;

@FunctionalInterface
public interface EventConsumer<EVENT extends Event> extends Consumer<EVENT> {
}
