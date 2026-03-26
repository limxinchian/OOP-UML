package com.mygdx.game.crossylane.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class InstructionScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;
    private final IOManager ioManager;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;
    private OrthographicCamera uiCamera;

    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    private BitmapFont bottomFont;

    private final String[] instructions = {
            "1. Use ARROW KEYS or WASD to move the chicken.",
            "2. Avoid cars and other road hazards.",
            "3. Press ESC during gameplay to pause the game.",
            "4. Reach the goal zone at the top to advance a level.",
            "5. Cross the middle lane on GREEN for +50, RED for -50.",
            "6. Menus can be controlled with keyboard or mouse."
    };

    public InstructionScene(SceneManager<CrossyLaneSceneKey> sceneManager, IOManager ioManager) {
        this.navigator = new SceneNavigator(sceneManager);
        this.ioManager = ioManager;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.INSTRUCTIONS;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (layout == null) layout = new GlyphLayout();
        if (uiCamera == null) uiCamera = new OrthographicCamera();

        FontManager fonts = ioManager.getFontManager();
        titleFont  = fonts.getFont("default", 28);
        bodyFont   = fonts.getFont("default", 16);
        bottomFont = fonts.getFont("default", 14);
    }

    @Override
    public void onExit() { }

    private void updateUiCamera() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        uiCamera.setToOrtho(false, screenW, screenH);
        uiCamera.position.set(screenW / 2f, screenH / 2f, 0f);
        uiCamera.update();

        batch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.goToMainMenu();
        }
    }

    @Override
    public void afterWorldUpdate(float delta) { }

    @Override
    public void render() {
        updateUiCamera();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float contentWidth = clampFloat(screenW * 0.58f, 700f, 940f);
        float titleWidth = clampFloat(screenW * 0.26f, 360f, 500f);
        float titleHeight = clampFloat(screenH * 0.09f, 76f, 96f);

        float rowWidth = contentWidth;
        float rowHeight = clampFloat(screenH * 0.07f, 48f, 62f);
        float rowGap = clampFloat(screenH * 0.016f, 12f, 18f);

        float hintBoxHeight = clampFloat(screenH * 0.045f, 34f, 42f);

        float panelPaddingX = 42f;
        float panelTopPadding = 28f;
        float gapTitleToRows = 30f;
        float gapRowsToHint = 26f;
        float panelBottomPadding = 24f;

        float rowsHeight = instructions.length * rowHeight + (instructions.length - 1) * rowGap;

        float panelWidth = contentWidth + panelPaddingX * 2f;
        float panelHeight =
                panelTopPadding +
                titleHeight +
                gapTitleToRows +
                rowsHeight +
                gapRowsToHint +
                hintBoxHeight +
                panelBottomPadding;

        float panelX = screenW / 2f - panelWidth / 2f;
        float panelY = screenH / 2f - panelHeight / 2f;

        float titleX = screenW / 2f - titleWidth / 2f;
        float titleY = panelY + panelHeight - panelTopPadding - titleHeight;

        float rowX = screenW / 2f - rowWidth / 2f;
        float firstRowY = titleY - gapTitleToRows - rowHeight;

        float hintBoxWidth = contentWidth;
        float hintBoxX = rowX;
        float hintBoxY = panelY + panelBottomPadding;

        Gdx.gl.glClearColor(0.06f, 0.08f, 0.14f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background (blue tones)
        shapeRenderer.setColor(0.06f, 0.08f, 0.14f, 1f);
        shapeRenderer.rect(0, 0, screenW, screenH);

        shapeRenderer.setColor(0.04f, 0.06f, 0.10f, 1f);
        shapeRenderer.rect(0, 0, screenW * 0.18f, screenH);
        shapeRenderer.rect(screenW * 0.82f, 0, screenW * 0.18f, screenH);

        shapeRenderer.setColor(0.08f, 0.12f, 0.20f, 1f);
        shapeRenderer.rect(screenW * 0.20f, 0, screenW * 0.60f, screenH);

        // Panel shadow
        shapeRenderer.setColor(0f, 0f, 0f, 0.30f);
        shapeRenderer.rect(panelX + 10f, panelY - 10f, panelWidth, panelHeight);

        // Panel
        shapeRenderer.setColor(0.10f, 0.16f, 0.28f, 0.95f);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);

        // Panel top strip
        shapeRenderer.setColor(0.18f, 0.28f, 0.48f, 1f);
        shapeRenderer.rect(panelX, panelY + panelHeight - 16f, panelWidth, 16f);

        // Title
        shapeRenderer.setColor(0f, 0f, 0f, 0.28f);
        shapeRenderer.rect(titleX + 6f, titleY - 6f, titleWidth, titleHeight);

        shapeRenderer.setColor(0.18f, 0.28f, 0.48f, 1f);
        shapeRenderer.rect(titleX, titleY, titleWidth, titleHeight);

        shapeRenderer.setColor(0.30f, 0.45f, 0.75f, 1f);
        shapeRenderer.rect(titleX, titleY + titleHeight - 10f, titleWidth, 10f);

        // Rows
        for (int i = 0; i < instructions.length; i++) {
            float rowY = firstRowY - i * (rowHeight + rowGap);

            shapeRenderer.setColor(0f, 0f, 0f, 0.18f);
            shapeRenderer.rect(rowX + 4f, rowY - 4f, rowWidth, rowHeight);

            shapeRenderer.setColor(0.16f, 0.24f, 0.42f, 1f);
            shapeRenderer.rect(rowX, rowY, rowWidth, rowHeight);

            shapeRenderer.setColor(0.30f, 0.45f, 0.75f, 1f);
            shapeRenderer.rect(rowX, rowY + rowHeight - 6f, rowWidth, 6f);
        }

        // Hint box
        shapeRenderer.setColor(0f, 0f, 0f, 0.18f);
        shapeRenderer.rect(hintBoxX + 3f, hintBoxY - 3f, hintBoxWidth, hintBoxHeight);

        shapeRenderer.setColor(0.18f, 0.28f, 0.48f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY, hintBoxWidth, hintBoxHeight);

        shapeRenderer.setColor(0.30f, 0.45f, 0.75f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY + hintBoxHeight - 6f, hintBoxWidth, 6f);

        shapeRenderer.end();

        batch.begin();

        // Title text
        String title = "HOW TO PLAY";
        layout.setText(titleFont, title);

        titleFont.setColor(Color.BLACK);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f + 2f,
                titleY + titleHeight / 2f + layout.height / 2f + 2f);

        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f,
                titleY + titleHeight / 2f + layout.height / 2f);

        // Instructions
        bodyFont.setColor(0.94f, 0.96f, 1f, 1f);

        for (int i = 0; i < instructions.length; i++) {
            float rowY = firstRowY - i * (rowHeight + rowGap);

            bodyFont.draw(batch, instructions[i],
                    rowX + 22f,
                    rowY + rowHeight / 2f + 6f);
        }

        // Bottom text
        String bottomText = "ESC : Return to Main Menu";
        layout.setText(bottomFont, bottomText);

        bottomFont.setColor(Color.WHITE);
        bottomFont.draw(batch, bottomText,
                screenW / 2f - layout.width / 2f,
                hintBoxY + hintBoxHeight / 2f + layout.height / 2f - 2f);

        batch.end();
    }

    private float clampFloat(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }

    @Override
    public boolean updatesWorld() {
        return false;
    }
}