package com.mygdx.game.engine.managers;

import com.mygdx.game.engine.io.InputManager;
import com.mygdx.game.engine.render.OutputManager;

/**
 * Engine-level IO facade:
 * - InputManager updates InputComponents
 * - OutputManager renders RenderableComponents
 *
 * No context-specific game logic belongs here.
 */
public class IOManager implements IManager {

    private final InputManager inputManager;
    private final OutputManager outputManager;

    public IOManager(EntityManager entityManager) {
        this.inputManager = new InputManager(entityManager);
        this.outputManager = new OutputManager();
    }

    public InputManager getInput() {
        return inputManager;
    }

    public OutputManager getOutput() {
        return outputManager;
    }

    @Override
    public void initialize() {
        inputManager.initialize();
        outputManager.initialize();
    }

    @Override
    public void update(float deltaTime) {
        inputManager.update(deltaTime);
    }

    @Override
    public void shutdown() {
        inputManager.shutdown();
        outputManager.dispose();
    }
}
