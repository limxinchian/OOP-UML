package com.mygdx.game.engine.render;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.engine.entity.Entity;
import com.mygdx.game.engine.entity.TransformComponent;

/**
 * Engine-level rendering manager.
 *
 * Refactor note (Part 2):
 * Previously imported CrossyLaneConfig for WORLD_WIDTH / WORLD_HEIGHT,
 * which coupled the engine to a specific game.  World dimensions are now
 * injected through the constructor so OutputManager remains fully
 * game-agnostic and reusable across different projects.
 *
 * Addresses: Engine/Game separation, Dependency Inversion Principle.
 */
public class OutputManager {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    private OrthographicCamera camera;
    private Viewport viewport;

    private final float worldWidth;
    private final float worldHeight;

    /**
     * @param worldWidth  logical world width in game units
     * @param worldHeight logical world height in game units
     */
    public OutputManager(float worldWidth, float worldHeight) {
        if (worldWidth <= 0f) throw new IllegalArgumentException("worldWidth must be > 0");
        if (worldHeight <= 0f) throw new IllegalArgumentException("worldHeight must be > 0");

        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void initialize() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (spriteBatch == null) spriteBatch = new SpriteBatch();
        if (font == null) font = new BitmapFont();
        if (glyphLayout == null) glyphLayout = new GlyphLayout();

        camera = new OrthographicCamera();
        viewport = new StretchViewport(worldWidth, worldHeight, camera);
        viewport.apply(true);
        camera.update();
    }

    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
            camera.update();
        }
    }

    public void beginFrame(float r, float g, float b, float a) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (viewport != null) {
            viewport.apply();
        }
        if (camera != null) {
            camera.update();
            shapeRenderer.setProjectionMatrix(camera.combined);
            spriteBatch.setProjectionMatrix(camera.combined);
        }
    }

    public Matrix4 getWorldProjectionMatrix() {
        return camera.combined;
    }

    public void applyWorldProjection(ShapeRenderer renderer) {
        if (renderer != null && camera != null) {
            renderer.setProjectionMatrix(camera.combined);
        }
    }

    public void applyWorldProjection(SpriteBatch batch) {
        if (batch != null && camera != null) {
            batch.setProjectionMatrix(camera.combined);
        }
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public void renderEntities(List<Entity> entities) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity e : entities) {
            if (!e.isActive()) continue;

            TransformComponent t = e.getComponent(TransformComponent.class);
            RenderableComponent rc = e.getComponent(RenderableComponent.class);
            TextureComponent tex = e.getComponent(TextureComponent.class);

            if (tex != null && tex.isEnabled()) continue;
            if (t == null || rc == null || !rc.isEnabled()) continue;

            shapeRenderer.setColor(rc.r(), rc.g(), rc.b(), rc.a());

            if (rc.getShape() == RenderShape.RECTANGLE) {
                shapeRenderer.rect(t.getPositionX(), t.getPositionY(), t.getWidth(), t.getHeight());
            } else if (rc.getShape() == RenderShape.CIRCLE) {
                float radius = rc.getRadius();
                shapeRenderer.circle(t.getPositionX() + radius, t.getPositionY() + radius, radius);
            }
        }
        shapeRenderer.end();

        spriteBatch.begin();
        for (Entity e : entities) {
            if (!e.isActive()) continue;

            TransformComponent t = e.getComponent(TransformComponent.class);
            TextureComponent tex = e.getComponent(TextureComponent.class);

            if (t == null || tex == null || !tex.isEnabled()) continue;

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
    }

    public void endFrame() { }

    public void beginTextOverlay() {
        spriteBatch.begin();
    }

    public void drawText(String text, float x, float y) {
        font.draw(spriteBatch, text, x, y);
    }

    public void drawCenteredText(String text, float centerX, float y) {
        glyphLayout.setText(font, text);
        font.draw(spriteBatch, text, centerX - (glyphLayout.width / 2f), y);
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
