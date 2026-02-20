package com.mygdx.game.engine.scene;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * SceneManager (engine-level, non-contextual).
 *
 * Supports:
 * - register/unregister scenes
 * - start / change scene
 * - stack-based scenes (push/pop) for overlays like Pause menus
 * - a "previous scene key" for simple resume logic
 *
 * Lifecycle note:
 * Registered scenes are treated as reusable instances.
 * We call onExit()/onEnter() on transitions, but only call dispose() on:
 * - unregisterScene(..., true)
 * - SceneManager.dispose()
 */
public class SceneManager<K> {

    private final Map<K, IScene<K>> scenes = new HashMap<>();
    private final Deque<K> sceneStack = new ArrayDeque<>();

    private IScene<K> currentScene;
    private K previousSceneKey;

    private boolean started = false;
    private boolean disposed = false;
    private boolean transitioning = false;

    public void registerScene(IScene<K> scene) {
        ensureNotDisposed();

        if (scene == null) throw new IllegalArgumentException("scene cannot be null");
        K key = scene.getKey();
        if (key == null) throw new IllegalArgumentException("scene key cannot be null");

        IScene<K> existing = scenes.get(key);
        if (existing == scene) return; // same instance already registered

        // If replacing an existing scene with same key, dispose the old one safely (unless it's current).
        if (existing != null) {
            // Remove key from stack to avoid stale references.
            sceneStack.removeIf(k -> Objects.equals(k, key));

            if (currentScene != null && Objects.equals(currentScene.getKey(), key)) {
                safeOnExit(currentScene);
                currentScene = null;
                started = false;
            }

            safeDispose(existing);
        }

        scenes.put(key, scene);
    }

    /**
     * Remove a scene from the registry.
     * If the removed scene is currently active, it will be exited and cleared.
     */
    public void unregisterScene(K key, boolean dispose) {
        if (key == null || disposed) return;

        // Remove any stack refs to this scene key.
        sceneStack.removeIf(k -> Objects.equals(k, key));

        IScene<K> removed = scenes.remove(key);
        if (removed == null) return;

        if (currentScene != null && Objects.equals(currentScene.getKey(), key)) {
            safeOnExit(currentScene);
            currentScene = null;
            started = false;
        }

        if (Objects.equals(previousSceneKey, key)) {
            previousSceneKey = null;
        }

        if (dispose) {
            safeDispose(removed);
        }
    }

    /**
     * Start the scene manager on the given scene.
     * Intended to be called once at application startup.
     */
    public void start(K key) {
        ensureNotDisposed();

        if (started || currentScene != null) {
            throw new IllegalStateException("SceneManager already started");
        }

        IScene<K> first = requireScene(key);
        currentScene = first;
        safeOnEnter(currentScene);
        started = true;
    }

    /**
     * Change to a new scene (replaces current scene, does NOT affect stack).
     */
    public void changeScene(K key) {
        ensureNotDisposed();
        IScene<K> nextScene = requireScene(key);

        if (currentScene != null && Objects.equals(currentScene.getKey(), key)) {
            return; // already on target scene
        }

        if (transitioning) {
            throw new IllegalStateException("Re-entrant scene transition detected");
        }

        transitioning = true;
        try {
            if (currentScene != null) {
                previousSceneKey = currentScene.getKey();
                safeOnExit(currentScene);
            }

            currentScene = nextScene;
            safeOnEnter(currentScene);
            started = true;
        } finally {
            transitioning = false;
        }
    }

    /**
     * Clear the stack, then change scene.
     * Use this when you want a "hard reset" navigation (e.g., main menu, restart game).
     */
    public void resetTo(K key) {
        ensureNotDisposed();

        clearStack();

        if (!started || currentScene == null) {
            start(key);
            return;
        }

        changeScene(key);
    }

    /**
     * Push current scene onto stack, then change to the given scene.
     * Useful for Pause/Menu overlays.
     */
    public void pushScene(K key) {
        ensureNotDisposed();
        IScene<K> nextScene = requireScene(key);

        if (currentScene != null) {
            // Avoid pushing duplicate top key in weird repeated calls
            K currentKey = currentScene.getKey();
            if (sceneStack.isEmpty() || !Objects.equals(sceneStack.peek(), currentKey)) {
                sceneStack.push(currentKey);
            }
        }

        // Use direct transition to already-resolved scene to avoid double lookups
        changeScene(nextScene.getKey());
    }

    /**
     * Pop the previous scene from stack and change to it.
     * Safe no-op if the stack is empty.
     */
    public void popScene() {
        if (disposed) return;

        if (sceneStack.isEmpty()) {
            return; // safe no-op instead of crashing
        }

        K previous = sceneStack.pop();
        changeScene(previous);
    }

    public void clearStack() {
        sceneStack.clear();
    }

    public void update(float delta) {
        if (disposed) return;
        if (currentScene != null) currentScene.update(delta);
    }

    public void afterWorldUpdate(float delta) {
        if (disposed) return;
        if (currentScene != null) currentScene.afterWorldUpdate(delta);
    }

    public void render() {
        if (disposed) return;
        if (currentScene != null) currentScene.render();
    }

    public void dispose() {
        if (disposed) return;

        // Exit current active scene once before disposing everything.
        if (currentScene != null) {
            safeOnExit(currentScene);
        }

        // Dispose all registered scenes exactly once.
        Set<IScene<K>> uniqueScenes = new HashSet<>(scenes.values());
        for (IScene<K> scene : uniqueScenes) {
            safeDispose(scene);
        }

        scenes.clear();
        sceneStack.clear();
        currentScene = null;
        previousSceneKey = null;
        started = false;
        transitioning = false;
        disposed = true;
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

    public boolean isStarted() {
        return started;
    }

    public boolean isDisposed() {
        return disposed;
    }

    private IScene<K> requireScene(K key) {
        if (key == null) throw new IllegalArgumentException("Scene key cannot be null");
        IScene<K> scene = scenes.get(key);
        if (scene == null) throw new IllegalStateException("Scene not registered: " + key);
        return scene;
    }

    private void ensureNotDisposed() {
        if (disposed) {
            throw new IllegalStateException("SceneManager is already disposed");
        }
    }

    private void safeOnEnter(IScene<K> scene) {
        if (scene == null) return;
        scene.onEnter();
    }

    private void safeOnExit(IScene<K> scene) {
        if (scene == null) return;
        scene.onExit();
    }

    private void safeDispose(IScene<K> scene) {
        if (scene == null) return;
        scene.dispose();
    }
}
