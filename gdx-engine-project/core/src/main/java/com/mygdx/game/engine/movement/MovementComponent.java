package com.mygdx.game.engine.movement;

import com.mygdx.game.engine.ecs.Component;

/**
 * Optional per-entity movement strategy.
 * If present, MovementManager will prefer this strategy for that entity.
 */
public class MovementComponent extends Component {

    private MovementStrategy strategy;

    public MovementComponent(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    public MovementStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void update(float deltaTime) {
        // Movement handled by MovementManager
    }
}
