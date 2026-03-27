package com.mygdx.game.crossylane.entity;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.entity.Entity;
import com.mygdx.game.engine.entity.TransformComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * A zebra crossing — a safe crossing zone where traffic yields.
 * Triggers a "safe crossing bonus" when the player uses it.
 *
 * Components attached:
 *  - TransformComponent  : spans full lane width
 *  - CollisionComponent  : trigger, LAYER_SAFE, only detects LAYER_PLAYER
 *  - RenderableComponent : light grey (simulates white stripes)
 */
public class ZebraCrossingEntity extends Entity {

    public ZebraCrossingEntity(float x, float y, float width) {
        addComponent(new TransformComponent(x, y, width, CrossyLaneConfig.LANE_HEIGHT));

        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_SAFE,
                CrossyLaneConfig.MASK_SAFE,
                true));

        // Light grey — represents painted white crossing stripes
        addComponent(RenderableComponent.rectangle(0.88f, 0.88f, 0.88f, 0.85f));
    }
}