package com.mygdx.game.crossylane.entities;

import com.badlogic.gdx.Input.Keys;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.io.InputComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * The player-controlled pedestrian entity.
 *
 * Movement is grid-based: one GRID_STEP per key press.
 * Collision is a trigger — the game scene handles what happens on hit.
 *
 * Components attached:
 *  - TransformComponent   : position and size
 *  - CollisionComponent   : trigger, LAYER_PLAYER, collides with cars/goal/coins/safe zones
 *  - InputComponent       : arrow key bindings for grid movement
 *  - RenderableComponent  : blue rectangle visual
 */
public class PlayerEntity extends Entity {

    public PlayerEntity(float x, float y) {
        addComponent(new TransformComponent(x, y,
                CrossyLaneConfig.PLAYER_WIDTH,
                CrossyLaneConfig.PLAYER_HEIGHT));

        // Trigger collision — no physics resolution, game logic decides outcome
        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_PLAYER,
                CrossyLaneConfig.MASK_PLAYER,
                true));

        // Grid-based input: one step per key press (bindJustPressed = edge-triggered)
        InputComponent input = new InputComponent();
        input.bindJustPressed(Keys.UP,    (entity, dt) -> move(entity,  0,  CrossyLaneConfig.GRID_STEP));
        input.bindJustPressed(Keys.DOWN,  (entity, dt) -> move(entity,  0, -CrossyLaneConfig.GRID_STEP));
        input.bindJustPressed(Keys.LEFT,  (entity, dt) -> move(entity, -CrossyLaneConfig.GRID_STEP, 0));
        input.bindJustPressed(Keys.RIGHT, (entity, dt) -> move(entity,  CrossyLaneConfig.GRID_STEP, 0));
        addComponent(input);

        // Blue rectangle to represent the player
        addComponent(RenderableComponent.rectangle(0.2f, 0.4f, 0.9f, 1f));
    }

    /**
     * Moves the player by (dx, dy), clamped within world bounds.
     * Called from InputComponent lambda — keeps movement logic inside the entity.
     */
    private static void move(Entity entity, float dx, float dy) {
        TransformComponent t = entity.getComponent(TransformComponent.class);
        if (t == null) return;

        float newX = t.getPositionX() + dx;
        float newY = t.getPositionY() + dy;

        // Clamp to screen edges
        newX = Math.max(0f, Math.min(newX, CrossyLaneConfig.WORLD_WIDTH  - t.getWidth()));
        newY = Math.max(0f, Math.min(newY, CrossyLaneConfig.WORLD_HEIGHT - t.getHeight()));

        t.setPosition(newX, newY);
    }

    /**
     * Resets the player back to the starting spawn position.
     * Called by the game scene on level restart or death.
     */
    public void resetPosition() {
        TransformComponent t = getComponent(TransformComponent.class);
        if (t != null) {
            t.setPosition(CrossyLaneConfig.PLAYER_START_X, CrossyLaneConfig.PLAYER_START_Y);
        }
    }
}
