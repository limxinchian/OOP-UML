package com.mygdx.game.crossylane.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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

/**
 * Pause overlay — pushed on top of the gameplay scene.
 *
 * Phase 3 changes:
 * - Fonts loaded through engine's FontManager (no per-scene BitmapFont ownership).
 * - Mouse hover + click support via engine's MouseInput.
 */
public class PauseScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;
    private final IOManager ioManager;
    private final CrossyLaneAudioController audioController;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;

    private BitmapFont titleFont;
    private BitmapFont optionFont;
    private BitmapFont hintFont;

    private final String[] options = { "RESUME", "RESTART", "MAIN MENU" };
    private int selectedIndex = 0;

    private static final float BOX_WIDTH = 300f;
    private static final float BOX_HEIGHT = 50f;
    private static final float BOX_GAP = 18f;

    public PauseScene(SceneManager<CrossyLaneSceneKey> sceneManager, IOManager ioManager,
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

        FontManager fonts = ioManager.getFontManager();
        titleFont  = fonts.getFont("default", 28);
        optionFont = fonts.getFont("default", 18);
        hintFont   = fonts.getFont("default", 15);

        selectedIndex = 0;
        audioController.pauseMusic();
    }

    @Override
    public void onExit() { }

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

        // Mouse support
        MouseInput mouse = ioManager.getMouse();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float startY = screenH / 2f + 22f;

        for (int i = 0; i < options.length; i++) {
            float bx = screenW / 2f - BOX_WIDTH / 2f;
            float by = startY - i * (BOX_HEIGHT + BOX_GAP);

            if (mouse.isOver(bx, by, BOX_WIDTH, BOX_HEIGHT)) {
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
            case 0: audioController.resumeMusic(); navigator.resumeGame(); break;
            case 1: navigator.restartGame(); break;
            case 2: navigator.goToMainMenu(); break;
            default: break;
        }
    }

    @Override
    public void afterWorldUpdate(float delta) { }

    @Override
    public void render() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float startY = screenH / 2f + 22f;

        float panelX = screenW / 2f - 240f;
        float panelY = screenH / 2f - 190f;
        float panelW = 480f;
        float panelH = 350f;

        Gdx.gl.glClearColor(0.06f, 0.06f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(panelX, panelY, panelW, panelH);

        for (int i = 0; i < options.length; i++) {
            float x = screenW / 2f - BOX_WIDTH / 2f;
            float y = startY - i * (BOX_HEIGHT + BOX_GAP);

            shapeRenderer.setColor(i == selectedIndex
                    ? new Color(0.90f, 0.75f, 0.20f, 1f)
                    : new Color(0.35f, 0.35f, 0.35f, 1f));
            shapeRenderer.rect(x, y, BOX_WIDTH, BOX_HEIGHT);
        }
        shapeRenderer.end();

        batch.begin();

        titleFont.setColor(Color.WHITE);
        layout.setText(titleFont, "PAUSED");
        titleFont.draw(batch, "PAUSED",
                screenW / 2f - layout.width / 2f, screenH / 2f + 120f);

        for (int i = 0; i < options.length; i++) {
            float y = startY - i * (BOX_HEIGHT + BOX_GAP);
            optionFont.setColor(i == selectedIndex ? Color.BLACK : Color.WHITE);
            layout.setText(optionFont, options[i]);
            optionFont.draw(batch, options[i],
                    screenW / 2f - layout.width / 2f,
                    y + BOX_HEIGHT / 2f + layout.height / 2f);
        }

        hintFont.setColor(1f, 0.92f, 0.2f, 1f);
        String hint = "ESC: Resume  |  ENTER or click: Select";
        layout.setText(hintFont, hint);
        hintFont.draw(batch, hint,
                screenW / 2f - layout.width / 2f, panelY + 38f);

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
