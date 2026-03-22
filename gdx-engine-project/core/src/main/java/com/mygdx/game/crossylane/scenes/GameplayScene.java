package com.mygdx.game.crossylane.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.entities.CarEntity;
import com.mygdx.game.crossylane.entities.EntityFactory;
import com.mygdx.game.crossylane.entities.GoalZoneEntity;
import com.mygdx.game.crossylane.entities.GrassZoneEntity;
import com.mygdx.game.crossylane.entities.LaneMarkerEntity;
import com.mygdx.game.crossylane.entities.PlayerEntity;
import com.mygdx.game.crossylane.entities.additional_entity.CoinEntity;
import com.mygdx.game.crossylane.entities.additional_entity.TrafficLightEntity;
import com.mygdx.game.crossylane.state.CrossyLaneSession;
import com.mygdx.game.crossylane.traffic.TrafficLightController;
import com.mygdx.game.crossylane.ui.GameplayHudOverlay;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.managers.CollisionManager;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.managers.MovementManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class GameplayScene implements IScene<CrossyLaneSceneKey> {
    private static final int STARTING_LIVES = 3;
    private static final int STARTING_LEVEL = 1;
    private static final int GOAL_SCORE_BONUS = 100;
    private static final int COIN_SCORE_BONUS = 50;
    private static final int ROAD_COINS_PER_LANE = 2;
    private static final int TOP_PATCH_COIN_COUNT = 5;
    private static final float TRAFFIC_LIGHT_LABEL_PADDING = 8f;
    private static final float TRAFFIC_LIGHT_LABEL_WIDTH = 72f;
    private static final float TRAFFIC_LIGHT_LABEL_HEIGHT = 22f;

    private final SceneManager<CrossyLaneSceneKey> sceneManager;
    private final EntityManager entityManager;
    private final IOManager ioManager;
    private final CrossyLaneSession session;

    private final List<CarEntity> cars = new ArrayList<>();
    private final List<GrassZoneEntity> grassZones = new ArrayList<>();
    private final List<LaneMarkerEntity> laneMarkers = new ArrayList<>();
    private final List<CoinEntity> coins = new ArrayList<>();
    private TrafficLightEntity trafficLight;
    private TrafficLightController trafficLightController;
    private GoalZoneEntity goalZone;
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

    private boolean worldInitialized = false;
    private int previousPlayerLaneIndex = TrafficLightController.NO_LANE_INDEX;

    public GameplayScene(
            CrossyLaneSession session,
            SceneManager<CrossyLaneSceneKey> sceneManager,
            EntityManager entityManager,
            MovementManager movementManager,
            CollisionManager collisionManager,
            IOManager ioManager) {

        this.session = session;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
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
        grassZones.clear();
        laneMarkers.clear();
        coins.clear();
        trafficLight = null;
        trafficLightController = null;
        goalZone = null;

        resetHudState();

        grassZones.addAll(EntityFactory.createDemoGrassZones());
        for (GrassZoneEntity grass : grassZones) {
            entityManager.addEntity(grass);
        }

        goalZone = EntityFactory.createGoalZone();
        entityManager.addEntity(goalZone);

        laneMarkers.addAll(EntityFactory.createLaneMarkers(getPlayableLaneCount()));
        for (LaneMarkerEntity marker : laneMarkers) {
            entityManager.addEntity(marker);
        }

        cars.addAll(EntityFactory.createDemoCars());
        for (CarEntity car : cars) {
            entityManager.addEntity(car);
        }

        // Keep coins scattered on the road lanes.
        coins.addAll(EntityFactory.createCoinsForRoadLanes(
                getPlayableLaneCount(),
                ROAD_COINS_PER_LANE));

        // Add extra random coins across the top green patch, below the yellow goal area.
        coins.addAll(EntityFactory.createCoinsForTopPatch(
                TOP_PATCH_COIN_COUNT,
                getRoadTopY(),
                CrossyLaneConfig.GOAL_ZONE_Y));
        for (CoinEntity coin : coins) {
            entityManager.addEntity(coin);
        }

        trafficLightController = new TrafficLightController(
                CrossyLaneConfig.CONTROLLED_TRAFFIC_LIGHT_LANE_INDEX,
                CrossyLaneConfig.TRAFFIC_LIGHT_SWITCH_INTERVAL,
                CrossyLaneConfig.TRAFFIC_LIGHT_RED_SCORE_PENALTY,
                CrossyLaneConfig.TRAFFIC_LIGHT_GREEN_SCORE_BONUS);
        trafficLight = EntityFactory.createTrafficLight();
        entityManager.addEntity(trafficLight);

        player = EntityFactory.createPlayer();
        entityManager.addEntity(player);
        previousPlayerLaneIndex = getPlayerLaneIndex();
    }

    public void resetGame() {
        resetHudState();
        session.setPlayerWon(false);

        clearWorldEntities();

        worldInitialized = false;
        initializeWorld();
        worldInitialized = true;
    }

    private void clearWorldEntities() {
        for (CarEntity car : cars) {
            entityManager.removeEntity(car);
        }
        cars.clear();

        for (GrassZoneEntity grass : grassZones) {
            entityManager.removeEntity(grass);
        }
        grassZones.clear();

        for (LaneMarkerEntity marker : laneMarkers) {
            entityManager.removeEntity(marker);
        }
        laneMarkers.clear();

        for (CoinEntity coin : coins) {
            entityManager.removeEntity(coin);
        }
        coins.clear();

        if (trafficLight != null) {
            entityManager.removeEntity(trafficLight);
            trafficLight = null;
        }
        trafficLightController = null;

        if (goalZone != null) {
            entityManager.removeEntity(goalZone);
            goalZone = null;
        }

        if (player != null) {
            entityManager.removeEntity(player);
            player = null;
        }

        previousPlayerLaneIndex = TrafficLightController.NO_LANE_INDEX;
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.pushScene(CrossyLaneSceneKey.PAUSE);
            return;
        }

        if (trafficLightController != null) {
            trafficLightController.tick(delta);
        }

        wrapCars();

        if (player != null) {
            clampPlayerToScreen();

            if (player.consumeHitByCar()) {
                handlePlayerHit();
                return;
            }

            if (player.consumeReachedGoal()) {
                handleGoalReached();
                return;
            }

            checkCoinCollisions();
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
        previousPlayerLaneIndex = getPlayerLaneIndex();
    }

    private void handleGoalReached() {
        displayedScore += GOAL_SCORE_BONUS;
        displayedLevel++;

        if (player != null) {
            player.resetPosition();
        }
        session.setPlayerWon(true);
        sceneManager.changeScene(CrossyLaneSceneKey.RESULT);
    }

    private void checkCoinCollisions() {
        if (player == null) return;

        TransformComponent playerTransform = player.getComponent(TransformComponent.class);
        if (playerTransform == null) return;

        for (CoinEntity coin : coins) {
            if (coin.isCollected()) continue;

            TransformComponent coinTransform = coin.getComponent(TransformComponent.class);
            if (coinTransform == null) continue;

            // Check overlap using rectangle bounds
            float pLeft = playerTransform.getPositionX();
            float pRight = playerTransform.getRight();
            float pTop = playerTransform.getTop();
            float pBottom = playerTransform.getPositionY();
            
            float cLeft = coinTransform.getPositionX();
            float cRight = coinTransform.getRight();
            float cTop = coinTransform.getTop();
            float cBottom = coinTransform.getPositionY();

            boolean overlapX = pLeft < cRight && pRight > cLeft;
            boolean overlapY = pBottom < cTop && pTop > cBottom;

            if (overlapX && overlapY) {
                coin.collect();
                displayedScore += COIN_SCORE_BONUS;
            }
        }
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

    public void triggerGameOver() {
        session.setPlayerWon(false);
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
        applyTrafficLightScore();
        previousPlayerLaneIndex = getPlayerLaneIndex();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.20f, 0.60f, 0.20f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawRoads();
        shapeRenderer.end();

        ioManager.getOutput().renderEntities(entityManager.getEntities());
        renderTrafficLightLabel();
        ioManager.getOutput().beginTextOverlay();
        hudOverlay.render(ioManager.getOutput(), displayedScore, displayedLives, displayedLevel);
        renderTrafficLightStatusText();
        ioManager.getOutput().endTextOverlay();
    }

    private void drawRoads() {
        shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 1f);
        shapeRenderer.rect(
                0f,
                CrossyLaneConfig.ROAD_START_Y,
                WORLD_WIDTH,
                getRoadHeight());
    }

    private int getPlayableLaneCount() {
        return CrossyLaneConfig.VISIBLE_ROAD_LANE_COUNT;
    }

    private float getRoadHeight() {
        return getPlayableLaneCount() * CrossyLaneConfig.LANE_HEIGHT;
    }

    private float getRoadTopY() {
        return CrossyLaneConfig.ROAD_START_Y + getRoadHeight();
    }

    private void applyTrafficLightScore() {
        if (player == null || trafficLightController == null) {
            return;
        }

        displayedScore += trafficLightController.scoreForLaneEntry(
                previousPlayerLaneIndex,
                getPlayerLaneIndex());
    }

    private int getPlayerLaneIndex() {
        if (player == null) {
            return TrafficLightController.NO_LANE_INDEX;
        }

        TransformComponent transform = player.getComponent(TransformComponent.class);
        if (transform == null) {
            return TrafficLightController.NO_LANE_INDEX;
        }

        float playerCenterY = transform.getPositionY() + (transform.getHeight() / 2f);
        float roadBottomY = CrossyLaneConfig.ROAD_START_Y;
        float roadTopY = getRoadTopY();

        if (playerCenterY < roadBottomY || playerCenterY >= roadTopY) {
            return TrafficLightController.NO_LANE_INDEX;
        }

        return (int) ((playerCenterY - roadBottomY) / CrossyLaneConfig.LANE_HEIGHT);
    }

    private void renderTrafficLightLabel() {
        if (trafficLight == null || trafficLightController == null) {
            return;
        }

        TransformComponent transform = trafficLight.getComponent(TransformComponent.class);
        if (transform == null) {
            return;
        }

        float labelX = transform.getPositionX() + (transform.getWidth() - TRAFFIC_LIGHT_LABEL_WIDTH) / 2f;
        float labelY = transform.getTop() + TRAFFIC_LIGHT_LABEL_PADDING;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.7f);
        shapeRenderer.rect(labelX, labelY, TRAFFIC_LIGHT_LABEL_WIDTH, TRAFFIC_LIGHT_LABEL_HEIGHT);
        shapeRenderer.end();
    }

    private void renderTrafficLightStatusText() {
        if (trafficLight == null || trafficLightController == null) {
            return;
        }

        TransformComponent transform = trafficLight.getComponent(TransformComponent.class);
        if (transform == null) {
            return;
        }

        float centerX = transform.getPositionX() + (transform.getWidth() / 2f);
        float baselineY = transform.getTop() + TRAFFIC_LIGHT_LABEL_PADDING + 16f;
        ioManager.getOutput().drawCenteredText(
                trafficLightController.getCurrentPhaseName(),
                centerX,
                baselineY);
    }

    @Override
    public void dispose() {
        clearWorldEntities();

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
