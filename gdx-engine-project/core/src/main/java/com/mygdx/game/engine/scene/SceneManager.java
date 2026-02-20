package com.mygdx.game.engine.scene;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private Map<SceneType, IScene> scenes;
    private IScene currentScene;
    private SceneType previousSceneType;

    public SceneManager() {
        scenes = new HashMap<>();
    }

    public void registerScene(IScene scene) {
        scenes.put(scene.getType(), scene);
    }

    public void start(SceneType type) {
        IScene scene = scenes.get(type);
        if (scene == null) throw new IllegalStateException("Scene not registered: " + type);

        currentScene = scene;
        currentScene.onEnter();
    }

    public void changeScene(SceneType type) {
        IScene nextScene = scenes.get(type);
        if (nextScene == null) throw new IllegalStateException("Scene not registered: " + type);

        if (currentScene != null) {
            previousSceneType = currentScene.getType();
            currentScene.onExit();
        }

        currentScene = nextScene;
        currentScene.onEnter();
    }

    public void update(float delta) {
        if (currentScene != null) currentScene.update(delta);
    }

    public void afterWorldUpdate(float delta) {
        if (currentScene != null) currentScene.afterWorldUpdate(delta);
    }

    public void render() {
        if (currentScene != null) currentScene.render();
    }

    public void dispose() {
        for (IScene scene : scenes.values()) scene.dispose();
    }

    public SceneType getPreviousSceneType() {
        return previousSceneType;
    }

    public IScene getCurrentScene() {
        return currentScene;
    }
}
