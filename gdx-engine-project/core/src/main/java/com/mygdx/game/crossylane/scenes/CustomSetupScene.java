package com.mygdx.game.crossylane.scenes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.config.LaneDefinition;
import com.mygdx.game.crossylane.config.LevelDefinition;
import com.mygdx.game.crossylane.config.TrafficLightDefinition;
import com.mygdx.game.crossylane.state.CrossyLaneSession;
import com.mygdx.game.engine.io.MouseInput;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.render.FontManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;
import com.mygdx.game.engine.math.MathUtil;

public class CustomSetupScene implements IScene<CrossyLaneSceneKey> {

    private final SceneNavigator navigator;
    private final IOManager ioManager;
    private final CrossyLaneSession session;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;
    private OrthographicCamera uiCamera;

    private BitmapFont titleFont;
    private BitmapFont labelFont;
    private BitmapFont valueFont;
    private BitmapFont hintFont;

    private int laneCount = 3;
    private int vehiclesPerLane = 2;
    private int speedLevel = 2;

    private static final int MIN_LANES = 1;
    private static final int MAX_LANES = 5;
    private static final int MIN_VEHICLES = 1;
    private static final int MAX_VEHICLES = 6;

    private static final String[] SPEED_LABELS = { "Slow", "Medium", "Fast", "Very Fast", "Insane" };
    private static final float[] SPEED_VALUES = { 100f, 180f, 260f, 340f, 420f };

    private static final int ROW_COUNT = 4;
    private int selectedRow = 0;

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
        if (uiCamera == null) uiCamera = new OrthographicCamera();

        FontManager fonts = ioManager.getFontManager();
        titleFont = fonts.getFont("default", 30);
        labelFont = fonts.getFont("default", 18);
        valueFont = fonts.getFont("default", 18);
        hintFont  = fonts.getFont("default", 13);

