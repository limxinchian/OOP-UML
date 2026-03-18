package com.mygdx.game.crossylane.entities.additional_entity;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * A collectible coin placed on a lane or safe zone.
 * Grants a score bonus when the player walks over it.
 *
 * Components attached:
 *  - TransformComponent  : position and size
 *  - CollisionComponent  : trigger, LAYER_COIN, only detects LAYER_PLAYER
 *  - RenderableComponent : gold circle
 */
public class CoinEntity extends Entity {

    private boolean collected = false;

    public CoinEntity(float x, float y) {
        float size = CrossyLaneConfig.COIN_SIZE;

        addComponent(new TransformComponent(x, y, size, size));

        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_COIN,
                CrossyLaneConfig.MASK_COIN,
                true));

        // Gold circle
        addComponent(RenderableComponent.circle(size / 2f, 1f, 0.84f, 0f, 1f));
    }

    /** @return true if this coin has already been picked up */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Marks the coin as collected and hides it from the world.
     * Called by game scene logic when player overlaps this entity.
     */
    public void collect() {
        this.collected = true;
        setActive(false);
    }
}
