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

public class ResultScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont optionFont;
    private BitmapFont hintFont;
    private GlyphLayout layout;

    private final String[] options = { "RESTART", "MAIN MENU" };
    private int selectedIndex = 0;

    public ResultScene(SceneManager<CrossyLaneSceneKey> sceneManager) {
        this.navigator = new SceneNavigator(sceneManager);
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.RESULT;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (titleFont == null) titleFont = new BitmapFont();
        if (optionFont == null) optionFont = new BitmapFont();
        if (hintFont == null) hintFont = new BitmapFont();
        if (layout == null) layout = new GlyphLayout();

        titleFont.getData().setScale(1.9f);
        optionFont.getData().setScale(1.2f);
        hintFont.getData().setScale(1.0f);

        selectedIndex = 0;
    }

    @Override
    public void onExit() {}

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)
                || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = options.length - 1;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)
                || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedIndex++;
            if (selectedIndex >= options.length) selectedIndex = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedIndex) {
                case 0:
                    navigator.restartGame();
                    break;
                case 1:
                    navigator.goToMainMenu();
                    break;
            }
        }
    }

    @Override
    public void afterWorldUpdate(float delta) {}

    @Override
    public void render() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float boxWidth = 280f;
        float boxHeight = 55f;
        float startY = screenH / 2f + 5f;
        float gap = 20f;

        float panelX = screenW / 2f - 240f;
        float panelY = screenH / 2f - 180f;
        float panelW = 480f;
        float panelH = 360f;

        Gdx.gl.glClearColor(0.25f, 0.08f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Panel
        shapeRenderer.setColor(0.35f, 0.10f, 0.10f, 1f);
        shapeRenderer.rect(panelX, panelY, panelW, panelH);

        // Buttons
        for (int i = 0; i < options.length; i++) {
            float x = screenW / 2f - boxWidth / 2f;
            float y = startY - i * (boxHeight + gap);

            if (i == selectedIndex) {
                shapeRenderer.setColor(0.95f, 0.80f, 0.20f, 1f);
            } else {
                shapeRenderer.setColor(0.45f, 0.18f, 0.18f, 1f);
            }

            shapeRenderer.rect(x, y, boxWidth, boxHeight);
        }

        shapeRenderer.end();

        batch.begin();

        // Title
        titleFont.setColor(Color.WHITE);
        layout.setText(titleFont, "GAME OVER");
        titleFont.draw(batch,
                "GAME OVER",
                screenW / 2f - layout.width / 2f,
                screenH / 2f + 130f);

        // Options text
        for (int i = 0; i < options.length; i++) {
            float y = startY - i * (boxHeight + gap);

            optionFont.setColor(i == selectedIndex ? Color.BLACK : Color.WHITE);
            layout.setText(optionFont, options[i]);
            optionFont.draw(batch,
                    options[i],
                    screenW / 2f - layout.width / 2f,
                    y + boxHeight / 2f + layout.height / 2f);
        }

        // ⭐ CLEAR, BALANCED YELLOW TEXT (same style as Pause)
        hintFont.setColor(1f, 0.92f, 0.2f, 1f);

        String bottomText = "Press ENTER to Select";
        layout.setText(hintFont, bottomText);

        hintFont.draw(batch,
                bottomText,
                screenW / 2f - layout.width / 2f,
                panelY + 50f);   // moved higher = clearer

        batch.end();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (optionFont != null) optionFont.dispose();
        if (hintFont != null) hintFont.dispose();
    }

    @Override
    public boolean updatesWorld() {
        return false;
    }
}