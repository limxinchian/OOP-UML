package com.mygdx.game.engine.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central publish-subscribe event bus for decoupled communication.
 *
 * Design Pattern: Observer
 * -----------------------------------------------------------------------
 * Publishers call {@link #publish(GameEvent)} with a concrete event.
 * Subscribers register via {@link #subscribe(Class, EventListener)} and
 * receive only events of the requested type.  Neither side knows the other
 * exists, which keeps coupling low and makes it trivial to add new event
 * types without modifying existing code (Open/Closed Principle).
 *
 * The EventBus lives in the engine layer and carries no game-specific
 * knowledge — concrete event types are defined in the game package.
 */
public class EventBus {

    private final Map<Class<? extends GameEvent>, List<EventListener<?>>> listeners = new HashMap<>();

    /**
     * Subscribe to a specific event type.
     *
     * @param eventType the concrete event class to listen for
     * @param listener  callback invoked when that event is published
     * @param <T>       event type
     */
    public <T extends GameEvent> void subscribe(Class<T> eventType, EventListener<T> listener) {
        if (eventType == null) throw new IllegalArgumentException("eventType cannot be null");
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");

        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Unsubscribe a previously registered listener.
     *
     * @param eventType the event class the listener was registered for
     * @param listener  the listener instance to remove
     * @param <T>       event type
     */
    public <T extends GameEvent> void unsubscribe(Class<T> eventType, EventListener<T> listener) {
        if (eventType == null || listener == null) return;

        List<EventListener<?>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
        }
    }

    /**
     * Publish an event to all subscribers of its concrete type.
     * Listeners are invoked synchronously in registration order.
     *
     * @param event the event to broadcast
     * @param <T>   event type
     */
    @SuppressWarnings("unchecked")
    public <T extends GameEvent> void publish(T event) {
        if (event == null) return;

        List<EventListener<?>> list = listeners.get(event.getClass());
        if (list == null || list.isEmpty()) return;

        // Iterate over a snapshot to allow listeners to unsubscribe safely
        for (EventListener<?> raw : new ArrayList<>(list)) {
            ((EventListener<T>) raw).onEvent(event);
        }
    }

    /**
     * Remove all subscriptions. Called during engine shutdown.
     */
    public void clear() {
        listeners.clear();
    }
}
