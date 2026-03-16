package com.mygdx.game.engine.render;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;

/**
 * Output side of IO:
 * draws all entities that have TransformComponent + RenderableComponent.
 */
public class OutputManager {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    public void initialize() {
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
        }
        if (font == null) {
            font = new BitmapFont();
        }
    }

    public void beginFrame(float r, float g, float b, float a) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }

    public void renderEntities(List<Entity> entities) {
        for (Entity e : entities) {
            if (!e.isActive()) continue;

            TransformComponent t = e.getComponent(TransformComponent.class);
            RenderableComponent rc = e.getComponent(RenderableComponent.class);

            if (t == null || rc == null || !rc.isEnabled()) continue;

            shapeRenderer.setColor(rc.r(), rc.g(), rc.b(), rc.a());

            if (rc.getShape() == RenderShape.RECTANGLE) {
                shapeRenderer.rect(t.getPositionX(), t.getPositionY(), t.getWidth(), t.getHeight());
            } else if (rc.getShape() == RenderShape.CIRCLE) {
                float radius = rc.getRadius();
                shapeRenderer.circle(t.getPositionX() + radius, t.getPositionY() + radius, radius);
            }
        }
    }

    public void endFrame() {
        shapeRenderer.end();
    }

    public void beginTextOverlay() {
        spriteBatch.begin();
    }

    public void drawText(String text, float x, float y) {
        font.draw(spriteBatch, text, x, y);
    }

    public void endTextOverlay() {
        spriteBatch.end();
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
        if (font != null) {
            font.dispose();
            font = null;
        }
    }
}
