package com.mygdx.game.crossylane.event;

import com.mygdx.game.engine.entity.Entity;
import com.mygdx.game.engine.event.GameEvent;

/**
 * Published when the player entity collides with a car.
 * Subscribers (e.g. GameplayScene) handle life-loss and respawn logic.
 */
public class PlayerHitEvent implements GameEvent {

    private final Entity player;

    public PlayerHitEvent(Entity player) {
        this.player = player;
    }

    public Entity getPlayer() {
        return player;
    }
}
