package com.mygdx.game.crossylane.systems;

import java.util.List;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.entities.CarEntity;
import com.mygdx.game.engine.ecs.TransformComponent;

/**
 * Handles screen-edge wrapping for vehicle entities.
 *
 * Extracted from GameplayScene to satisfy the Single Responsibility Principle:
 * the scene orchestrates gameplay flow; this class owns the wrap-around rule.
 *
 * Reusable — any entity list with a direction and transform can use this.
 */
public class CarWrapSystem {

    private CarWrapSystem() {}

    /**
     * Wraps all cars so they reappear on the opposite side when they
     * leave the screen.
     *
     * @param cars       the vehicles to check
     * @param worldWidth the screen width boundary
     */
    public static void wrapAll(List<CarEntity> cars, float worldWidth) {
        for (CarEntity car : cars) {
            TransformComponent transform = car.getComponent(TransformComponent.class);
            if (transform == null) continue;

            if (car.getDirection() == 1 && transform.getPositionX() > worldWidth) {
                transform.setPositionX(-transform.getWidth());
            }

            if (car.getDirection() == -1 && transform.getRight() < 0) {
                transform.setPositionX(worldWidth);
            }
        }
    }
}
