package com.mygdx.game.engine.render;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;

public class OutputManager {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    public void initialize() {
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
        }
    }

    public void beginFrame(float r, float g, float b, float a) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void renderEntities(List<Entity> entities) {
        // 1. Draw textured entities first
        spriteBatch.begin();
        for (Entity e : entities) {
            if (!e.isActive())
                continue;

            TransformComponent t = e.getComponent(TransformComponent.class);
            TextureComponent tex = e.getComponent(TextureComponent.class);

            if (t == null || tex == null || !tex.isEnabled())
                continue;

            if (tex.isFlipX()) {
                spriteBatch.draw(
                        tex.getTexture(),
                        t.getPositionX() + t.getWidth(),
                        t.getPositionY(),
                        -t.getWidth(),
                        t.getHeight());
            } else {
                spriteBatch.draw(
                        tex.getTexture(),
                        t.getPositionX(),
                        t.getPositionY(),
                        t.getWidth(),
                        t.getHeight());
            }
        }
        spriteBatch.end();

        // 2. Draw shape-based entities
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity e : entities) {
            if (!e.isActive())
                continue;

            TransformComponent t = e.getComponent(TransformComponent.class);
            RenderableComponent rc = e.getComponent(RenderableComponent.class);
            TextureComponent tex = e.getComponent(TextureComponent.class);

            // If entity already has texture, skip shape rendering
            if (tex != null && tex.isEnabled())
                continue;
            if (t == null || rc == null || !rc.isEnabled())
                continue;

            shapeRenderer.setColor(rc.r(), rc.g(), rc.b(), rc.a());

            if (rc.getShape() == RenderShape.RECTANGLE) {
                shapeRenderer.rect(t.getPositionX(), t.getPositionY(), t.getWidth(), t.getHeight());
            } else if (rc.getShape() == RenderShape.CIRCLE) {
                float radius = rc.getRadius();
                shapeRenderer.circle(t.getPositionX() + radius, t.getPositionY() + radius, radius);
            }
        }
        shapeRenderer.end();
    }

    public void endFrame() {
        // no-op, kept for compatibility
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
    }
}