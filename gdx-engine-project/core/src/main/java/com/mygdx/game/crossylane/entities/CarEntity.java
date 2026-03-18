package com.mygdx.game.crossylane.entities;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * A moving vehicle entity in a traffic lane.
 *
 * Direction: 1 = left→right, -1 = right→left
 * Speed is in pixels per second.
 *
 * Components attached:
 *  - TransformComponent  : position and size
 *  - PhysicsComponent    : horizontal velocity for BasicMovementStrategy
 *  - CollisionComponent  : solid, LAYER_CAR, collides with LAYER_PLAYER
 *  - RenderableComponent : red-toned rectangle
 */
public class CarEntity extends Entity {

    private final float speed;
    private final int direction;

    public CarEntity(float x, float y, float width, float height, float speed, int direction) {
        this.speed     = speed;
        this.direction = direction;

        addComponent(new TransformComponent(x, y, width, height));
        addComponent(new PhysicsComponent(speed * direction, 0f, 1f));

        // Solid (not trigger) — physics resolution pushes the player back
        // Layer/mask from config keeps collision layers consistent across the game
        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_CAR,
                CrossyLaneConfig.MASK_CAR,
                false));

        // Reddish rectangle — clearly a vehicle threat
        addComponent(RenderableComponent.rectangle(0.9f, 0.3f, 0.2f, 1f));
    }

    public float getSpeed()     { return speed; }
    public int   getDirection() { return direction; }
}
