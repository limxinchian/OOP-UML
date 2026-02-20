package com.mygdx.game.engine.math;

public class Rectangle {
    private float x;
    private float y;
    private float width;
    private float height;

    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getRight() {
        return x + width;
    }

    public float getTop() {
        return y + height;
    }

    public void set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public boolean overlaps(Rectangle other) {
        if (other == null) return false;
        return x <= other.getRight() &&
               getRight() >= other.getX() &&
               y <= other.getTop() &&
               getTop() >= other.getY();
    }
}
