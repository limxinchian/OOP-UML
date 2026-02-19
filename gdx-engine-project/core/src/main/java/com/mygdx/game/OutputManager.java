package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class OutputManager {

    private SpriteBatch batch;

    public OutputManager() {
        this.batch = new SpriteBatch();
    }

    public void begin() {
        batch.begin();
    }

    public void end() {
        batch.end();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void dispose() {
        batch.dispose();
    }
}