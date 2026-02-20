package com.mygdx.game.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class MainMenuScene implements IScene<DemoSceneKey> {

    private final SceneManager<DemoSceneKey> sceneManager;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;

    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private GlyphLayout layout;

    private Rectangle startBtn;
    private Rectangle instructionsBtn;
    private Rectangle exitBtn;

    private boolean initialized = false;
    private int lastW = -1;
    private int lastH = -1;

    public MainMenuScene(SceneManager<DemoSceneKey> sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public DemoSceneKey getKey() {
        return DemoSceneKey.MAIN_MENU;
    }

    @Override
    public void onEnter() {
        if (!initialized) {
            shapeRenderer = new ShapeRenderer();
            batch = new SpriteBatch();
            layout = new GlyphLayout();

            // Cleaner than NEAREST for scaled default fonts
            titleFont = new BitmapFont();
            titleFont.getData().setScale(2.0f);
            titleFont.setUseIntegerPositions(true);
            titleFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

            buttonFont = new BitmapFont();
            buttonFont.getData().setScale(1.2f);
            buttonFont.setUseIntegerPositions(true);
            buttonFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

            initialized = true;
        }

        ensureLayout();
    }

    @Override
    public void onExit() { }

    @Override
    public void update(float delta) {
        ensureLayout();

        // Keyboard shortcuts
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            sceneManager.resetTo(DemoSceneKey.GAME);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            sceneManager.changeScene(DemoSceneKey.INSTRUCTIONS);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            return;
        }

        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (startBtn.contains(x, y)) {
                sceneManager.resetTo(DemoSceneKey.GAME);
            } else if (instructionsBtn.contains(x, y)) {
                sceneManager.changeScene(DemoSceneKey.INSTRUCTIONS);
            } else if (exitBtn.contains(x, y)) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void render() {
        ensureLayout();

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        float mx = Gdx.input.getX();
        float my = h - Gdx.input.getY();

        boolean hoverStart = startBtn.contains(mx, my);
        boolean hoverInstr = instructionsBtn.contains(mx, my);
        boolean hoverExit = exitBtn.contains(mx, my);

        // Background
        Gdx.gl.glClearColor(0.05f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Buttons (filled)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawButtonFill(startBtn, hoverStart);
        drawButtonFill(instructionsBtn, hoverInstr);
        drawButtonFill(exitBtn, hoverExit);
        shapeRenderer.end();

        // Buttons (outline)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawButtonOutline(startBtn, hoverStart);
        drawButtonOutline(instructionsBtn, hoverInstr);
        drawButtonOutline(exitBtn, hoverExit);
        shapeRenderer.end();

        batch.begin();

        // --- Title: compute Y so it NEVER overlaps buttons ---
        layout.setText(titleFont, "Flappy Demo");

        float minTitleY = startBtn.y + startBtn.height + layout.height + 26f; // always above button group
        float desiredTitleY = h - 70f;                                        // near top
        float titleY = Math.max(minTitleY, desiredTitleY);

        float titleX = (w - layout.width) / 2f;
        // round for stable rendering
        titleX = Math.round(titleX);
        titleY = Math.round(titleY);

        // Small shadow for readability (still crisp because integer positions)
        titleFont.setColor(0f, 0f, 0f, 0.35f);
        titleFont.draw(batch, layout, titleX + 2, titleY - 2);
        titleFont.setColor(1f, 1f, 1f, 1f);
        titleFont.draw(batch, layout, titleX, titleY);

        // Button labels (centered)
        drawCenteredLabel("Start (Enter)", startBtn);
        drawCenteredLabel("Instructions (I)", instructionsBtn);
        drawCenteredLabel("Exit (Esc)", exitBtn);

        batch.end();
    }

    private void ensureLayout() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        if (w == lastW && h == lastH && startBtn != null) return;

        lastW = w;
        lastH = h;

        float btnW = Math.min(360f, w * 0.60f);
        float btnH = 64f;
        float gap = 18f;

        float groupHeight = 3f * btnH + 2f * gap;
        float groupTopY = (h * 0.55f) + groupHeight / 2f; // slightly above center

        float centerX = w / 2f;

        float y0 = groupTopY - btnH; // top button Y
        startBtn = new Rectangle(centerX - btnW / 2f, y0, btnW, btnH);
        instructionsBtn = new Rectangle(centerX - btnW / 2f, y0 - (btnH + gap), btnW, btnH);
        exitBtn = new Rectangle(centerX - btnW / 2f, y0 - 2f * (btnH + gap), btnW, btnH);
    }

    private void drawButtonFill(Rectangle r, boolean hovered) {
        if (hovered) shapeRenderer.setColor(0.92f, 0.92f, 0.92f, 1f);
        else shapeRenderer.setColor(0.85f, 0.85f, 0.85f, 1f);

        shapeRenderer.rect(Math.round(r.x), Math.round(r.y), Math.round(r.width), Math.round(r.height));
    }

    private void drawButtonOutline(Rectangle r, boolean hovered) {
        if (hovered) shapeRenderer.setColor(0.25f, 0.75f, 1f, 1f);
        else shapeRenderer.setColor(0.10f, 0.10f, 0.10f, 1f);

        shapeRenderer.rect(Math.round(r.x), Math.round(r.y), Math.round(r.width), Math.round(r.height));
    }

    private void drawCenteredLabel(String text, Rectangle btn) {
        layout.setText(buttonFont, text);
        float x = Math.round(btn.x + (btn.width - layout.width) / 2f);
        float y = Math.round(btn.y + (btn.height + layout.height) / 2f);

        // dark label on light button
        buttonFont.setColor(0.10f, 0.10f, 0.10f, 1f);
        buttonFont.draw(batch, layout, x, y);
        buttonFont.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
    }
}
