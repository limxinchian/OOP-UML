package com.mygdx.game.engine.movement;

import java.util.List;

import com.mygdx.game.engine.entity.Entity;
import com.mygdx.game.engine.entity.PhysicsComponent;
import com.mygdx.game.engine.entity.TransformComponent;

/**
 * Engine-level screen-edge wrapping for any entity with Transform + Physics.
 *
 * Entities that leave the screen on one side are repositioned to the opposite
 * side.  Direction is inferred from the entity's velocity in PhysicsComponent,
 * so no game-specific subclass or interface is required.
 *
 * Refactor note (Part 2):
 * Generalised from the game-level CarWrapSystem (which was typed to CarEntity)
 * into a reusable engine utility.  Any game built on this engine can now use
 * screen-wrapping for projectiles, enemies, asteroids, etc.
 *
 * Addresses: Scalability, Low Coupling, Open/Closed Principle.
 */
public final class ScreenWrapSystem {

    private ScreenWrapSystem() { }

    /**
     * Wraps all entities in the list that have moved beyond the horizontal
     * screen boundary back to the opposite side.
     *
     * @param entities   the entities to check (null-safe, skips inactive)
     * @param worldWidth the screen width boundary
     */
    public static void wrapHorizontal(List<? extends Entity> entities, float worldWidth) {
        if (entities == null) return;

        for (Entity entity : entities) {
            if (entity == null || !entity.isActive()) continue;

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
            if (transform == null || physics == null) continue;

            // Moving right and fully off-screen right → reappear on the left
            if (physics.getVelocityX() > 0f && transform.getPositionX() > worldWidth) {
                transform.setPositionX(-transform.getWidth());
            }

            // Moving left and fully off-screen left → reappear on the right
            if (physics.getVelocityX() < 0f && transform.getRight() < 0f) {
                transform.setPositionX(worldWidth);
            }
        }
    }
}
