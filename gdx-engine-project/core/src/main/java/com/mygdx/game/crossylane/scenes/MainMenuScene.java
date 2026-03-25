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
import com.mygdx.game.crossylane.state.CrossyLaneSession;
import com.mygdx.game.engine.io.MouseInput;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

/**
 * Main menu screen — entry point of the game.
 *
 * Phase 5 changes:
 * - Added "CUSTOM MODE" option that navigates to the sandbox setup screen.
 * - Clears session on enter so starting a new game is always fresh.
 */
public class MainMenuScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;
    private final IOManager ioManager;
    private final CrossyLaneSession session;
    private final CrossyLaneAudioController audioController;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;

    private BitmapFont titleFont;
    private BitmapFont optionFont;

    private final String[] options = { "START GAME", "INSTRUCTIONS", "CUSTOM MODE", "EXIT" };
    private int selectedIndex = 0;

    private static final float BOX_WIDTH = 320f;
    private static final float BOX_HEIGHT = 55f;
    private static final float BOX_GAP = 18f;

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

        FontManager fonts = ioManager.getFontManager();
        titleFont  = fonts.getFont("default", 32);
        optionFont = fonts.getFont("default", 20);

        session.reset();
        selectedIndex = 0;
        audioController.playMenuMusic();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            activateOption(selectedIndex);
            return;
        }

        MouseInput mouse = ioManager.getMouse();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float startY = screenH / 2f + 40f;

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
            case 0: navigator.startGame(); break;
            case 1: navigator.goToInstructions(); break;
            case 2: navigator.goToCustomSetup(); break;
            case 3: Gdx.app.exit(); break;
            default: break;
        }
    }

    @Override
    public void afterWorldUpdate(float delta) { }

    @Override
    public void render() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float startY = screenH / 2f + 40f;

        Gdx.gl.glClearColor(0.12f, 0.18f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.18f, 0.30f, 0.18f, 1f);
        shapeRenderer.rect(screenW / 2f - 220f, screenH - 130f, 440f, 80f);

        for (int i = 0; i < options.length; i++) {
            float x = screenW / 2f - BOX_WIDTH / 2f;
            float y = startY - i * (BOX_HEIGHT + BOX_GAP);

            shapeRenderer.setColor(i == selectedIndex
                    ? new Color(0.95f, 0.85f, 0.25f, 1f)
                    : new Color(0.25f, 0.45f, 0.25f, 1f));
            shapeRenderer.rect(x, y, BOX_WIDTH, BOX_HEIGHT);
        }
        shapeRenderer.end();

        batch.begin();

        titleFont.setColor(Color.WHITE);
        layout.setText(titleFont, "CROSSY LANE");
        titleFont.draw(batch, "CROSSY LANE",
                screenW / 2f - layout.width / 2f, screenH - 75f);

        for (int i = 0; i < options.length; i++) {
            float y = startY - i * (BOX_HEIGHT + BOX_GAP);
            optionFont.setColor(i == selectedIndex ? Color.BLACK : Color.WHITE);
            layout.setText(optionFont, options[i]);
            optionFont.draw(batch, options[i],
                    screenW / 2f - layout.width / 2f,
                    y + BOX_HEIGHT / 2f + layout.height / 2f);
        }

        optionFont.setColor(Color.LIGHT_GRAY);
        String hint = "Arrow keys or mouse to navigate, ENTER or click to select";
        layout.setText(optionFont, hint);
        optionFont.draw(batch, hint,
                screenW / 2f - layout.width / 2f, 50f);

        batch.end();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }

    @Override
    public boolean updatesWorld() { return false; }
}
