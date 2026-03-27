package com.mygdx.game.engine.movement;

import com.mygdx.game.engine.entity.PhysicsComponent;
import com.mygdx.game.engine.entity.TransformComponent;

public class BasicMovementStrategy implements MovementStrategy {
    @Override
    public void applyMovement(TransformComponent transform, PhysicsComponent physics, float deltaTime) {
        transform.translate(physics.getVelocityX() * deltaTime, physics.getVelocityY() * deltaTime);
    }
}
