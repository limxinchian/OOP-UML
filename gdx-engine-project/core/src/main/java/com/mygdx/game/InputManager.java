package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class InputManager {

    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }

    public boolean isKeyJustPressed(int key) {
        return Gdx.input.isKeyJustPressed(key);
    }

    public Vector2 getMousePosition() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }
}