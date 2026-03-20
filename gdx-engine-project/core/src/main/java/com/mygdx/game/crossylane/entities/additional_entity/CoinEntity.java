package com.mygdx.game.crossylane.entities.additional_entity;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.render.TextureComponent;

/**
 * A collectible coin placed on a road lane.
 * Grants a score bonus when the player walks over it.
 *
 * Components attached:
 *  - TransformComponent  : position and size
 *  - CollisionComponent  : trigger, LAYER_COIN, only detects LAYER_PLAYER
 *  - TextureComponent    : coin sprite
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

        // Coin texture
        addComponent(new TextureComponent("coin.png"));
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