        laneCount = 3;
        vehiclesPerLane = 2;
        speedLevel = 2;
        selectedRow = 0;
    }

    @Override
    public void onExit() { }

    @Override
    public void update(float delta) {
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            adjustValue(selectedRow, -1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            adjustValue(selectedRow, 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && selectedRow == 3) {
            launchCustomGame();
            return;
        }

        MouseInput mouse = ioManager.getMouse();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float panelWidth = MathUtil.clamp(screenW * 0.48f, 520f, 760f);
        float rowHeight = MathUtil.clamp(screenH * 0.075f, 52f, 72f);
        float rowGap = MathUtil.clamp(screenH * 0.02f, 14f, 20f);
        float buttonSize = MathUtil.clamp(rowHeight * 0.72f, 36f, 46f);

        float panelX = screenW / 2f - panelWidth / 2f;
        float topRowY = screenH / 2f + rowHeight * 1.5f;

        for (int row = 0; row < ROW_COUNT; row++) {
            float rowY = topRowY - row * (rowHeight + rowGap);

            if (mouse.isOver(panelX, rowY, panelWidth, rowHeight)) {
                selectedRow = row;
            }

            if (row < 3) {
                float plusBtnX = panelX + panelWidth - buttonSize - 18f;
                float minusBtnX = plusBtnX - buttonSize - 16f;
                float btnY = rowY + (rowHeight - buttonSize) / 2f;

                if (mouse.isClickedInside(minusBtnX, btnY, buttonSize, buttonSize)) {
                    adjustValue(row, -1);
                }

                if (mouse.isClickedInside(plusBtnX, btnY, buttonSize, buttonSize)) {
                    adjustValue(row, 1);
                }
            } else {
                if (mouse.isClickedInside(panelX, rowY, panelWidth, rowHeight)) {
                    launchCustomGame();
                    return;
                }
            }
        }
    }

    private void adjustValue(int row, int dir) {
        switch (row) {
            case 0:
                laneCount = MathUtil.clamp(laneCount + dir, MIN_LANES, MAX_LANES);
                break;
            case 1:
                vehiclesPerLane = MathUtil.clamp(vehiclesPerLane + dir, MIN_VEHICLES, MAX_VEHICLES);
                break;
            case 2:
                speedLevel = MathUtil.clamp(speedLevel + dir, 0, SPEED_VALUES.length - 1);
                break;
            default:
                break;
        }
    }

    private void launchCustomGame() {
        LevelDefinition customLevel = buildCustomLevel();
        session.reset();
        session.setCustomLevel(customLevel);
        navigator.startCustomGame();
    }

    private LevelDefinition buildCustomLevel() {
        float baseSpeed = SPEED_VALUES[speedLevel];
        List<LaneDefinition> lanes = new ArrayList<>();

        for (int i = 0; i < laneCount; i++) {
            int direction = (i % 2 == 0) ? 1 : -1;
            float speed = baseSpeed + (i * 20f);
            lanes.add(new LaneDefinition(i, vehiclesPerLane, speed, direction));
        }

        List<TrafficLightDefinition> lights = Collections.singletonList(
            new TrafficLightDefinition(0, 3.5f, -50, 0));

        return new LevelDefinition(1, lanes, 2, 3, 3.5f, lights);
    }

    @Override
    public void afterWorldUpdate(float delta) { }

    @Override
    public void render() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        uiCamera.setToOrtho(false, screenW, screenH);
        uiCamera.update();

        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        batch.setProjectionMatrix(uiCamera.combined);

        float panelWidth = MathUtil.clamp(screenW * 0.48f, 520f, 760f);
        float rowHeight = MathUtil.clamp(screenH * 0.075f, 52f, 72f);
        float rowGap = MathUtil.clamp(screenH * 0.02f, 14f, 20f);
        float buttonSize = MathUtil.clamp(rowHeight * 0.72f, 36f, 46f);

        float panelX = screenW / 2f - panelWidth / 2f;
        float topRowY = screenH / 2f + rowHeight * 1.5f;

        float panelPaddingTop = 90f;
        float panelPaddingBottom = 28f;
        float hintInnerPadding = 14f;

        String hint = "UP/DOWN: select row   |   LEFT/RIGHT or click [-][+]: adjust   |   ENTER: start   |   ESC: back";
        float hintTextWidth = panelWidth - 40f;
        layout.setText(hintFont, hint, new Color(1f, 1f, 1f, 1f), hintTextWidth, 1, true);

        float hintBoxHeight = Math.max(42f, layout.height + hintInnerPadding * 2f);

        float panelHeight = panelPaddingTop
                + (ROW_COUNT * rowHeight)
                + ((ROW_COUNT - 1) * rowGap)
                + 30f
                + hintBoxHeight
                + panelPaddingBottom;

        float bottomRowY = topRowY - (ROW_COUNT - 1) * (rowHeight + rowGap);
        float panelY = bottomRowY - panelPaddingBottom - hintBoxHeight - 30f;
        float outerPanelX = panelX - 20f;
        float outerPanelY = panelY;
        float outerPanelW = panelWidth + 40f;
        float outerPanelH = panelHeight;

        float hintBoxX = panelX;
        float hintBoxY = panelY + panelPaddingBottom;

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
        shapeRenderer.rect(outerPanelX + 8f, outerPanelY - 8f, outerPanelW, outerPanelH);

        shapeRenderer.setColor(0.10f, 0.18f, 0.10f, 0.92f);
        shapeRenderer.rect(outerPanelX, outerPanelY, outerPanelW, outerPanelH);

        shapeRenderer.setColor(0.18f, 0.32f, 0.18f, 1f);
        shapeRenderer.rect(outerPanelX, outerPanelY + outerPanelH - 16f, outerPanelW, 16f);

        for (int row = 0; row < ROW_COUNT; row++) {
            float rowY = topRowY - row * (rowHeight + rowGap);
            boolean isStartRow = (row == 3);
            boolean selected = (row == selectedRow);

            if (isStartRow) {
                if (selected) {
                    shapeRenderer.setColor(0.86f, 0.75f, 0.18f, 1f);
                } else {
                    shapeRenderer.setColor(0.18f, 0.60f, 0.24f, 1f);
                }
                shapeRenderer.rect(panelX, rowY, panelWidth, rowHeight);
            } else {
                if (selected) {
                    shapeRenderer.setColor(0.24f, 0.30f, 0.24f, 1f);
                } else {
                    shapeRenderer.setColor(0.18f, 0.26f, 0.18f, 1f);
                }
                shapeRenderer.rect(panelX, rowY, panelWidth, rowHeight);

                float plusBtnX = panelX + panelWidth - buttonSize - 18f;
                float minusBtnX = plusBtnX - buttonSize - 16f;
                float btnY = rowY + (rowHeight - buttonSize) / 2f;

                shapeRenderer.setColor(0.34f, 0.49f, 0.34f, 1f);
                shapeRenderer.rect(minusBtnX, btnY, buttonSize, buttonSize);
                shapeRenderer.rect(plusBtnX, btnY, buttonSize, buttonSize);
            }
        }

        shapeRenderer.setColor(0f, 0f, 0f, 0.18f);
        shapeRenderer.rect(hintBoxX + 3f, hintBoxY - 3f, panelWidth, hintBoxHeight);

        shapeRenderer.setColor(0.20f, 0.38f, 0.20f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY, panelWidth, hintBoxHeight);

        shapeRenderer.setColor(0.30f, 0.50f, 0.30f, 1f);
        shapeRenderer.rect(hintBoxX, hintBoxY + hintBoxHeight - 6f, panelWidth, 6f);

        shapeRenderer.end();

        batch.begin();

        String title = "CUSTOM LEVEL";
        titleFont.setColor(Color.WHITE);
        layout.setText(titleFont, title);
        titleFont.draw(batch, title,
                screenW / 2f - layout.width / 2f,
                outerPanelY + outerPanelH - 34f);

        String[] labels = { "Lanes", "Vehicles", "Speed" };
        String[] values = {
                String.valueOf(laneCount),
                String.valueOf(vehiclesPerLane),
                SPEED_LABELS[speedLevel] + " (" + (int) SPEED_VALUES[speedLevel] + ")"
        };

        for (int row = 0; row < 3; row++) {
            float rowY = topRowY - row * (rowHeight + rowGap);
            float textY = rowY + rowHeight / 2f + 7f;

            labelFont.setColor(Color.LIGHT_GRAY);
            labelFont.draw(batch, labels[row], panelX + 24f, textY);

            layout.setText(valueFont, values[row]);
            float plusBtnX = panelX + panelWidth - buttonSize - 18f;
            float minusBtnX = plusBtnX - buttonSize - 16f;

            float valueX = minusBtnX - 24f - layout.width;
            valueFont.setColor(Color.WHITE);
            valueFont.draw(batch, values[row], valueX, textY);

            float btnY = rowY + (rowHeight - buttonSize) / 2f;
            float btnTextY = btnY + buttonSize / 2f + 7f;

            layout.setText(valueFont, "-");
            valueFont.draw(batch, "-",
                    minusBtnX + buttonSize / 2f - layout.width / 2f,
                    btnTextY);

            layout.setText(valueFont, "+");
            valueFont.draw(batch, "+",
                    plusBtnX + buttonSize / 2f - layout.width / 2f,
                    btnTextY);
        }

        float startRowY = topRowY - 3 * (rowHeight + rowGap);
        String startText = "START";
        valueFont.setColor(selectedRow == 3 ? Color.BLACK : Color.WHITE);
        layout.setText(valueFont, startText);
        valueFont.draw(batch, startText,
                screenW / 2f - layout.width / 2f,
                startRowY + rowHeight / 2f + 8f);

        hintFont.setColor(0.92f, 0.96f, 1f, 1f);
        layout.setText(hintFont, hint, hintFont.getColor(), hintTextWidth, 1, true);
        hintFont.draw(batch,
                hint,
                hintBoxX + 20f,
                hintBoxY + hintBoxHeight - hintInnerPadding,
                hintTextWidth,
                1,
                true);

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