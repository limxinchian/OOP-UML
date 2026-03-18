package com.mygdx.game.crossylane.scenes;

import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class PauseScene implements IScene<CrossyLaneSceneKey> {
    private final SceneManager<CrossyLaneSceneKey> sceneManager;

    public PauseScene(SceneManager<CrossyLaneSceneKey> sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.PAUSE;
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
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean updatesWorld() {
        return false;
    }
}