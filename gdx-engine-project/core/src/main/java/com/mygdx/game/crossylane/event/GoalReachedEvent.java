package com.mygdx.game.crossylane.event;

import com.mygdx.game.engine.entity.Entity;
import com.mygdx.game.engine.event.GameEvent;

/**
 * Published when the player entity enters the goal zone.
 * Subscribers handle level completion, score bonus, and scene transition.
 */
public class GoalReachedEvent implements GameEvent {

    private final Entity player;

    public GoalReachedEvent(Entity player) {
        this.player = player;
    }

    public Entity getPlayer() {
        return player;
    }
}
