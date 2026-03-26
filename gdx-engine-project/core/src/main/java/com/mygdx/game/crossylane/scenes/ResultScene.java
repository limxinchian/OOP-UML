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
import com.mygdx.game.crossylane.config.LevelRegistry;
import com.mygdx.game.crossylane.state.CrossyLaneSession;
import com.mygdx.game.crossylane.ui.MenuUiTheme;
import com.mygdx.game.engine.io.MouseInput;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class ResultScene implements IScene<CrossyLaneSceneKey> {

    private final CrossyLaneSession session;
    private final SceneNavigator navigator;
    private final IOManager ioManager;
    private final LevelRegistry levelRegistry;
    private final CrossyLaneAudioController audioController;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;
    private OrthographicCamera uiCamera;

    private BitmapFont titleFont;
    private BitmapFont scoreFont;
    private BitmapFont optionFont;
    private BitmapFont hintFont;

    private String[] options;
    private int selectedIndex = 0;

    public ResultScene(CrossyLaneSession session,
                       SceneManager<CrossyLaneSceneKey> sceneManager,
                       IOManager ioManager,
                       LevelRegistry levelRegistry,
                       CrossyLaneAudioController audioController) {
        this.session = session;
        this.navigator = new SceneNavigator(sceneManager);
        this.ioManager = ioManager;
        this.levelRegistry = levelRegistry;
        this.audioController = audioController;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.RESULT;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (layout == null) layout = new GlyphLayout();
        if (uiCamera == null) uiCamera = new OrthographicCamera();

        FontManager fonts = ioManager.getFontManager();
        titleFont  = fonts.getFont("default", 28);
        scoreFont  = fonts.getFont("default", 18);
        optionFont = fonts.getFont("default", 17);
        hintFont   = fonts.getFont("default", 14);

        options = buildOptions();
        selectedIndex = 0;

        if (session.hasPlayerWon()) {
            audioController.playWinMusic();
        } else {
            audioController.playLoseMusic();
        }
    }

    private String[] buildOptions() {
        if (session.hasPlayerWon()) {
            if (session.isCustomMode()) {
                return new String[]{ "PLAY AGAIN", "MAIN MENU" };
            }
            if (levelRegistry.isFinalLevel(session.getLevelNumber())) {
                return new String[]{ "RESTART", "MAIN MENU" };
            }
            return new String[]{ "NEXT LEVEL", "RESTART", "MAIN MENU" };
        }
        return new String[]{ "RESTART", "MAIN MENU" };
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

        float menuWidth = clampFloat(screenW * 0.22f, 300f, 420f);
        float buttonHeight = clampFloat(screenH * 0.075f, 50f, 68f);
        float buttonGap = clampFloat(screenH * 0.022f, 16f, 24f);

        float titleHeight = clampFloat(screenH * 0.09f, 74f, 94f);

        float scoreBoxHeight = clampFloat(screenH * 0.06f, 42f, 52f);
        float hintBoxHeight = clampFloat(screenH * 0.045f, 34f, 42f);

        float panelTopPadding = 28f;
        float gapTitleToScore = 22f;
        float gapScoreToButtons = 26f;
        float gapButtonsToHint = 28f;
        float panelBottomPadding = 24f;

        float menuHeight = options.length * buttonHeight + (options.length - 1) * buttonGap;
        float panelHeight =
                panelTopPadding +
                titleHeight +
                gapTitleToScore +
                scoreBoxHeight +
                gapScoreToButtons +
                menuHeight +
                gapButtonsToHint +
                hintBoxHeight +
                panelBottomPadding;

        float panelY = screenH / 2f - panelHeight / 2f;

        float titleY = panelY + panelHeight - panelTopPadding - titleHeight;
        float buttonX = screenW / 2f - menuWidth / 2f;
        float menuStartY = titleY - gapTitleToScore - scoreBoxHeight - gapScoreToButtons - buttonHeight;

        for (int i = 0; i < options.length; i++) {
            float by = menuStartY - i * (buttonHeight + buttonGap);

            if (mouse.isOver(buttonX, by, menuWidth, buttonHeight)) {
                selectedIndex = i;
                if (mouse.isLeftJustPressed()) {
                    activateOption(i);
                    return;
                }
            }
        }
    }

    private void activateOption(int index) {
        String label = options[index];

        switch (label) {
            case "NEXT LEVEL":
                navigator.nextLevel();
                break;
            case "PLAY AGAIN":
                navigator.startCustomGame();
                break;
            case "RESTART":
                session.clearCustomLevel();
                navigator.restartGame();
                break;
            case "MAIN MENU":
                session.clearCustomLevel();
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

        float menuWidth = clampFloat(screenW * 0.22f, 300f, 420f);
        float buttonHeight = clampFloat(screenH * 0.075f, 50f, 68f);
        float buttonGap = clampFloat(screenH * 0.022f, 16f, 24f);

        float titleWidth = clampFloat(screenW * 0.28f, 340f, 500f);
        float titleHeight = clampFloat(screenH * 0.09f, 74f, 94f);

        float scoreBoxHeight = clampFloat(screenH * 0.06f, 42f, 52f);
        float hintBoxHeight = clampFloat(screenH * 0.045f, 34f, 42f);

        float panelPaddingX = 42f;
        float panelTopPadding = 28f;
        float gapTitleToScore = 22f;
        float gapScoreToButtons = 26f;
        float gapButtonsToHint = 28f;
        float panelBottomPadding = 24f;

        float menuHeight = options.length * buttonHeight + (options.length - 1) * buttonGap;
        float panelWidth = menuWidth + panelPaddingX * 2f;
        float panelHeight =
                panelTopPadding +
                titleHeight +
                gapTitleToScore +
                scoreBoxHeight +
                gapScoreToButtons +
                menuHeight +
                gapButtonsToHint +
                hintBoxHeight +
                panelBottomPadding;

        float panelX = screenW / 2f - panelWidth / 2f;
        float panelY = screenH / 2f - panelHeight / 2f;

        float titleX = screenW / 2f - titleWidth / 2f;
        float titleY = panelY + panelHeight - panelTopPadding - titleHeight;

        float scoreBoxWidth = menuWidth;
        float scoreBoxX = screenW / 2f - scoreBoxWidth / 2f;
        float scoreBoxY = titleY - gapTitleToScore - scoreBoxHeight;

        float buttonX = screenW / 2f - menuWidth / 2f;
        float menuStartY = scoreBoxY - gapScoreToButtons - buttonHeight;

        float hintBoxWidth = menuWidth;
        float hintBoxX = buttonX;
        float hintBoxY = panelY + panelBottomPadding;

        boolean won = session.hasPlayerWon();

        if (won) {
            Gdx.gl.glClearColor(0.06f, 0.08f, 0.14f, 1f);
        } else {
            Gdx.gl.glClearColor(0.10f, 0.03f, 0.04f, 1f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (won) {
            MenuUiTheme.drawBackdrop(shapeRenderer, screenW, screenH);
            MenuUiTheme.drawPanel(shapeRenderer, panelX, panelY, panelWidth, panelHeight);
            MenuUiTheme.drawCard(shapeRenderer, titleX, titleY, titleWidth, titleHeight);
            MenuUiTheme.drawCard(shapeRenderer, scoreBoxX, scoreBoxY, scoreBoxWidth, scoreBoxHeight);
        } else {
            drawLossBackdrop(shapeRenderer, screenW, screenH);
            drawLossPanel(shapeRenderer, panelX, panelY, panelWidth, panelHeight);
            drawLossCard(shapeRenderer, titleX, titleY, titleWidth, titleHeight);
            drawLossCard(shapeRenderer, scoreBoxX, scoreBoxY, scoreBoxWidth, scoreBoxHeight);
        }

        // Buttons
        for (int i = 0; i < options.length; i++) {
            float buttonY = menuStartY - i * (buttonHeight + buttonGap);
            boolean selected = (i == selectedIndex);

            if (won) {
                MenuUiTheme.drawButton(shapeRenderer, buttonX, buttonY, menuWidth, buttonHeight, selected);
            } else {
                drawLossButton(shapeRenderer, buttonX, buttonY, menuWidth, buttonHeight, selected);
            }
        }

        if (won) {
            MenuUiTheme.drawCard(shapeRenderer, hintBoxX, hintBoxY, hintBoxWidth, hintBoxHeight);
        } else {
            drawLossCard(shapeRenderer, hintBoxX, hintBoxY, hintBoxWidth, hintBoxHeight);
        }

        shapeRenderer.end();

        batch.begin();

        boolean customMode = session.isCustomMode();
        boolean finalStageCleared = won && !customMode && levelRegistry.isFinalLevel(session.getLevelNumber());
        String title;
        if (finalStageCleared) {
            title = "CONGRATULATIONS!";
        } else {
            title = won ? "LEVEL COMPLETE!" : "GAME OVER";
        }
        layout.setText(titleFont, title);

        titleFont.setColor(0f, 0f, 0f, 0.45f);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f + 2f,
                titleY + titleHeight / 2f + layout.height / 2f + 2f);

        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f,
                titleY + titleHeight / 2f + layout.height / 2f);

        String scoreLine = customMode
            ? "Score: " + session.getScore() + "    Mode: Custom"
            : "Score: " + session.getScore() + "    Level: " + session.getLevelNumber();
        layout.setText(scoreFont, scoreLine);

        scoreFont.setColor(1f, 0.92f, 0.35f, 1f);
        scoreFont.draw(batch, scoreLine,
                screenW / 2f - layout.width / 2f,
                scoreBoxY + scoreBoxHeight / 2f + layout.height / 2f - 1f);

        if (finalStageCleared) {
            String completionNote = "You have completed all stages!";
            layout.setText(hintFont, completionNote);
            hintFont.setColor(1f, 0.94f, 0.70f, 1f);
            hintFont.draw(batch, completionNote,
                screenW / 2f - layout.width / 2f,
                scoreBoxY - 10f);
        }

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

        String hint = "ENTER / click : Select";
        layout.setText(hintFont, hint);

        hintFont.setColor(1f, 0.94f, 0.94f, 1f);
        hintFont.draw(batch, hint,
                screenW / 2f - layout.width / 2f,
                hintBoxY + hintBoxHeight / 2f + layout.height / 2f - 2f);

        batch.end();
    }

    private float clampFloat(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private void drawLossBackdrop(ShapeRenderer renderer, float screenW, float screenH) {
        renderer.setColor(0.10f, 0.03f, 0.04f, 1f);
        renderer.rect(0, 0, screenW, screenH);

        renderer.setColor(0.08f, 0.02f, 0.03f, 1f);
        renderer.rect(0, 0, screenW * 0.18f, screenH);
        renderer.rect(screenW * 0.82f, 0, screenW * 0.18f, screenH);

        renderer.setColor(0.16f, 0.04f, 0.06f, 1f);
        renderer.rect(screenW * 0.20f, 0, screenW * 0.60f, screenH);
    }

    private void drawLossPanel(ShapeRenderer renderer, float x, float y, float width, float height) {
        renderer.setColor(0f, 0f, 0f, 0.35f);
        renderer.rect(x + 10f, y - 10f, width, height);

        renderer.setColor(0.22f, 0.06f, 0.08f, 0.96f);
        renderer.rect(x, y, width, height);

        renderer.setColor(0.42f, 0.10f, 0.12f, 1f);
        renderer.rect(x, y + height - 16f, width, 16f);
    }

    private void drawLossCard(ShapeRenderer renderer, float x, float y, float width, float height) {
        renderer.setColor(0f, 0f, 0f, 0.20f);
        renderer.rect(x + 4f, y - 4f, width, height);

        renderer.setColor(0.32f, 0.08f, 0.10f, 1f);
        renderer.rect(x, y, width, height);

        renderer.setColor(0.58f, 0.16f, 0.18f, 1f);
        renderer.rect(x, y + height - 6f, width, 6f);
    }

    private void drawLossButton(
            ShapeRenderer renderer,
            float x,
            float y,
            float width,
            float height,
            boolean selected) {
        renderer.setColor(0f, 0f, 0f, 0.25f);
        renderer.rect(x + 4f, y - 4f, width, height);

        if (selected) {
            renderer.setColor(0.95f, 0.80f, 0.20f, 1f);
        } else {
            renderer.setColor(0.38f, 0.10f, 0.12f, 1f);
        }
        renderer.rect(x, y, width, height);

        if (selected) {
            renderer.setColor(1.00f, 0.90f, 0.38f, 1f);
        } else {
            renderer.setColor(0.62f, 0.18f, 0.20f, 1f);
        }
        renderer.rect(x, y + height - 8f, width, 8f);
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