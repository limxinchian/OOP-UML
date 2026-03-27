package com.mygdx.game.engine.math;

public class Vector2 {
    private float x;
    private float y;

    public Vector2() {
        this(0f, 0f);
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2 other) {
        if (other == null) {
            this.x = 0f;
            this.y = 0f;
            return;
        }
        this.x = other.x;
        this.y = other.y;
    }

    public void add(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public Vector2 copy() {
        return new Vector2(x, y);
    }
}
