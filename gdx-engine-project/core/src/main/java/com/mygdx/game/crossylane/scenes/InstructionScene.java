package com.mygdx.game.crossylane.scenes;

import com.mygdx.game.crossylane.ui.ScreenTextOverlay;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class InstructionScene implements IScene<CrossyLaneSceneKey> {
    private final SceneManager<CrossyLaneSceneKey> sceneManager;
    private final IOManager ioManager;
    private final ScreenTextOverlay screenTextOverlay = new ScreenTextOverlay();

    public InstructionScene(SceneManager<CrossyLaneSceneKey> sceneManager, IOManager ioManager) {
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.INSTRUCTIONS;
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onExit() {
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void afterWorldUpdate(float delta) {
    }

    @Override
    public void render() {
        ioManager.getOutput().beginFrame(0.1f, 0.1f, 0.14f, 1f);
        ioManager.getOutput().endFrame();
        ioManager.getOutput().beginTextOverlay();
        screenTextOverlay.renderInstructions(ioManager.getOutput());
        ioManager.getOutput().endTextOverlay();
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean updatesWorld() {
        return false;
    }
}
