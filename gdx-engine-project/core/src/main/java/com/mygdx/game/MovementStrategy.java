package com.mygdx.game;

public interface MovementStrategy {
    void applyMovement(TransformComponent transform, PhysicsComponent physics, float deltaTime);
}
