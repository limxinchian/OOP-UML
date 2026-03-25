package com.mygdx.game.engine.render;

import com.mygdx.game.engine.ecs.Component;

/**
 * Minimal render metadata for OutputManager to draw.
 * Uses ShapeRenderer (filled).
 */
public class RenderableComponent extends Component {

    private final RenderShape shape;

    // RGBA
    private float r, g, b, a;

    // Only used when shape == CIRCLE
    private float radius;

    private RenderableComponent(RenderShape shape) {
        this.shape = shape;
        this.r = 1f; this.g = 1f; this.b = 1f; this.a = 1f;
        this.radius = 0f;
    }

    public static RenderableComponent rectangle(float r, float g, float b, float a) {
        RenderableComponent c = new RenderableComponent(RenderShape.RECTANGLE);
        c.setColor(r, g, b, a);
        return c;
    }

    public static RenderableComponent circle(float radius, float r, float g, float b, float a) {
        RenderableComponent c = new RenderableComponent(RenderShape.CIRCLE);
        c.radius = radius;
        c.setColor(r, g, b, a);
        return c;
    }

    public RenderShape getShape() {
        return shape;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setColor(float r, float g, float b, float a) {
        this.r = clamp01(r);
        this.g = clamp01(g);
        this.b = clamp01(b);
        this.a = clamp01(a);
    }

    public float r() { return r; }
    public float g() { return g; }
    public float b() { return b; }
    public float a() { return a; }

    @Override
    public void update(float deltaTime) {
        // Render handled by OutputManager
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}
