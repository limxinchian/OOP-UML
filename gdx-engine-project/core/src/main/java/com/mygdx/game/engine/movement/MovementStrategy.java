package com.mygdx.game.engine.movement;

import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.ecs.TransformComponent;

public interface MovementStrategy {
    void applyMovement(TransformComponent transform, PhysicsComponent physics, float deltaTime);
}
