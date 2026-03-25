package com.mygdx.game.crossylane.scenes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.config.LaneDefinition;
import com.mygdx.game.crossylane.config.LevelDefinition;
import com.mygdx.game.crossylane.config.TrafficLightDefinition;
import com.mygdx.game.crossylane.state.CrossyLaneSession;
import com.mygdx.game.engine.io.MouseInput;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

/**
 * Custom Level / Sandbox setup screen.
 *
 * Lets the player configure vehicle speed, number of lanes, and vehicles
 * per lane, then launches a one-off level with those parameters.
 *
 * This scene explicitly showcases engine scalability: the player can set
 * any combination (1–5 lanes, 1–6 vehicles, 80–400 px/s speed) and the
 * engine handles it without any special-casing. The configured values are
 * wrapped in a standard LevelDefinition and fed to the same GameplayScene
 * that handles registry levels — zero code branches for "custom" vs "normal".
 *
 * Controls:
 * - UP/DOWN or mouse hover to select a parameter row
 * - LEFT/RIGHT or mouse click on [-]/[+] to adjust values
 * - ENTER or click START to launch
 * - ESC to return to main menu
 */
public class CustomSetupScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;
    private final IOManager ioManager;
    private final CrossyLaneSession session;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;

    private BitmapFont titleFont;
    private BitmapFont labelFont;
    private BitmapFont valueFont;
    private BitmapFont hintFont;

    // -- Configurable parameters ------------------------------------------------
    private int laneCount = 3;
    private int vehiclesPerLane = 2;
    private int speedLevel = 2;          // index into SPEED_OPTIONS

    private static final int MIN_LANES = 1;
    private static final int MAX_LANES = 5;
    private static final int MIN_VEHICLES = 1;
    private static final int MAX_VEHICLES = 6;

    private static final String[] SPEED_LABELS = { "Slow", "Medium", "Fast", "Very Fast", "Insane" };
    private static final float[]  SPEED_VALUES = { 100f,   180f,    260f,   340f,         420f };

    // -- UI layout --------------------------------------------------------------
    private static final int ROW_COUNT = 4;     // 3 params + START button
    private int selectedRow = 0;

    private static final float ROW_HEIGHT = 52f;
    private static final float ROW_GAP = 14f;
    private static final float PANEL_WIDTH = 520f;
    private static final float BTN_SIZE = 36f;

    public CustomSetupScene(SceneManager<CrossyLaneSceneKey> sceneManager,
                            IOManager ioManager,
                            CrossyLaneSession session) {
        this.navigator = new SceneNavigator(sceneManager);
        this.ioManager = ioManager;
        this.session = session;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.CUSTOM_SETUP;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (batch == null) batch = new SpriteBatch();
        if (layout == null) layout = new GlyphLayout();

        FontManager fonts = ioManager.getFontManager();
        titleFont = fonts.getFont("default", 26);
        labelFont = fonts.getFont("default", 17);
        valueFont = fonts.getFont("default", 20);
        hintFont  = fonts.getFont("default", 13);

        // Reset to sensible defaults
        laneCount = 3;
        vehiclesPerLane = 2;
        speedLevel = 2;
        selectedRow = 0;
    }

    @Override
    public void onExit() { }

    @Override
    public void update(float delta) {
        // Navigation
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedRow = (selectedRow - 1 + ROW_COUNT) % ROW_COUNT;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedRow = (selectedRow + 1) % ROW_COUNT;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.goToMainMenu();
            return;
        }

        // Value adjustment via keyboard
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) adjustValue(selectedRow, -1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) adjustValue(selectedRow, +1);

        // Activate START
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && selectedRow == 3) {
            launchCustomGame();
            return;
        }

        // Mouse support
        MouseInput mouse = ioManager.getMouse();
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float panelX = screenW / 2f - PANEL_WIDTH / 2f;
        float topRowY = screenH / 2f + 70f;

        for (int row = 0; row < ROW_COUNT; row++) {
            float rowY = topRowY - row * (ROW_HEIGHT + ROW_GAP);

            // Hover detection for entire row
            if (mouse.isOver(panelX, rowY, PANEL_WIDTH, ROW_HEIGHT)) {
                selectedRow = row;
            }

            if (row < 3) {
                // [-] button
                float minusBtnX = panelX + PANEL_WIDTH - BTN_SIZE * 2 - 60f;
                if (mouse.isClickedInside(minusBtnX, rowY + 8f, BTN_SIZE, BTN_SIZE)) {
                    adjustValue(row, -1);
                }
                // [+] button
                float plusBtnX = panelX + PANEL_WIDTH - BTN_SIZE - 16f;
                if (mouse.isClickedInside(plusBtnX, rowY + 8f, BTN_SIZE, BTN_SIZE)) {
                    adjustValue(row, +1);
                }
            } else {
                // START button click
                if (mouse.isClickedInside(panelX, rowY, PANEL_WIDTH, ROW_HEIGHT)) {
                    launchCustomGame();
                    return;
                }
            }
        }
    }

    private void adjustValue(int row, int dir) {
        switch (row) {
            case 0: laneCount = clamp(laneCount + dir, MIN_LANES, MAX_LANES); break;
            case 1: vehiclesPerLane = clamp(vehiclesPerLane + dir, MIN_VEHICLES, MAX_VEHICLES); break;
            case 2: speedLevel = clamp(speedLevel + dir, 0, SPEED_VALUES.length - 1); break;
            default: break;
        }
    }

    private void launchCustomGame() {
        LevelDefinition customLevel = buildCustomLevel();
        session.reset();
        session.setCustomLevel(customLevel);
        navigator.startCustomGame();
    }

    /**
     * Builds a LevelDefinition from the player's chosen parameters.
     * Alternates lane directions for visual variety.
     * Adds a traffic light on lane 0 if there are 2+ lanes.
     */
    private LevelDefinition buildCustomLevel() {
        float baseSpeed = SPEED_VALUES[speedLevel];
        List<LaneDefinition> lanes = new ArrayList<>();

        for (int i = 0; i < laneCount; i++) {
            int direction = (i % 2 == 0) ? 1 : -1;
            float speed = baseSpeed + (i * 20f);   // slight variation per lane
            lanes.add(new LaneDefinition(i, vehiclesPerLane, speed, direction));
        }

        // Add a traffic light on lane 0 if there are at least 2 lanes
        List<TrafficLightDefinition> lights;
        if (laneCount >= 2) {
            lights = Collections.singletonList(
                    new TrafficLightDefinition(0, 3.5f, -50, 50));
        } else {
            lights = Collections.emptyList();
        }

        return new LevelDefinition(1, lanes, 2, 3, 3.5f, lights);
    }

    @Override
    public void afterWorldUpdate(float delta) { }

    @Override
    public void render() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float panelX = screenW / 2f - PANEL_WIDTH / 2f;
        float topRowY = screenH / 2f + 70f;

        Gdx.gl.glClearColor(0.08f, 0.10f, 0.18f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background panel
        float panelH = ROW_COUNT * (ROW_HEIGHT + ROW_GAP) + 120f;
        float panelY = topRowY - (ROW_COUNT - 1) * (ROW_HEIGHT + ROW_GAP) - 60f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.12f, 0.15f, 0.28f, 1f);
        shapeRenderer.rect(panelX - 20f, panelY, PANEL_WIDTH + 40f, panelH);

        // Rows
        for (int row = 0; row < ROW_COUNT; row++) {
            float rowY = topRowY - row * (ROW_HEIGHT + ROW_GAP);

            boolean isStartRow = (row == 3);
            boolean selected = (row == selectedRow);

            if (isStartRow) {
                shapeRenderer.setColor(selected
                        ? new Color(0.20f, 0.80f, 0.30f, 1f)
                        : new Color(0.15f, 0.50f, 0.20f, 1f));
                shapeRenderer.rect(panelX, rowY, PANEL_WIDTH, ROW_HEIGHT);
            } else {
                // Row background
                shapeRenderer.setColor(selected
                        ? new Color(0.20f, 0.25f, 0.45f, 1f)
                        : new Color(0.14f, 0.18f, 0.32f, 1f));
                shapeRenderer.rect(panelX, rowY, PANEL_WIDTH, ROW_HEIGHT);

                // [-] and [+] buttons
                float minusBtnX = panelX + PANEL_WIDTH - BTN_SIZE * 2 - 60f;
                float plusBtnX = panelX + PANEL_WIDTH - BTN_SIZE - 16f;
                float btnY = rowY + 8f;

                shapeRenderer.setColor(0.30f, 0.35f, 0.55f, 1f);
                shapeRenderer.rect(minusBtnX, btnY, BTN_SIZE, BTN_SIZE);
                shapeRenderer.rect(plusBtnX, btnY, BTN_SIZE, BTN_SIZE);
            }
        }
        shapeRenderer.end();

        // Text
        batch.begin();

        // Title
        titleFont.setColor(Color.WHITE);
        layout.setText(titleFont, "CUSTOM LEVEL");
        titleFont.draw(batch, "CUSTOM LEVEL",
                screenW / 2f - layout.width / 2f,
                topRowY + ROW_HEIGHT + 30f);

        // Parameter rows
        String[] labels = { "Lanes", "Vehicles / Lane", "Speed" };
        String[] values = {
                String.valueOf(laneCount),
                String.valueOf(vehiclesPerLane),
                SPEED_LABELS[speedLevel] + " (" + (int) SPEED_VALUES[speedLevel] + ")"
        };

        for (int row = 0; row < 3; row++) {
            float rowY = topRowY - row * (ROW_HEIGHT + ROW_GAP);
            float textY = rowY + ROW_HEIGHT / 2f + 6f;

            labelFont.setColor(Color.LIGHT_GRAY);
            labelFont.draw(batch, labels[row], panelX + 20f, textY);

            valueFont.setColor(Color.WHITE);
            layout.setText(valueFont, values[row]);
            float valueX = panelX + PANEL_WIDTH - BTN_SIZE * 2 - 80f - layout.width;
            valueFont.draw(batch, values[row], valueX, textY);

            // [-] [+] text
            float minusBtnX = panelX + PANEL_WIDTH - BTN_SIZE * 2 - 60f;
            float plusBtnX = panelX + PANEL_WIDTH - BTN_SIZE - 16f;
            float btnTextY = rowY + 8f + BTN_SIZE / 2f + 6f;

            valueFont.setColor(Color.WHITE);
            layout.setText(valueFont, "-");
            valueFont.draw(batch, "-",
                    minusBtnX + BTN_SIZE / 2f - layout.width / 2f, btnTextY);
            layout.setText(valueFont, "+");
            valueFont.draw(batch, "+",
                    plusBtnX + BTN_SIZE / 2f - layout.width / 2f, btnTextY);
        }

        // START button
        float startRowY = topRowY - 3 * (ROW_HEIGHT + ROW_GAP);
        valueFont.setColor(selectedRow == 3 ? Color.WHITE : Color.LIGHT_GRAY);
        layout.setText(valueFont, "START");
        valueFont.draw(batch, "START",
                screenW / 2f - layout.width / 2f,
                startRowY + ROW_HEIGHT / 2f + 8f);

        // Hint
        hintFont.setColor(0.6f, 0.65f, 0.8f, 1f);
        String hint = "UP/DOWN: select row  |  LEFT/RIGHT or click [-][+]: adjust  |  ENTER: start  |  ESC: back";
        layout.setText(hintFont, hint);
        hintFont.draw(batch, hint,
                screenW / 2f - layout.width / 2f, panelY - 10f);

        batch.end();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }

    @Override
    public boolean updatesWorld() { return false; }
}
