package com.mygdx.game.crossylane.systems;

import java.util.List;

import com.mygdx.game.crossylane.entities.CarEntity;
import com.mygdx.game.engine.movement.ScreenWrapSystem;

/**
 * Game-level convenience wrapper around the engine's ScreenWrapSystem.
 *
 * Refactor note (Part 2):
 * Previously contained its own wrapping logic typed to CarEntity.
 * Now delegates to the engine-level ScreenWrapSystem, which works
 * with any Entity that has Transform + Physics.  CarWrapSystem is
 * kept as a thin game-level façade for type clarity in GameplayScene.
 *
 * Addresses: Scalability, Low Coupling, DRY.
 */
public final class CarWrapSystem {

    private CarWrapSystem() { }

    /**
     * Wraps all cars so they reappear on the opposite side when they
     * leave the screen.  Delegates to ScreenWrapSystem.
     *
     * @param cars       the vehicles to check
     * @param worldWidth the screen width boundary
     */
    public static void wrapAll(List<CarEntity> cars, float worldWidth) {
        ScreenWrapSystem.wrapHorizontal(cars, worldWidth);
    }
}
