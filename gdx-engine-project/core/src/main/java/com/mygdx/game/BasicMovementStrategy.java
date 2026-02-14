package com.mygdx.game;

public class BasicMovementStrategy implements MovementStrategy {
    @Override
    public void applyMovement(TransformComponent transform, PhysicsComponent physics, float deltaTime) {
        transform.positionX += physics.velocityX * deltaTime;
        transform.positionY += physics.velocityY * deltaTime;
    }
}
