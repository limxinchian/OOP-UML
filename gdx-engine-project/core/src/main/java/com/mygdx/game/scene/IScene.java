package com.mygdx.game.scene;

public interface IScene {
    SceneType getType();

    void onEnter();

    void onExit();

    void update(float delta);

    void render();

    void dispose();
}
