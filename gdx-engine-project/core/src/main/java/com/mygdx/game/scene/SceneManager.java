package com.mygdx.game.scene;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private Map<SceneType, IScene> scenes;
    private IScene currentScene;
    private SceneType previousSceneType;

    public SceneManager() {
        scenes = new HashMap<>();
    }

    // Register a scene
    public void registerScene(IScene scene) {
        scenes.put(scene.getType(), scene);
    }

    // Start the first scene
    public void start(SceneType type) {
        IScene scene = scenes.get(type);

        if (scene == null) {
            throw new IllegalStateException("Scene not registered: " + type);
        }

        currentScene = scene;
        currentScene.onEnter();
    }

    // Change scene
    public void changeScene(SceneType type) {

        IScene nextScene = scenes.get(type);

        if (nextScene == null) {
            throw new IllegalStateException("Scene not registered: " + type);
        }

        if (currentScene != null) {
            previousSceneType = currentScene.getType();  // Save previous scene
            currentScene.onExit();
        }

        currentScene = nextScene;
        currentScene.onEnter();
    }

    // Update current scene
    public void update(float delta) {
        if (currentScene != null) {
            currentScene.update(delta);
        }
    }

    // Render current scene
    public void render() {
        if (currentScene != null) {
            currentScene.render();
        }
    }

    // Dispose all scenes
    public void dispose() {
        for (IScene scene : scenes.values()) {
            scene.dispose();
        }
    }

    // Get previous scene (used for resume logic)
    public SceneType getPreviousSceneType() {
        return previousSceneType;
    }
}

