package com.mygdx.game.engine.io;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IManager;

/**
 * Engine-level input manager:
 * - Reads raw key state from LibGDX
 * - Executes Commands stored in InputComponent
 * No context/game-specific logic here.
 */
public class InputManager implements IManager {

    private final EntityManager entityManager;

    public InputManager(EntityManager entityManager) {
        if (entityManager == null) throw new IllegalArgumentException("entityManager cannot be null");
        this.entityManager = entityManager;
    }

    @Override
    public void initialize() { }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entityManager.getEntities()) {
            if (!entity.isActive()) continue;

            InputComponent input = entity.getComponent(InputComponent.class);
            if (input == null || !input.isEnabled()) continue;

            // Edge-triggered
            for (Map.Entry<Integer, Command> e : input.getPressBindings().entrySet()) {
                if (Gdx.input.isKeyJustPressed(e.getKey())) {
                    e.getValue().execute(entity, deltaTime);
                }
            }

            // Level-triggered
            for (Map.Entry<Integer, Command> e : input.getHoldBindings().entrySet()) {
                if (Gdx.input.isKeyPressed(e.getKey())) {
                    e.getValue().execute(entity, deltaTime);
                }
            }
        }
    }

    @Override
    public void shutdown() { }
}
