package com.mygdx.game.crossylane.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.entities.CarEntity;
import com.mygdx.game.crossylane.ui.GameplayHudOverlay;
import com.mygdx.game.crossylane.entities.PlayerEntity;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.managers.CollisionManager;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.managers.MovementManager;
import com.mygdx.game.engine.render.RenderShape;
import com.mygdx.game.engine.render.RenderableComponent;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class GameplayScene implements IScene<CrossyLaneSceneKey> {
    private static final int STARTING_LIVES = 3;
    private static final int STARTING_LEVEL = 1;
    private static final int GOAL_SCORE_BONUS = 100;

    private final SceneManager<CrossyLaneSceneKey> sceneManager;
    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;

    private final List<CarEntity> cars = new ArrayList<>();
    private final GameplayHudOverlay hudOverlay = new GameplayHudOverlay();
    private int displayedScore = 0;
    private int displayedLives = STARTING_LIVES;
    private int displayedLevel = STARTING_LEVEL;
    private PlayerEntity player;

    private ShapeRenderer shapeRenderer;

    private static final float WORLD_WIDTH = CrossyLaneConfig.WORLD_WIDTH;
    private static final float WORLD_HEIGHT = CrossyLaneConfig.WORLD_HEIGHT;

    private static final float PLAYER_START_X = CrossyLaneConfig.PLAYER_START_X;
    private static final float PLAYER_START_Y = CrossyLaneConfig.PLAYER_START_Y;

    private static final float ROAD_HEIGHT = 60f;

    private static final float LANE_1_Y = 140f;
    private static final float LANE_2_Y = 260f;
    private static final float LANE_3_Y = 380f;

    private static final float ROAD_1_Y = LANE_1_Y - 10f;
    private static final float ROAD_2_Y = LANE_2_Y - 10f;
    private static final float ROAD_3_Y = LANE_3_Y - 10f;

    private boolean worldInitialized = false;

    public GameplayScene(
            SceneManager<CrossyLaneSceneKey> sceneManager,
            EntityManager entityManager,
            MovementManager movementManager,
            CollisionManager collisionManager,
            IOManager ioManager) {

        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.collisionManager = collisionManager;
        this.ioManager = ioManager;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.GAMEPLAY;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }

        if (!worldInitialized) {
            initializeWorld();
            worldInitialized = true;
        }
    }

    @Override
    public void onExit() {
        // keep current world for pause/result flow
    }

    private void initializeWorld() {
        cars.clear();
        resetHudState();

        player = new PlayerEntity(PLAYER_START_X, PLAYER_START_Y);
        entityManager.addEntity(player);

        // Lane 1, left -> right
        cars.add(new CarEntity(0f, LANE_1_Y, 80f, 40f, 150f, 1));
        cars.add(new CarEntity(300f, LANE_1_Y, 80f, 40f, 150f, 1));

        // Lane 2, right -> left
        cars.add(new CarEntity(700f, LANE_2_Y, 80f, 40f, 180f, -1));
        cars.add(new CarEntity(400f, LANE_2_Y, 80f, 40f, 180f, -1));

        // Lane 3, left -> right
        cars.add(new CarEntity(200f, LANE_3_Y, 80f, 40f, 200f, 1));
        cars.add(new CarEntity(600f, LANE_3_Y, 80f, 40f, 200f, 1));

        for (CarEntity car : cars) {
            entityManager.addEntity(car);
        }
    }

    public void resetGame() {
        resetHudState();

        for (CarEntity car : cars) {
            entityManager.removeEntity(car);
        }
        cars.clear();

        if (player != null) {
            entityManager.removeEntity(player);
            player = null;
        }

        worldInitialized = false;
        initializeWorld();
        worldInitialized = true;
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.pushScene(CrossyLaneSceneKey.PAUSE);
            return;
        }

        wrapCars();

        if (player != null) {
            clampPlayerToScreen();

            for (CarEntity car : cars) {
                if (isColliding(player, car)) {
                    handlePlayerHit();
                    return;
                }
            }

            if (hasReachedGoal()) {
                handleGoalReached();
            }
        }
    }

    private void resetHudState() {
        displayedScore = 0;
        displayedLives = STARTING_LIVES;
        displayedLevel = STARTING_LEVEL;
    }

    private void handlePlayerHit() {
        displayedLives--;

        if (displayedLives <= 0) {
            triggerGameOver();
            return;
        }

        if (player != null) {
            player.resetPosition();
        }
    }

    private void handleGoalReached() {
        displayedScore += GOAL_SCORE_BONUS;
        displayedLevel++;

        if (player != null) {
            player.resetPosition();
        }

        sceneManager.changeScene(CrossyLaneSceneKey.RESULT);
    }

    private void wrapCars() {
        for (CarEntity car : cars) {
            TransformComponent transform = car.getComponent(TransformComponent.class);
            if (transform == null) {
                continue;
            }

            if (car.getDirection() == 1 && transform.getPositionX() > WORLD_WIDTH) {
                transform.setPositionX(-transform.getWidth());
            }

            if (car.getDirection() == -1 && transform.getRight() < 0) {
                transform.setPositionX(WORLD_WIDTH);
            }
        }
    }

    private void clampPlayerToScreen() {
        if (player == null)
            return;

        TransformComponent transform = player.getComponent(TransformComponent.class);
        if (transform == null)
            return;

        float x = transform.getPositionX();
        float y = transform.getPositionY();

        if (x < 0f)
            x = 0f;
        if (y < 0f)
            y = 0f;
        if (x + transform.getWidth() > WORLD_WIDTH) {
            x = WORLD_WIDTH - transform.getWidth();
        }
        if (y + transform.getHeight() > WORLD_HEIGHT) {
            y = WORLD_HEIGHT - transform.getHeight();
        }

        transform.setPosition(x, y);
    }

    private boolean hasReachedGoal() {
        if (player == null)
            return false;

        TransformComponent transform = player.getComponent(TransformComponent.class);
        if (transform == null)
            return false;

        return transform.getTop() >= WORLD_HEIGHT - 40f;
    }

    public void triggerGameOver() {
        sceneManager.changeScene(CrossyLaneSceneKey.RESULT);
    }

    public int getDisplayedScore() {
        return displayedScore;
    }

    public int getDisplayedLives() {
        return displayedLives;
    }

    public int getDisplayedLevel() {
        return displayedLevel;
    }

    @Override
    public void afterWorldUpdate(float delta) {
    }

    private boolean isColliding(Entity a, Entity b) {
        TransformComponent ta = a.getComponent(TransformComponent.class);
        TransformComponent tb = b.getComponent(TransformComponent.class);

        if (ta == null || tb == null) {
            return false;
        }

        return ta.getPositionX() < tb.getPositionX() + tb.getWidth()
                && ta.getPositionX() + ta.getWidth() > tb.getPositionX()
                && ta.getPositionY() < tb.getPositionY() + tb.getHeight()
                && ta.getPositionY() + ta.getHeight() > tb.getPositionY();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.20f, 0.60f, 0.20f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawGrassZones();
        drawRoads();
        drawLaneDividers();

        shapeRenderer.end();

        ioManager.getOutput().renderEntities(entityManager.getEntities());
        ioManager.getOutput().beginTextOverlay();
        hudOverlay.render(ioManager.getOutput(), displayedScore, displayedLives, displayedLevel);
        ioManager.getOutput().endTextOverlay();
    }

    private void drawGrassZones() {
        shapeRenderer.setColor(0.20f, 0.60f, 0.20f, 1f);

        shapeRenderer.rect(0f, 0f, WORLD_WIDTH, ROAD_1_Y);
        shapeRenderer.rect(0f, ROAD_1_Y + ROAD_HEIGHT, WORLD_WIDTH,
                ROAD_2_Y - (ROAD_1_Y + ROAD_HEIGHT));
        shapeRenderer.rect(0f, ROAD_2_Y + ROAD_HEIGHT, WORLD_WIDTH,
                ROAD_3_Y - (ROAD_2_Y + ROAD_HEIGHT));
        shapeRenderer.rect(0f, ROAD_3_Y + ROAD_HEIGHT, WORLD_WIDTH,
                WORLD_HEIGHT - (ROAD_3_Y + ROAD_HEIGHT));
    }

    private void drawRoads() {
        shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 1f);
        shapeRenderer.rect(0f, ROAD_1_Y, WORLD_WIDTH, ROAD_HEIGHT);
        shapeRenderer.rect(0f, ROAD_2_Y, WORLD_WIDTH, ROAD_HEIGHT);
        shapeRenderer.rect(0f, ROAD_3_Y, WORLD_WIDTH, ROAD_HEIGHT);
    }

    private void drawLaneDividers() {
        shapeRenderer.setColor(0.85f, 0.85f, 0.85f, 1f);
        drawLaneDivider(LANE_1_Y + 15f);
        drawLaneDivider(LANE_2_Y + 15f);
        drawLaneDivider(LANE_3_Y + 15f);
    }

    private void drawLaneDivider(float y) {
        float dashWidth = 30f;
        float dashHeight = 4f;
        float gap = 25f;

        for (float x = 0f; x < WORLD_WIDTH; x += dashWidth + gap) {
            shapeRenderer.rect(x, y, dashWidth, dashHeight);
        }
    }

    @Override
    public void dispose() {
        for (CarEntity car : cars) {
            entityManager.removeEntity(car);
        }
        cars.clear();

        if (player != null) {
            entityManager.removeEntity(player);
            player = null;
        }

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }

        worldInitialized = false;
    }

    @Override
    public boolean updatesWorld() {
        return true;
    }
}
