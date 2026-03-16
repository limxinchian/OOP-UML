package com.mygdx.game.crossylane.scenes;

import com.mygdx.game.crossylane.ui.ScreenTextOverlay;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class MainMenuScene implements IScene<CrossyLaneSceneKey> {
    private final SceneManager<CrossyLaneSceneKey> sceneManager;
    private final IOManager ioManager;
    private final ScreenTextOverlay screenTextOverlay = new ScreenTextOverlay();

    public MainMenuScene(SceneManager<CrossyLaneSceneKey> sceneManager, IOManager ioManager) {
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.MAIN_MENU;
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
        ioManager.getOutput().beginFrame(0.08f, 0.12f, 0.16f, 1f);
        ioManager.getOutput().endFrame();
        ioManager.getOutput().beginTextOverlay();
        screenTextOverlay.renderMainMenu(ioManager.getOutput());
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
