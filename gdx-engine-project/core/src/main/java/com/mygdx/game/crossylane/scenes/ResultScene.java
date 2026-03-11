package com.mygdx.game.crossylane.scenes;

import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class ResultScene implements IScene<CrossyLaneSceneKey> {
    private final SceneManager<CrossyLaneSceneKey> sceneManager;

    public ResultScene(SceneManager<CrossyLaneSceneKey> sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.RESULT;
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