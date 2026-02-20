package com.mygdx.game.engine.scene;

public interface IScene {
    SceneType getType();

    void onEnter();
    void onExit();

    /**
     * Pre-world update: game/demo logic (spawn, gravity, scene switching inputs, etc.)
     */
    void update(float delta);

    /**
     * Post-world update: run checks that depend on world results
     * (e.g., collision -> game over).
     */
    default void afterWorldUpdate(float delta) { }

    /**
     * Whether the engine should run world managers this frame.
     * Pause scenes should return false.
     */
    default boolean updatesWorld() { return true; }

    void render();
    void dispose();
}
