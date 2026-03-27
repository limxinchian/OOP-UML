package com.mygdx.game.crossylane.entity;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.entity.Entity;
import com.mygdx.game.engine.entity.TransformComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * A safe waiting island in the middle of the road.
 * The player can stand here safely — cars do not collide with this zone.
 *
 * Components attached:
 *  - TransformComponent  : custom size island
 *  - CollisionComponent  : trigger, LAYER_SAFE, only detects LAYER_PLAYER
 *  - RenderableComponent : pale green island
 */
public class SafeStopZoneEntity extends Entity {

    public SafeStopZoneEntity(float x, float y, float width, float height) {
        addComponent(new TransformComponent(x, y, width, height));

        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_SAFE,
                CrossyLaneConfig.MASK_SAFE,
                true));

        // Pale green — clearly a safe area
        addComponent(RenderableComponent.rectangle(0.5f, 0.85f, 0.5f, 0.9f));
    }
}