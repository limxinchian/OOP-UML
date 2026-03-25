package com.mygdx.game.engine.managers;

import java.util.ArrayList;
import java.util.List;

import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.movement.BasicMovementStrategy;
import com.mygdx.game.engine.movement.MovementComponent;
import com.mygdx.game.engine.movement.MovementStrategy;

public class MovementManager implements IManager {
    private final List<MovementStrategy> globalStrategies;
    private final EntityManager entityManager;

    private final MovementStrategy defaultStrategy = new BasicMovementStrategy();

    public MovementManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.globalStrategies = new ArrayList<>();
    }

    @Override
    public void initialize() { }

    // Backwards-compatible: if an entity has no MovementComponent,
    // all global strategies will be applied (or default if none).
    public void addMovementStrategy(MovementStrategy strategy) {
        globalStrategies.add(strategy);
    }

    public void removeMovementStrategy(MovementStrategy strategy) {
        globalStrategies.remove(strategy);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entityManager.getEntities()) {
            if (!entity.isActive()) continue;

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);

            if (transform == null || physics == null || !physics.isEnabled()) continue;

            MovementComponent mc = entity.getComponent(MovementComponent.class);
            if (mc != null && mc.isEnabled() && mc.getStrategy() != null) {
                mc.getStrategy().applyMovement(transform, physics, deltaTime);
                continue;
            }

            if (globalStrategies.isEmpty()) {
                defaultStrategy.applyMovement(transform, physics, deltaTime);
            } else {
                for (MovementStrategy strategy : globalStrategies) {
                    strategy.applyMovement(transform, physics, deltaTime);
                }
            }
        }
    }

    @Override
    public void shutdown() {
        globalStrategies.clear();
    }
}
