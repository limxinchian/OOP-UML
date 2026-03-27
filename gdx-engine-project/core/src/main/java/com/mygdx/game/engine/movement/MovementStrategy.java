package com.mygdx.game.engine.movement;

import com.mygdx.game.engine.entity.PhysicsComponent;
import com.mygdx.game.engine.entity.TransformComponent;

public interface MovementStrategy {
    void applyMovement(TransformComponent transform, PhysicsComponent physics, float deltaTime);
}
