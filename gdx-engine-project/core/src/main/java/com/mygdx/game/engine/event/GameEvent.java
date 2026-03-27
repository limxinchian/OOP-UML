package com.mygdx.game.engine.event;

/**
 * Marker interface for all events published through the engine's EventBus.
 *
 * Game-specific events implement this interface and carry whatever payload
 * the subscriber needs. The engine never inspects the concrete type — it only
 * routes by class, keeping the event system fully game-agnostic.
 */
public interface GameEvent {
}
