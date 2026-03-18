package com.mygdx.game.engine.ecs;

public class TransformComponent extends Component {
    private float positionX;
    private float positionY;
    private float width;
    private float height;
    private float rotation;

    public TransformComponent(float x, float y, float w, float h) {
        this.positionX = x;
        this.positionY = y;
        this.width = Math.max(0f, w);
        this.height = Math.max(0f, h);
        this.rotation = 0.0f;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getRotation() {
        return rotation;
    }

    public float getRight() {
        return positionX + width;
    }

    public float getTop() {
        return positionY + height;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public void setPosition(float x, float y) {
        this.positionX = x;
        this.positionY = y;
    }

    public void translate(float dx, float dy) {
        this.positionX += dx;
        this.positionY += dy;
    }

    public void setWidth(float width) {
        this.width = Math.max(0f, width);
    }

    public void setHeight(float height) {
        this.height = Math.max(0f, height);
    }

    public void setSize(float width, float height) {
        this.width = Math.max(0f, width);
        this.height = Math.max(0f, height);
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public void update(float deltaTime) {
        // Transform update handled by MovementManager
    }
}
