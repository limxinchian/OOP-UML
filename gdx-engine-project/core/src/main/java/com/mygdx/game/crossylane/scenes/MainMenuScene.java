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
import com.mygdx.game.crossylane.state.CrossyLaneSession;
import com.mygdx.game.engine.io.MouseInput;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class MainMenuScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;
    private final IOManager ioManager;
    private final CrossyLaneSession session;
    private final CrossyLaneAudioController audioController;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;
    private OrthographicCamera uiCamera;

    private BitmapFont titleFont;
    private BitmapFont optionFont;
    private BitmapFont hintFont;

    // Added SETTINGS
    private final String[] options = {
            "START GAME",
            "INSTRUCTIONS",
            "CUSTOM MODE",
            "SETTINGS",
            "EXIT"
    };

    private int selectedIndex = 0;

    public MainMenuScene(SceneManager<CrossyLaneSceneKey> sceneManager,
                         IOManager ioManager,
                         CrossyLaneSession session,
                         CrossyLaneAudioController audioController) {
        this.navigator = new SceneNavigator(sceneManager);
        this.ioManager = ioManager;
        this.session = session;
        this.audioController = audioController;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.MAIN_MENU;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (layout == null) layout = new GlyphLayout();
        if (uiCamera == null) uiCamera = new OrthographicCamera();

        FontManager fonts = ioManager.getFontManager();
        titleFont = fonts.getFont("default", 32);
        optionFont = fonts.getFont("default", 20);
        hintFont = fonts.getFont("default", 13);

        session.reset();
        selectedIndex = 0;

        audioController.playMenuMusic();
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            activateOption(selectedIndex);
            return;
        }

        MouseInput mouse = ioManager.getMouse();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float menuWidth = clampFloat(screenW * 0.22f, 320f, 420f);
        float buttonHeight = clampFloat(screenH * 0.072f, 52f, 70f);
        float buttonGap = clampFloat(screenH * 0.018f, 14f, 22f);

        float titleWidth = clampFloat(screenW * 0.28f, 420f, 560f);
        float titleHeight = clampFloat(screenH * 0.09f, 78f, 98f);

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
                navigator.startGame();
                break;
            case 1:
                navigator.goToInstructions();
                break;
            case 2:
                navigator.goToCustomSetup();
                break;
            case 3:
                navigator.goToSettings();
                break;
            case 4:
                Gdx.app.exit();
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

        float menuWidth = clampFloat(screenW * 0.22f, 320f, 420f);
        float buttonHeight = clampFloat(screenH * 0.072f, 52f, 70f);
        float buttonGap = clampFloat(screenH * 0.018f, 14f, 22f);

        float titleWidth = clampFloat(screenW * 0.28f, 420f, 560f);
        float titleHeight = clampFloat(screenH * 0.09f, 78f, 98f);

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

        Gdx.gl.glClearColor(0.08f, 0.12f, 0.09f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background
        shapeRenderer.setColor(0.08f, 0.12f, 0.09f, 1f);
        shapeRenderer.rect(0, 0, screenW, screenH);

        // Side bands
        shapeRenderer.setColor(0.06f, 0.10f, 0.08f, 1f);
        shapeRenderer.rect(0, 0, screenW * 0.18f, screenH);
        shapeRenderer.rect(screenW * 0.82f, 0, screenW * 0.18f, screenH);

        // Center band
        shapeRenderer.setColor(0.10f, 0.17f, 0.11f, 1f);
        shapeRenderer.rect(screenW * 0.20f, 0, screenW * 0.60f, screenH);

        // Main panel shadow
        shapeRenderer.setColor(0f, 0f, 0f, 0.30f);
        shapeRenderer.rect(panelX + 10f, panelY - 10f, panelWidth, panelHeight);

        // Main panel
        shapeRenderer.setColor(0.10f, 0.18f, 0.10f, 0.92f);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);

        // Panel accent top strip
        shapeRenderer.setColor(0.18f, 0.32f, 0.18f, 1f);
        shapeRenderer.rect(panelX, panelY + panelHeight - 16f, panelWidth, 16f);

        // Title shadow
        shapeRenderer.setColor(0f, 0f, 0f, 0.28f);
        shapeRenderer.rect(titleX + 6f, titleY - 6f, titleWidth, titleHeight);

        // Title bar
        shapeRenderer.setColor(0.18f, 0.32f, 0.18f, 1f);
        shapeRenderer.rect(titleX, titleY, titleWidth, titleHeight);

        // Title highlight strip
        shapeRenderer.setColor(0.28f, 0.45f, 0.28f, 1f);
        shapeRenderer.rect(titleX, titleY + titleHeight - 10f, titleWidth, 10f);

        // Buttons
        for (int i = 0; i < options.length; i++) {
            float buttonY = menuStartY - i * (buttonHeight + buttonGap);
            boolean selected = (i == selectedIndex);

            shapeRenderer.setColor(0f, 0f, 0f, 0.22f);
            shapeRenderer.rect(buttonX + 4f, buttonY - 4f, menuWidth, buttonHeight);

            if (selected) {
                shapeRenderer.setColor(0.90f, 0.80f, 0.20f, 1f);
            } else {
                shapeRenderer.setColor(0.26f, 0.48f, 0.26f, 1f);
            }
            shapeRenderer.rect(buttonX, buttonY, menuWidth, buttonHeight);

            if (selected) {
                shapeRenderer.setColor(0.98f, 0.90f, 0.40f, 1f);
            } else {
                shapeRenderer.setColor(0.36f, 0.58f, 0.36f, 1f);
            }
            shapeRenderer.rect(buttonX, buttonY + buttonHeight - 8f, menuWidth, 8f);
        }

        // Hint box
        shapeRenderer.setColor(0f, 0f, 0f, 0.18f);
        shapeRenderer.rect(hintBoxX + 3f, hintBoxY - 3f, hintBoxWidth, hintBoxHeight);

        shapeRenderer.setColor(0.20f, 0.38f, 0.20f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY, hintBoxWidth, hintBoxHeight);

        shapeRenderer.setColor(0.30f, 0.50f, 0.30f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY + hintBoxHeight - 6f, hintBoxWidth, 6f);

        shapeRenderer.end();

        batch.begin();

        // Title
        String title = "CROSSY LANE";
        layout.setText(titleFont, title);

        titleFont.setColor(0f, 0f, 0f, 0.45f);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f + 2f,
                titleY + titleHeight / 2f + layout.height / 2f + 2f);

        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f,
                titleY + titleHeight / 2f + layout.height / 2f);

        // Button text
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

        // Hint text
        String hint = "Click To Select";
        layout.setText(hintFont, hint);
        hintFont.setColor(0.92f, 0.96f, 0.92f, 1f);
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