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
import com.mygdx.game.crossylane.config.LevelRegistry;
import com.mygdx.game.crossylane.state.CrossyLaneSession;
import com.mygdx.game.engine.io.MouseInput;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

/**
 * Win / Game-Over result screen.
 *
 * Phase 5 changes:
 * - Displays the player's score from session.
 * - When the player won: shows "NEXT LEVEL", "RESTART", "MAIN MENU".
 *   "Next Level" preserves score and lives for the next level.
 * - When the player lost (or won the final level / custom mode):
 *   shows "RESTART", "MAIN MENU".
 * - In custom mode, winning shows "PLAY AGAIN" instead of "NEXT LEVEL".
 */
public class ResultScene implements IScene<CrossyLaneSceneKey> {

    private final CrossyLaneSession session;
    private final SceneNavigator navigator;
    private final IOManager ioManager;
    private final LevelRegistry levelRegistry;
    private final CrossyLaneAudioController audioController;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;

    private BitmapFont titleFont;
    private BitmapFont scoreFont;
    private BitmapFont optionFont;
    private BitmapFont hintFont;

    private String[] options;
    private int selectedIndex = 0;

    private static final float BOX_WIDTH = 280f;
    private static final float BOX_HEIGHT = 50f;
    private static final float BOX_GAP = 16f;

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

        FontManager fonts = ioManager.getFontManager();
        titleFont  = fonts.getFont("default", 28);
        scoreFont  = fonts.getFont("default", 18);
        optionFont = fonts.getFont("default", 17);
        hintFont   = fonts.getFont("default", 14);

        // Build options dynamically based on win/lose and mode
        options = buildOptions();
        selectedIndex = 0;

        // Play appropriate music
        if (session.hasPlayerWon()) {
            audioController.playWinMusic();
        } else {
            audioController.playLoseMusic();
        }
    }

    private String[] buildOptions() {
        if (session.hasPlayerWon()) {
            if (session.isCustomMode()) {
                // Custom mode win: replay or leave
                return new String[]{ "PLAY AGAIN", "MAIN MENU" };
            }
            if (levelRegistry.isFinalLevel(session.getLevelNumber())) {
                // Beat the last level — no "next"
                return new String[]{ "RESTART", "MAIN MENU" };
            }
            // Won a normal level with more to go
            return new String[]{ "NEXT LEVEL", "RESTART", "MAIN MENU" };
        }
        // Lost
        return new String[]{ "RESTART", "MAIN MENU" };
    }

    @Override
    public void onExit() { }

    @Override
    public void update(float delta) {
        // Keyboard
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

        // Mouse
        MouseInput mouse = ioManager.getMouse();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float startY = computeStartY(screenH);

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
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float startY = computeStartY(screenH);

        float panelX = screenW / 2f - 240f;
        float panelY = screenH / 2f - 200f;
        float panelW = 480f;
        float panelH = 400f;

        boolean won = session.hasPlayerWon();

        Gdx.gl.glClearColor(won ? 0.08f : 0.25f, won ? 0.20f : 0.08f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(won ? 0.10f : 0.35f, won ? 0.30f : 0.10f, 0.10f, 1f);
        shapeRenderer.rect(panelX, panelY, panelW, panelH);

        for (int i = 0; i < options.length; i++) {
            float x = screenW / 2f - BOX_WIDTH / 2f;
            float y = startY - i * (BOX_HEIGHT + BOX_GAP);

            shapeRenderer.setColor(i == selectedIndex
                    ? new Color(0.95f, 0.80f, 0.20f, 1f)
                    : new Color(won ? 0.18f : 0.45f, won ? 0.40f : 0.18f, 0.18f, 1f));
            shapeRenderer.rect(x, y, BOX_WIDTH, BOX_HEIGHT);
        }
        shapeRenderer.end();

        batch.begin();

        // Title
        titleFont.setColor(Color.WHITE);
        String title = won ? "LEVEL COMPLETE!" : "GAME OVER";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f, panelY + panelH - 30f);

        // Score display
        scoreFont.setColor(Color.YELLOW);
        String scoreLine = "Score: " + session.getScore()
                + "    Level: " + session.getLevelNumber();
        layout.setText(scoreFont, scoreLine);
        scoreFont.draw(batch, scoreLine,
                screenW / 2f - layout.width / 2f, panelY + panelH - 70f);

        // Option buttons
        for (int i = 0; i < options.length; i++) {
            float y = startY - i * (BOX_HEIGHT + BOX_GAP);
            optionFont.setColor(i == selectedIndex ? Color.BLACK : Color.WHITE);
            layout.setText(optionFont, options[i]);
            optionFont.draw(batch, options[i],
                    screenW / 2f - layout.width / 2f,
                    y + BOX_HEIGHT / 2f + layout.height / 2f);
        }

        // Hint
        hintFont.setColor(1f, 0.92f, 0.2f, 1f);
        String hint = "ENTER or click to select";
        layout.setText(hintFont, hint);
        hintFont.draw(batch, hint,
                screenW / 2f - layout.width / 2f, panelY + 30f);

        batch.end();
    }

    private float computeStartY(float screenH) {
        return screenH / 2f - 10f;
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }

    @Override
    public boolean updatesWorld() { return false; }
}
