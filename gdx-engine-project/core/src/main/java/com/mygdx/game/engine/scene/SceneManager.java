package com.mygdx.game.engine.scene;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * SceneManager (engine-level, non-contextual).
 *
 * Supports:
 * - register/unregister scenes
 * - start / change scene
 * - stack-based scenes (push/pop) for overlays like Pause menus
 * - a "previous scene key" for simple resume logic
 */
public class SceneManager<K> {

    private final Map<K, IScene<K>> scenes = new HashMap<>();
    private final Deque<K> sceneStack = new ArrayDeque<>();

    private IScene<K> currentScene;
    private K previousSceneKey;

    public void registerScene(IScene<K> scene) {
        if (scene == null) throw new IllegalArgumentException("scene cannot be null");
        K key = scene.getKey();
        if (key == null) throw new IllegalArgumentException("scene key cannot be null");
        scenes.put(key, scene);
    }

    /**
     * Remove a scene from the registry.
     * If the removed scene is currently active, it will be exited and cleared.
     */
    public void unregisterScene(K key, boolean dispose) {
        if (key == null) return;

        // Remove from stack if present
        sceneStack.removeIf(k -> Objects.equals(k, key));

        IScene<K> removed = scenes.remove(key);
        if (removed == null) return;

        if (currentScene != null && Objects.equals(currentScene.getKey(), key)) {
            currentScene.onExit();
            currentScene = null;
        }

        if (dispose) removed.dispose();
    }

    /**
     * Start the scene manager on the given scene.
     * Intended to be called once at application startup.
     */
    public void start(K key) {
        if (currentScene != null) throw new IllegalStateException("SceneManager already started");
        currentScene = requireScene(key);
        currentScene.onEnter();
    }

    /**
     * Change to a new scene (replaces current scene, does NOT affect stack).
     */
    public void changeScene(K key) {
        IScene<K> nextScene = requireScene(key);

        if (currentScene != null && Objects.equals(currentScene.getKey(), key)) {
            // no-op: already on that scene
            return;
        }

        if (currentScene != null) {
            previousSceneKey = currentScene.getKey();
            currentScene.onExit();
        }

        currentScene = nextScene;
        currentScene.onEnter();
    }

    /**
     * Clear the stack, then change scene.
     * Use this when you want a "hard reset" navigation (e.g., main menu, restart game).
     */
    public void resetTo(K key) {
        clearStack();
        if (currentScene == null) {
            start(key);
        } else {
            changeScene(key);
        }
    }

    /**
     * Push current scene onto stack, then change to the given scene.
     * Useful for Pause/Menu overlays.
     */
    public void pushScene(K key) {
        if (currentScene != null) {
            sceneStack.push(currentScene.getKey());
        }
        changeScene(key);
    }

    /**
     * Pop the previous scene from stack and change to it.
     */
    public void popScene() {
        if (sceneStack.isEmpty()) {
            throw new IllegalStateException("Scene stack is empty - cannot popScene()");
        }
        K previous = sceneStack.pop();
        changeScene(previous);
    }

    public void clearStack() {
        sceneStack.clear();
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
        for (IScene<K> scene : scenes.values()) scene.dispose();
        scenes.clear();
        sceneStack.clear();
        currentScene = null;
        previousSceneKey = null;
    }

    public K getPreviousSceneKey() {
        return previousSceneKey;
    }

    public IScene<K> getCurrentScene() {
        return currentScene;
    }

    public int getRegisteredSceneCount() {
        return scenes.size();
    }

    public int getStackDepth() {
        return sceneStack.size();
    }

    private IScene<K> requireScene(K key) {
        if (key == null) throw new IllegalArgumentException("Scene key cannot be null");
        IScene<K> scene = scenes.get(key);
        if (scene == null) throw new IllegalStateException("Scene not registered: " + key);
        return scene;
    }
}
