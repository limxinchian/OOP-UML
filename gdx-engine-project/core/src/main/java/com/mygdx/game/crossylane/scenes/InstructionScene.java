package com.mygdx.game.crossylane.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class InstructionScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    private BitmapFont bottomFont;
    private GlyphLayout layout;

    public InstructionScene(SceneManager<CrossyLaneSceneKey> sceneManager) {
        this.navigator = new SceneNavigator(sceneManager);
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.INSTRUCTIONS;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (titleFont == null) titleFont = new BitmapFont();
        if (bodyFont == null) bodyFont = new BitmapFont();
        if (bottomFont == null) bottomFont = new BitmapFont();
        if (layout == null) layout = new GlyphLayout();

        titleFont.getData().setScale(1.8f);
        bodyFont.getData().setScale(1.05f);
        bottomFont.getData().setScale(1.0f);
    }

    @Override
    public void onExit() {
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.goToMainMenu();
        }
    }

    @Override
    public void afterWorldUpdate(float delta) {
    }

    @Override
    public void render() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.08f, 0.12f, 0.20f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float panelX = 60f;
        float panelY = 60f;
        float panelW = screenW - 120f;
        float panelH = screenH - 120f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.15f, 0.22f, 0.35f, 1f);
        shapeRenderer.rect(panelX, panelY, panelW, panelH);
        shapeRenderer.end();

        batch.begin();

        // TITLE (moved up slightly)
        titleFont.setColor(Color.WHITE);
        layout.setText(titleFont, "HOW TO PLAY");
        titleFont.draw(batch,
                "HOW TO PLAY",
                screenW / 2f - layout.width / 2f,
                panelY + panelH - 40f);

        // INSTRUCTIONS (moved up)
        bodyFont.setColor(Color.WHITE);

        float textX = panelX + 40f;
        float firstLineY = panelY + panelH - 105f;
        float lineGap = 48f;

        bodyFont.draw(batch,
                "1. Use arrow keys to move the chicken.",
                textX,
                firstLineY);

        bodyFont.draw(batch,
                "2. Do not get hit by cars or other hazards.",
                textX,
                firstLineY - lineGap);

        bodyFont.draw(batch,
                "3. Press ESC during gameplay to pause the game.",
                textX,
                firstLineY - lineGap * 2);

        bodyFont.draw(batch,
                "4. Reach as far as possible to get a better score.",
                textX,
                firstLineY - lineGap * 3);

        // BOTTOM TEXT (lowered slightly for spacing)
        bottomFont.setColor(Color.YELLOW);
        String bottomText = "Press ESC to return to Main Menu";
        layout.setText(bottomFont, bottomText);

        bottomFont.draw(batch,
                bottomText,
                screenW / 2f - layout.width / 2f,
                panelY + 25f);

        batch.end();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
        if (bottomFont != null) bottomFont.dispose();
    }

    @Override
    public boolean updatesWorld() {
        return false;
    }
}
