package com.mygdx.game.engine.render;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;

/**
 * Output side of IO:
 * draws all entities that have TransformComponent + RenderableComponent.
 */
public class OutputManager {

    private ShapeRenderer shapeRenderer;

    public void initialize() {
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
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
                shapeRenderer.rect(t.positionX, t.positionY, t.width, t.height);
            } else if (rc.getShape() == RenderShape.CIRCLE) {
                float radius = rc.getRadius();
                shapeRenderer.circle(t.positionX + radius, t.positionY + radius, radius);
            }
        }
    }

    public void endFrame() {
        shapeRenderer.end();
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
    }
}
