package com.mygdx.game;
import java.util.ArrayList;
import java.util.List;

public class MovementManager implements IManager {
    private List<MovementStrategy> strategies;
    private EntityManager entityManager;

    public MovementManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.strategies = new ArrayList<>();
    }

    @Override
    public void initialize() { }

    public void addMovementStrategy(MovementStrategy strategy) {
        strategies.add(strategy);
    }

    public void removeMovementStrategy(MovementStrategy strategy) {
        strategies.remove(strategy);
    }

    public void updateMovement(float deltaTime) {
        for (Entity entity : entityManager.getEntities()) { 
            if (!entity.isActive()) continue;

            TransformComponent transform = (TransformComponent) entity.getComponent(TransformComponent.class);
            PhysicsComponent physics = (PhysicsComponent) entity.getComponent(PhysicsComponent.class);

            if (transform != null && physics != null && physics.isEnabled()) {
                for (MovementStrategy strategy : strategies) {
                    strategy.applyMovement(transform, physics, deltaTime);
                }
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        updateMovement(deltaTime);
    }

    @Override
    public void shutdown() {
        strategies.clear();
    }
}
