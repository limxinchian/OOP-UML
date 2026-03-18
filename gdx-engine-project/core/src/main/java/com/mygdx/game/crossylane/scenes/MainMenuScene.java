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

public class MainMenuScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont optionFont;
    private GlyphLayout layout;

    private final String[] options = { "START GAME", "INSTRUCTIONS", "EXIT" };
    private int selectedIndex = 0;

    public MainMenuScene(SceneManager<CrossyLaneSceneKey> sceneManager) {
        this.navigator = new SceneNavigator(sceneManager);
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.MAIN_MENU;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (titleFont == null) titleFont = new BitmapFont();
        if (optionFont == null) optionFont = new BitmapFont();
        if (layout == null) layout = new GlyphLayout();

        titleFont.getData().setScale(2.2f);
        optionFont.getData().setScale(1.3f);

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedIndex) {
                case 0:
                    navigator.startGame();
                    break;
                case 1:
                    navigator.goToInstructions();
                    break;
                case 2:
                    Gdx.app.exit();
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

        float boxWidth = 320f;
        float boxHeight = 55f;
        float startY = screenH / 2f + 20f;
        float gap = 20f;

        Gdx.gl.glClearColor(0.12f, 0.18f, 0.12f, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Title panel
        shapeRenderer.setColor(0.18f, 0.30f, 0.18f, 1f);
        shapeRenderer.rect(screenW / 2f - 220f, screenH - 140f, 440f, 80f);

        // Option boxes
        for (int i = 0; i < options.length; i++) {
            float x = screenW / 2f - boxWidth / 2f;
            float y = startY - i * (boxHeight + gap);

            if (i == selectedIndex) {
                shapeRenderer.setColor(0.95f, 0.85f, 0.25f, 1f);
            } else {
                shapeRenderer.setColor(0.25f, 0.45f, 0.25f, 1f);
            }

            shapeRenderer.rect(x, y, boxWidth, boxHeight);
        }

        shapeRenderer.end();

        batch.begin();

        titleFont.setColor(Color.WHITE);
        layout.setText(titleFont, "CROSSY LANE");
        titleFont.draw(batch, "CROSSY LANE",
                screenW / 2f - layout.width / 2f,
                screenH - 85f);

        for (int i = 0; i < options.length; i++) {
            float y = startY - i * (boxHeight + gap);

            optionFont.setColor(i == selectedIndex ? Color.BLACK : Color.WHITE);
            layout.setText(optionFont, options[i]);
            optionFont.draw(batch, options[i],
                    screenW / 2f - layout.width / 2f,
                    y + boxHeight / 2f + layout.height / 2f);
        }

        optionFont.setColor(Color.LIGHT_GRAY);
        layout.setText(optionFont, "Use UP / DOWN to move, ENTER to select");
        optionFont.draw(batch,
                "Use UP / DOWN to move, ENTER to select",
                screenW / 2f - layout.width / 2f,
                70f);

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
