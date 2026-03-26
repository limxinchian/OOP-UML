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

public class PauseScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;
    private final IOManager ioManager;
    private final CrossyLaneAudioController audioController;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;
    private OrthographicCamera uiCamera;

    private BitmapFont titleFont;
    private BitmapFont optionFont;
    private BitmapFont hintFont;

    private final String[] options = { "RESUME", "RESTART", "SETTINGS", "MAIN MENU" };
    private int selectedIndex = 0;

    public PauseScene(SceneManager<CrossyLaneSceneKey> sceneManager,
                      IOManager ioManager,
                      CrossyLaneAudioController audioController) {
        this.navigator = new SceneNavigator(sceneManager);
        this.ioManager = ioManager;
        this.audioController = audioController;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.PAUSE;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (layout == null) layout = new GlyphLayout();
        if (uiCamera == null) uiCamera = new OrthographicCamera();

        FontManager fonts = ioManager.getFontManager();
        titleFont  = fonts.getFont("default", 28);
        optionFont = fonts.getFont("default", 18);
        hintFont   = fonts.getFont("default", 14);

        selectedIndex = 0;
        audioController.pauseMusic();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = options.length - 1;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex++;
            if (selectedIndex >= options.length) selectedIndex = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            audioController.resumeMusic();
            navigator.resumeGame();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            activateOption(selectedIndex);
            return;
        }

        MouseInput mouse = ioManager.getMouse();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float menuWidth = clampFloat(screenW * 0.24f, 300f, 420f);
        float buttonHeight = clampFloat(screenH * 0.072f, 50f, 68f);
        float buttonGap = clampFloat(screenH * 0.020f, 14f, 22f);

        float titleWidth = clampFloat(screenW * 0.24f, 280f, 420f);
        float titleHeight = clampFloat(screenH * 0.09f, 72f, 92f);

        float hintBoxHeight = clampFloat(screenH * 0.045f, 34f, 42f);

        float panelPaddingX = 40f;
        float panelTopPadding = 28f;
        float gapTitleToButtons = 30f;
        float gapButtonsToHint = 24f;
        float panelBottomPadding = 24f;

        float menuHeight = options.length * buttonHeight + (options.length - 1) * buttonGap;

        float panelWidth = menuWidth + panelPaddingX * 2f;
        float panelHeight =
                panelTopPadding +
                titleHeight +
                gapTitleToButtons +
                menuHeight +
                gapButtonsToHint +
                hintBoxHeight +
                panelBottomPadding;

        float panelY = screenH / 2f - panelHeight / 2f;
        float titleY = panelY + panelHeight - panelTopPadding - titleHeight;
        float buttonX = screenW / 2f - menuWidth / 2f;
        float menuStartY = titleY - gapTitleToButtons - buttonHeight;

        for (int i = 0; i < options.length; i++) {
            float buttonY = menuStartY - i * (buttonHeight + buttonGap);

            if (mouse.isOver(buttonX, buttonY, menuWidth, buttonHeight)) {
                selectedIndex = i;
                if (mouse.isLeftJustPressed()) {
                    activateOption(i);
                    return;
                }
            }
        }
    }

    private void activateOption(int index) {
        switch (index) {
            case 0:
                audioController.resumeMusic();
                navigator.resumeGame();
                break;
            case 1:
                navigator.restartGame();
                break;
            case 2:
                navigator.goToSettings();
                break;
            case 3:
                navigator.goToMainMenu();
                break;
            default:
                break;
        }
    }

    @Override
    public void afterWorldUpdate(float delta) { }

    @Override
    public void render() {
        updateUiCamera();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float menuWidth = clampFloat(screenW * 0.24f, 300f, 420f);
        float buttonHeight = clampFloat(screenH * 0.072f, 50f, 68f);
        float buttonGap = clampFloat(screenH * 0.020f, 14f, 22f);

        float titleWidth = clampFloat(screenW * 0.24f, 280f, 420f);
        float titleHeight = clampFloat(screenH * 0.09f, 72f, 92f);

        float hintBoxHeight = clampFloat(screenH * 0.045f, 34f, 42f);

        float panelPaddingX = 40f;
        float panelTopPadding = 28f;
        float gapTitleToButtons = 30f;
        float gapButtonsToHint = 24f;
        float panelBottomPadding = 24f;

        float menuHeight = options.length * buttonHeight + (options.length - 1) * buttonGap;

        float panelWidth = menuWidth + panelPaddingX * 2f;
        float panelHeight =
                panelTopPadding +
                titleHeight +
                gapTitleToButtons +
                menuHeight +
                gapButtonsToHint +
                hintBoxHeight +
                panelBottomPadding;

        float panelX = screenW / 2f - panelWidth / 2f;
        float panelY = screenH / 2f - panelHeight / 2f;

        float titleX = screenW / 2f - titleWidth / 2f;
        float titleY = panelY + panelHeight - panelTopPadding - titleHeight;

        float buttonX = screenW / 2f - menuWidth / 2f;
        float menuStartY = titleY - gapTitleToButtons - buttonHeight;

        float hintBoxWidth = menuWidth;
        float hintBoxX = buttonX;
        float hintBoxY = panelY + panelBottomPadding;

        Gdx.gl.glClearColor(0.03f, 0.03f, 0.03f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background
        shapeRenderer.setColor(0.03f, 0.03f, 0.03f, 1f);
        shapeRenderer.rect(0, 0, screenW, screenH);

        shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 1f);
        shapeRenderer.rect(0, 0, screenW * 0.18f, screenH);
        shapeRenderer.rect(screenW * 0.82f, 0, screenW * 0.18f, screenH);

        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(screenW * 0.20f, 0, screenW * 0.60f, screenH);

        // Main panel shadow
        shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        shapeRenderer.rect(panelX + 10f, panelY - 10f, panelWidth, panelHeight);

        // Main panel
        shapeRenderer.setColor(0.10f, 0.10f, 0.10f, 0.96f);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);

        // Top accent strip
        shapeRenderer.setColor(0.18f, 0.18f, 0.18f, 1f);
        shapeRenderer.rect(panelX, panelY + panelHeight - 16f, panelWidth, 16f);

        // Title shadow
        shapeRenderer.setColor(0f, 0f, 0f, 0.30f);
        shapeRenderer.rect(titleX + 6f, titleY - 6f, titleWidth, titleHeight);

        // Title bar
        shapeRenderer.setColor(0.14f, 0.14f, 0.14f, 1f);
        shapeRenderer.rect(titleX, titleY, titleWidth, titleHeight);

        // Title highlight strip
        shapeRenderer.setColor(0.28f, 0.28f, 0.28f, 1f);
        shapeRenderer.rect(titleX, titleY + titleHeight - 10f, titleWidth, 10f);

        // Buttons
        for (int i = 0; i < options.length; i++) {
            float buttonY = menuStartY - i * (buttonHeight + buttonGap);
            boolean selected = (i == selectedIndex);

            shapeRenderer.setColor(0f, 0f, 0f, 0.25f);
            shapeRenderer.rect(buttonX + 4f, buttonY - 4f, menuWidth, buttonHeight);

            if (selected) {
                shapeRenderer.setColor(0.90f, 0.80f, 0.20f, 1f);
            } else {
                shapeRenderer.setColor(0.22f, 0.22f, 0.22f, 1f);
            }
            shapeRenderer.rect(buttonX, buttonY, menuWidth, buttonHeight);

            if (selected) {
                shapeRenderer.setColor(0.98f, 0.90f, 0.40f, 1f);
            } else {
                shapeRenderer.setColor(0.34f, 0.34f, 0.34f, 1f);
            }
            shapeRenderer.rect(buttonX, buttonY + buttonHeight - 8f, menuWidth, 8f);
        }

        // Hint box
        shapeRenderer.setColor(0f, 0f, 0f, 0.20f);
        shapeRenderer.rect(hintBoxX + 3f, hintBoxY - 3f, hintBoxWidth, hintBoxHeight);

        shapeRenderer.setColor(0.14f, 0.14f, 0.14f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY, hintBoxWidth, hintBoxHeight);

        shapeRenderer.setColor(0.28f, 0.28f, 0.28f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY + hintBoxHeight - 6f, hintBoxWidth, 6f);

        shapeRenderer.end();

        batch.begin();

        // Title
        String title = "PAUSED";
        layout.setText(titleFont, title);

        titleFont.setColor(0f, 0f, 0f, 0.45f);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f + 2f,
                titleY + titleHeight / 2f + layout.height / 2f + 2f);

        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f,
                titleY + titleHeight / 2f + layout.height / 2f);

        // Buttons text
        for (int i = 0; i < options.length; i++) {
            float buttonY = menuStartY - i * (buttonHeight + buttonGap);
            String option = options[i];

            layout.setText(optionFont, option);

            optionFont.setColor(0f, 0f, 0f, 0.35f);
            optionFont.draw(batch, option,
                    screenW / 2f - layout.width / 2f + 2f,
                    buttonY + buttonHeight / 2f + layout.height / 2f + 2f);

            optionFont.setColor(i == selectedIndex ? Color.BLACK : Color.WHITE);
            optionFont.draw(batch, option,
                    screenW / 2f - layout.width / 2f,
                    buttonY + buttonHeight / 2f + layout.height / 2f);
        }

        // Hint
        String hint = "ESC: Resume   |   ENTER / click: Select";
        layout.setText(hintFont, hint);

        hintFont.setColor(0.92f, 0.92f, 0.92f, 1f);
        hintFont.draw(batch, hint,
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