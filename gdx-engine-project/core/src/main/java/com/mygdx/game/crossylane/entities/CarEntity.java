package com.mygdx.game.crossylane.entities;

import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.render.RenderableComponent;
import com.mygdx.game.engine.ecs.TransformComponent;

public class CarEntity extends Entity {

    private final float speed;
    private final int direction; // 1 = left to right, -1 = right to left

    public CarEntity(float x, float y, float width, float height, float speed, int direction) {
        this.speed = speed;
        this.direction = direction;

        addComponent(new TransformComponent(x, y, width, height));
        addComponent(new PhysicsComponent(speed * direction, 0f, 1f));
        addComponent(new CollisionComponent(1, false));
        addComponent(RenderableComponent.rectangle(0.9f, 0.3f, 0.2f, 1f));
    }

    public float getSpeed() {
        return speed;
    }

    public int getDirection() {
        return direction;
    }
}