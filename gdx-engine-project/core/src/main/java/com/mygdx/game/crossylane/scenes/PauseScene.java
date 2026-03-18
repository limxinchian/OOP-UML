package com.mygdx.game.crossylane.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class PauseScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont optionFont;
    private GlyphLayout layout;
    private BitmapFont hintFont;

    private final String[] options = { "RESUME", "RESTART", "MAIN MENU" };
    private int selectedIndex = 0;

    public PauseScene(SceneManager<CrossyLaneSceneKey> sceneManager) {
        this.navigator = new SceneNavigator(sceneManager);
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.PAUSE;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (titleFont == null) titleFont = new BitmapFont();
        if (optionFont == null) optionFont = new BitmapFont();
        if (layout == null) layout = new GlyphLayout();
        if (hintFont == null) hintFont = new BitmapFont();
        hintFont.getData().setScale(0.95f);

        titleFont.getData().setScale(1.9f);
        optionFont.getData().setScale(1.2f);

        selectedIndex = 0;
    }

    @Override
    public void onExit() {
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = options.length - 1;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex++;
            if (selectedIndex >= options.length) selectedIndex = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.resumeGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedIndex) {
                case 0:
                    navigator.resumeGame();
                    break;
                case 1:
                    navigator.restartGame();
                    break;
                case 2:
                    navigator.goToMainMenu();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void afterWorldUpdate(float delta) {
    }

    @Override
    public void render() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float boxWidth = 300f;
        float boxHeight = 50f;
        float startY = screenH / 2f + 22f;
        float gap = 18f;

        Gdx.gl.glClearColor(0.06f, 0.06f, 0.06f, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Bigger and taller background panel
        float panelX = screenW / 2f - 240f;
        float panelY = screenH / 2f - 190f;
        float panelW = 480f;
        float panelH = 350f;

        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(panelX, panelY, panelW, panelH);

        for (int i = 0; i < options.length; i++) {
            float x = screenW / 2f - boxWidth / 2f;
            float y = startY - i * (boxHeight + gap);

            if (i == selectedIndex) {
                shapeRenderer.setColor(0.90f, 0.75f, 0.20f, 1f);
            } else {
                shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 1f);
            }

            shapeRenderer.rect(x, y, boxWidth, boxHeight);
        }

        shapeRenderer.end();

        batch.begin();

        // Title
        titleFont.setColor(Color.WHITE);
        layout.setText(titleFont, "PAUSED");
        titleFont.draw(batch, "PAUSED",
                screenW / 2f - layout.width / 2f,
                screenH / 2f + 120f);

        // Buttons
        for (int i = 0; i < options.length; i++) {
            float y = startY - i * (boxHeight + gap);

            optionFont.setColor(i == selectedIndex ? Color.BLACK : Color.WHITE);
            layout.setText(optionFont, options[i]);
            optionFont.draw(batch, options[i],
                    screenW / 2f - layout.width / 2f,
                    y + boxHeight / 2f + layout.height / 2f);
        }

        // Bottom instructions
        hintFont.setColor(1f, 0.92f, 0.2f, 1f);
        hintFont.getData().setScale(1.0f);

        String bottomText = "ESC: Resume   |   ENTER: Select";
        layout.setText(hintFont, bottomText);

        hintFont.draw(batch,
        bottomText,
        screenW / 2f - layout.width / 2f,
        panelY + 38f);

        batch.end();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (optionFont != null) optionFont.dispose();
    }

    @Override
    public boolean updatesWorld() {
        return false;
    }
}