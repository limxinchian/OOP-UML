package com.mygdx.game.crossylane.entities;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * The safe goal area at the top of the screen.
 * When the player enters this zone, the level is won.
 *
 * Components attached:
 *  - TransformComponent  : position and size
 *  - CollisionComponent  : trigger, LAYER_GOAL, only detects LAYER_PLAYER
 *  - RenderableComponent : bright yellow strip
 */
public class GoalZoneEntity extends Entity {

    public GoalZoneEntity(float x, float y, float width, float height) {
        addComponent(new TransformComponent(x, y, width, height));

        // Trigger only — game scene handles the win condition on overlap
        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_GOAL,
                CrossyLaneConfig.MASK_GOAL,
                true));

        // Bright yellow: clearly signals the safe destination
        addComponent(RenderableComponent.rectangle(1f, 0.9f, 0.1f, 1f));
    }
}
