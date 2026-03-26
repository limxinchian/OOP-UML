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
import com.mygdx.game.crossylane.audio.CrossyLaneAudioController;
import com.mygdx.game.engine.io.MouseInput;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class Settings implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;
    private final SceneManager<CrossyLaneSceneKey> sceneManager;
    private final IOManager ioManager;
    private final CrossyLaneAudioController audioController;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;
    private OrthographicCamera uiCamera;

    private BitmapFont titleFont;
    private BitmapFont labelFont;
    private BitmapFont valueFont;
    private BitmapFont hintFont;

    private float musicVolume;
    private float sfxVolume;

    private int selectedRow = 0;
    private static final int ROW_COUNT = 3; // music, sfx, back

    public Settings(SceneManager<CrossyLaneSceneKey> sceneManager,
                    IOManager ioManager,
                    CrossyLaneAudioController audioController) {
        this.navigator = new SceneNavigator(sceneManager);
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.audioController = audioController;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.SETTINGS;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (layout == null) layout = new GlyphLayout();
        if (uiCamera == null) uiCamera = new OrthographicCamera();

        FontManager fonts = ioManager.getFontManager();
        titleFont = fonts.getFont("default", 30);
        labelFont = fonts.getFont("default", 18);
        valueFont = fonts.getFont("default", 18);
        hintFont  = fonts.getFont("default", 13);

        musicVolume = audioController.getMusicVolume();
        sfxVolume = audioController.getSfxVolume();
        selectedRow = 0;
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

    private void goBack() {
        CrossyLaneSceneKey previous = sceneManager.getPreviousSceneKey();

        if (previous == CrossyLaneSceneKey.PAUSE) {
            sceneManager.changeScene(CrossyLaneSceneKey.PAUSE);
        } else {
            navigator.goToMainMenu();
        }
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedRow = (selectedRow - 1 + ROW_COUNT) % ROW_COUNT;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedRow = (selectedRow + 1) % ROW_COUNT;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            goBack();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            adjustSelected(-0.05f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            adjustSelected(0.05f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && selectedRow == 2) {
            goBack();
            return;
        }

        MouseInput mouse = ioManager.getMouse();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float panelWidth = clampFloat(screenW * 0.42f, 560f, 760f);
        float rowHeight = clampFloat(screenH * 0.08f, 56f, 74f);
        float rowGap = clampFloat(screenH * 0.02f, 16f, 22f);

        float titleHeight = clampFloat(screenH * 0.09f, 74f, 92f);
        float hintBoxHeight = clampFloat(screenH * 0.05f, 42f, 52f);

        float panelTopPadding = 26f;
        float gapTitleToRows = 28f;
        float gapRowsToHint = 22f;
        float panelBottomPadding = 20f;

        float rowsHeight = ROW_COUNT * rowHeight + (ROW_COUNT - 1) * rowGap;

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

        float firstRowY = panelY + panelHeight - panelTopPadding - titleHeight - gapTitleToRows - rowHeight;

        float sliderWidth = panelWidth * 0.44f;
        float sliderHeight = 14f;
        float sliderX = panelX + panelWidth - sliderWidth - 30f;

        for (int row = 0; row < ROW_COUNT; row++) {
            float rowY = firstRowY - row * (rowHeight + rowGap);

            if (mouse.isOver(panelX, rowY, panelWidth, rowHeight)) {
                selectedRow = row;
            }

            if (row < 2) {
                float sliderY = rowY + rowHeight / 2f - sliderHeight / 2f;

                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)
                        && mouse.isOver(sliderX, sliderY - 10f, sliderWidth, sliderHeight + 20f)) {
                    float percent = (Gdx.input.getX() - sliderX) / sliderWidth;
                    percent = clampFloat(percent, 0f, 1f);

                    if (row == 0) {
                        musicVolume = snapVolume(percent);
                        audioController.setMusicVolume(musicVolume);
                    } else {
                        sfxVolume = snapVolume(percent);
                        audioController.setSfxVolume(sfxVolume);
                    }
                }
            } else {
                if (mouse.isLeftJustPressed() && mouse.isOver(panelX, rowY, panelWidth, rowHeight)) {
                    goBack();
                    return;
                }
            }
        }
    }

    private void adjustSelected(float amount) {
        if (selectedRow == 0) {
            musicVolume = snapVolume(clampFloat(musicVolume + amount, 0f, 1f));
            audioController.setMusicVolume(musicVolume);
        } else if (selectedRow == 1) {
            sfxVolume = snapVolume(clampFloat(sfxVolume + amount, 0f, 1f));
            audioController.setSfxVolume(sfxVolume);
        }
    }

    private float snapVolume(float v) {
        if (v <= 0.03f) return 0f;
        if (v >= 0.97f) return 1f;
        return v;
    }

    @Override
    public void afterWorldUpdate(float delta) { }

    @Override
    public boolean updatesWorld() {
        return false;
    }

    @Override
    public void render() {
        updateUiCamera();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float panelWidth = clampFloat(screenW * 0.42f, 560f, 760f);
        float rowHeight = clampFloat(screenH * 0.08f, 56f, 74f);
        float rowGap = clampFloat(screenH * 0.02f, 16f, 22f);

        float titleWidth = clampFloat(screenW * 0.22f, 280f, 420f);
        float titleHeight = clampFloat(screenH * 0.09f, 74f, 92f);
        float hintBoxHeight = clampFloat(screenH * 0.05f, 42f, 52f);

        float panelTopPadding = 26f;
        float gapTitleToRows = 28f;
        float gapRowsToHint = 22f;
        float panelBottomPadding = 20f;

        float rowsHeight = ROW_COUNT * rowHeight + (ROW_COUNT - 1) * rowGap;

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

        float firstRowY = titleY - gapTitleToRows - rowHeight;

        float sliderWidth = panelWidth * 0.44f;
        float sliderHeight = 14f;
        float sliderX = panelX + panelWidth - sliderWidth - 30f;
        float knobRadius = 10f;

        float hintBoxX = panelX;
        float hintBoxY = panelY + panelBottomPadding;
        float hintBoxWidth = panelWidth;

        Gdx.gl.glClearColor(0.08f, 0.12f, 0.09f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.08f, 0.12f, 0.09f, 1f);
        shapeRenderer.rect(0, 0, screenW, screenH);

        shapeRenderer.setColor(0.06f, 0.10f, 0.08f, 1f);
        shapeRenderer.rect(0, 0, screenW * 0.18f, screenH);
        shapeRenderer.rect(screenW * 0.82f, 0, screenW * 0.18f, screenH);

        shapeRenderer.setColor(0.10f, 0.17f, 0.11f, 1f);
        shapeRenderer.rect(screenW * 0.20f, 0, screenW * 0.60f, screenH);

        shapeRenderer.setColor(0f, 0f, 0f, 0.30f);
        shapeRenderer.rect(panelX + 10f, panelY - 10f, panelWidth, panelHeight);

        shapeRenderer.setColor(0.10f, 0.18f, 0.10f, 0.92f);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);

        shapeRenderer.setColor(0.18f, 0.32f, 0.18f, 1f);
        shapeRenderer.rect(panelX, panelY + panelHeight - 16f, panelWidth, 16f);

        shapeRenderer.setColor(0f, 0f, 0f, 0.28f);
        shapeRenderer.rect(titleX + 6f, titleY - 6f, titleWidth, titleHeight);

        shapeRenderer.setColor(0.18f, 0.32f, 0.18f, 1f);
        shapeRenderer.rect(titleX, titleY, titleWidth, titleHeight);

        shapeRenderer.setColor(0.28f, 0.45f, 0.28f, 1f);
        shapeRenderer.rect(titleX, titleY + titleHeight - 10f, titleWidth, 10f);

        for (int row = 0; row < ROW_COUNT; row++) {
            float rowY = firstRowY - row * (rowHeight + rowGap);
            boolean selected = row == selectedRow;

            shapeRenderer.setColor(0f, 0f, 0f, 0.22f);
            shapeRenderer.rect(panelX + 4f, rowY - 4f, panelWidth, rowHeight);

            if (row == 2) {
                if (selected) {
                    shapeRenderer.setColor(0.90f, 0.80f, 0.20f, 1f);
                } else {
                    shapeRenderer.setColor(0.26f, 0.48f, 0.26f, 1f);
                }
                shapeRenderer.rect(panelX, rowY, panelWidth, rowHeight);
            } else {
                if (selected) {
                    shapeRenderer.setColor(0.24f, 0.30f, 0.24f, 1f);
                } else {
                    shapeRenderer.setColor(0.18f, 0.26f, 0.18f, 1f);
                }
                shapeRenderer.rect(panelX, rowY, panelWidth, rowHeight);

                float sliderY = rowY + rowHeight / 2f - sliderHeight / 2f;
                float fill = (row == 0) ? musicVolume : sfxVolume;

                shapeRenderer.setColor(0.18f, 0.24f, 0.38f, 1f);
                shapeRenderer.rect(sliderX, sliderY, sliderWidth, sliderHeight);

                shapeRenderer.setColor(0.90f, 0.80f, 0.20f, 1f);
                shapeRenderer.rect(sliderX, sliderY, sliderWidth * fill, sliderHeight);

                float knobX = sliderX + sliderWidth * fill;
                shapeRenderer.setColor(0.98f, 0.92f, 0.45f, 1f);
                shapeRenderer.circle(knobX, sliderY + sliderHeight / 2f, knobRadius);
            }
        }

        shapeRenderer.setColor(0f, 0f, 0f, 0.18f);
        shapeRenderer.rect(hintBoxX + 3f, hintBoxY - 3f, hintBoxWidth, hintBoxHeight);

        shapeRenderer.setColor(0.20f, 0.38f, 0.20f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY, hintBoxWidth, hintBoxHeight);

        shapeRenderer.setColor(0.30f, 0.50f, 0.30f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY + hintBoxHeight - 6f, hintBoxWidth, 6f);

        shapeRenderer.end();

        batch.begin();

        String title = "SETTINGS";
        layout.setText(titleFont, title);

        titleFont.setColor(0f, 0f, 0f, 0.45f);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f + 2f,
                titleY + titleHeight / 2f + layout.height / 2f + 2f);

        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f,
                titleY + titleHeight / 2f + layout.height / 2f);

        drawSliderRowText("MUSIC VOLUME", musicVolume, panelX, firstRowY, rowHeight, sliderX);

        drawSliderRowText("SFX VOLUME", sfxVolume, panelX,
                firstRowY - (rowHeight + rowGap), rowHeight, sliderX);

        float backY = firstRowY - 2 * (rowHeight + rowGap);
        String back = "BACK";
        layout.setText(labelFont, back);
        labelFont.setColor(selectedRow == 2 ? Color.BLACK : Color.WHITE);
        labelFont.draw(batch, back,
                screenW / 2f - layout.width / 2f,
                backY + rowHeight / 2f + layout.height / 2f - 2f);

        String hint = "UP/DOWN: select   |   LEFT/RIGHT or drag: adjust   |   ESC: back";
        layout.setText(hintFont, hint);
        hintFont.setColor(0.88f, 0.92f, 1f, 1f);
        hintFont.draw(batch, hint,
                screenW / 2f - layout.width / 2f,
                hintBoxY + hintBoxHeight / 2f + layout.height / 2f - 2f);

        batch.end();
    }

    private void drawSliderRowText(String label, float value, float panelX,
                                   float rowY, float rowHeight, float sliderX) {
        float textY = rowY + rowHeight / 2f + 6f;

        labelFont.setColor(Color.WHITE);
        labelFont.draw(batch, label, panelX + 22f, textY);

        String valueText = (int) (value * 100) + "%";
        layout.setText(valueFont, valueText);
        valueFont.setColor(0.92f, 0.96f, 1f, 1f);
        valueFont.draw(batch, valueText, sliderX - layout.width - 16f, textY);
    }

    private float clampFloat(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }
}