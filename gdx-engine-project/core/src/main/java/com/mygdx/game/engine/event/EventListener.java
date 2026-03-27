package com.mygdx.game.engine.event;

/**
 * Functional interface for subscribing to a specific event type.
 *
 * @param <T> the concrete event type this listener handles
 */
@FunctionalInterface
public interface EventListener<T extends GameEvent> {
    void onEvent(T event);
}
