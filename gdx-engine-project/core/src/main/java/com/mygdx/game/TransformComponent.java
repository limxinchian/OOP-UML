package com.mygdx.game;

public class TransformComponent extends Component {
    public float positionX;
    public float positionY;
    public float width;
    public float height;
    public float rotation;

    public TransformComponent(float x, float y, float w, float h) {
        this.positionX = x;
        this.positionY = y;
        this.width = w;
        this.height = h;
        this.rotation = 0.0f;
    }
}
