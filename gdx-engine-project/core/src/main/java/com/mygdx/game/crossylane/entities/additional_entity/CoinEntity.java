package com.mygdx.game.crossylane.entities.additional_entity;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.events.CoinCollectedEvent;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.event.EventBus;
import com.mygdx.game.engine.render.TextureComponent;

/**
 * A collectible coin placed on a road lane.
 * Grants a score bonus when the player walks over it.
 *
 * Refactor notes (Part 2):
 * - Coin collection now uses the engine's collision system via
 *   CollisionComponent.onCollisionEnter() instead of manual overlap
 *   checking in GameplayScene. This removes ~25 lines of duplicated
 *   rectangle-intersection code from the scene and properly leverages
 *   the engine's broadphase/narrowphase pipeline.
 * - Publishes CoinCollectedEvent via EventBus so any subscriber
 *   (scene, HUD, sound system) can react without coupling.
 *
 * Components attached:
 *  - TransformComponent  : position and size
 *  - CollisionComponent  : trigger, LAYER_COIN, detects LAYER_PLAYER
 *  - TextureComponent    : coin sprite
 */
public class CoinEntity extends Entity {

    private boolean collected = false;
    private final EventBus eventBus;

    public CoinEntity(float x, float y, EventBus eventBus) {
        if (eventBus == null) throw new IllegalArgumentException("eventBus cannot be null");
        this.eventBus = eventBus;

        float size = CrossyLaneConfig.COIN_SIZE;

        addComponent(new TransformComponent(x, y, size, size));

        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_COIN,
                CrossyLaneConfig.MASK_COIN,
                true) {
            @Override
            public void onCollisionEnter(CollisionComponent other) {
                if (other == null || collected) return;

                if (other.getCollisionLayer() == CrossyLaneConfig.LAYER_PLAYER) {
                    collect();
                }
            }
        });

        addComponent(new TextureComponent("coin.png"));
    }

    /** @return true if this coin has already been picked up */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Marks the coin as collected, hides it, and publishes an event.
     */
    private void collect() {
        this.collected = true;
        setActive(false);
        eventBus.publish(new CoinCollectedEvent(this));
    }
}
