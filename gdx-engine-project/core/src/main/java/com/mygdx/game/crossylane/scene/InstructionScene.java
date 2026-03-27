package com.mygdx.game.crossylane.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.ui.MenuUiTheme;
import com.mygdx.game.engine.io.IOManager;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;
import com.mygdx.game.engine.math.MathUtil;

/**
 * Instructions / How-To-Play screen.
 *
 * Phase 3 changes:
 * - Fonts loaded through engine's FontManager.
 * - Updated instruction text to reflect WASD + arrow key + mouse support.
 */
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
        titleFont  = fonts.getFont("default", 26);
        bodyFont   = fonts.getFont("default", 16);
        bottomFont = fonts.getFont("default", 15);
    }

    @Override
    public void onExit() { }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.goToMainMenu();
        }
    }

    @Override
    public void afterWorldUpdate(float delta) { }

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
    public void render() {
        updateUiCamera();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.06f, 0.08f, 0.14f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float panelX = MathUtil.clamp(screenW * 0.10f, 40f, 120f);
        float panelY = MathUtil.clamp(screenH * 0.10f, 40f, 100f);
        float panelW = screenW - panelX * 2f;
        float panelH = screenH - panelY * 2f;

        float titleBandHeight = MathUtil.clamp(screenH * 0.11f, 72f, 102f);
        float hintBandHeight = MathUtil.clamp(screenH * 0.09f, 54f, 74f);
        float textX = panelX + MathUtil.clamp(screenW * 0.04f, 28f, 60f);
        float firstLineY = panelY + panelH - titleBandHeight - 30f;
        float lineGap = MathUtil.clamp(screenH * 0.055f, 28f, 46f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        MenuUiTheme.drawBackdrop(shapeRenderer, screenW, screenH);
        MenuUiTheme.drawPanel(shapeRenderer, panelX, panelY, panelW, panelH);
        MenuUiTheme.drawCard(shapeRenderer, panelX, panelY + panelH - titleBandHeight, panelW, titleBandHeight);
        MenuUiTheme.drawCard(shapeRenderer, panelX, panelY, panelW, hintBandHeight);
        shapeRenderer.end();

        batch.begin();

        // Title
        titleFont.setColor(Color.WHITE);
        layout.setText(titleFont, "HOW TO PLAY");
        titleFont.draw(batch, "HOW TO PLAY",
            screenW / 2f - layout.width / 2f,
            panelY + panelH - titleBandHeight / 2f + layout.height / 2f - 2f);

        // Instructions
        bodyFont.setColor(Color.WHITE);

        bodyFont.draw(batch,
                "1. Use arrow keys or WASD to move the chicken.",
                textX, firstLineY);
        bodyFont.draw(batch,
                "2. Do not get hit by cars or other hazards.",
                textX, firstLineY - lineGap);
        bodyFont.draw(batch,
                "3. Press ESC during gameplay to pause the game.",
                textX, firstLineY - lineGap * 2);
        bodyFont.draw(batch,
                "4. Reach the goal zone at the top to advance a level.",
                textX, firstLineY - lineGap * 3);
        bodyFont.draw(batch,
                "5. Enter the road on green for bonus points, red for a penalty.",
                textX, firstLineY - lineGap * 4);
        bodyFont.draw(batch,
                "6. Navigate menus with keyboard or mouse click.",
                textX, firstLineY - lineGap * 5);

        // Bottom hint
        bottomFont.setColor(0.92f, 0.96f, 1f, 1f);
        String bottomText = "ESC: Back to Main Menu";
        layout.setText(bottomFont, bottomText);
        bottomFont.draw(batch, bottomText,
            screenW / 2f - layout.width / 2f,
            panelY + hintBandHeight / 2f + layout.height / 2f - 2f);

        batch.end();
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
