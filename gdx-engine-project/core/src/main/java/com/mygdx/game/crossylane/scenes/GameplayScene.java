package com.mygdx.game.crossylane.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.config.LevelDefinition;
import com.mygdx.game.crossylane.config.LevelRegistry;
import com.mygdx.game.crossylane.entities.CarEntity;
import com.mygdx.game.crossylane.entities.EntityFactory;
import com.mygdx.game.crossylane.entities.GoalZoneEntity;
import com.mygdx.game.crossylane.entities.GrassZoneEntity;
import com.mygdx.game.crossylane.entities.LaneMarkerEntity;
import com.mygdx.game.crossylane.entities.PlayerEntity;
import com.mygdx.game.crossylane.entities.additional_entity.CoinEntity;
import com.mygdx.game.crossylane.events.CoinCollectedEvent;
import com.mygdx.game.crossylane.events.GoalReachedEvent;
import com.mygdx.game.crossylane.events.PlayerHitEvent;
import com.mygdx.game.crossylane.audio.CrossyLaneAudioController;
import com.mygdx.game.crossylane.state.CrossyLaneSession;
import com.mygdx.game.crossylane.systems.CarWrapSystem;
import com.mygdx.game.crossylane.traffic.TrafficLightController;
import com.mygdx.game.crossylane.traffic.TrafficLightSystem;
import com.mygdx.game.crossylane.ui.GameplayHudOverlay;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.event.EventBus;
import com.mygdx.game.engine.event.EventListener;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

/**
 * Main gameplay scene — orchestrates level flow, entity lifecycle, and HUD.
 *
 * Phase 5 changes:
 * -----------------------------------------------------------------------
 * 1. Progressive levels — loadNextLevel() rebuilds the world at the next
 *    level number while preserving score and lives. The session stores
 *    state across scene transitions so the Result screen can offer
 *    "Next Level" when the player wins.
 *
 * 2. Custom / Sandbox mode — if session.isCustomMode() is true, the scene
 *    uses session.getCustomLevel() instead of the LevelRegistry. This
 *    demonstrates engine scalability: any LevelDefinition (however many
 *    lanes, vehicles, and lights) works without code changes.
 *
 * 3. State persistence — score, lives, and level are saved to session
 *    before every scene transition (goal reached, game over) so the
 *    Result screen can display them accurately.
 */
public class GameplayScene implements IScene<CrossyLaneSceneKey> {
    private static final int STARTING_LIVES = 3;
    private static final int GOAL_SCORE_BONUS = 100;
    private static final int COIN_SCORE_BONUS = 50;

    // -- Dependencies -----------------------------------------------------------
    private final SceneManager<CrossyLaneSceneKey> sceneManager;
    private final EntityManager entityManager;
    private final IOManager ioManager;
    private final EventBus eventBus;
    private final CrossyLaneSession session;
    private final LevelRegistry levelRegistry;
    private final CrossyLaneAudioController audioController;

    // -- World entities ---------------------------------------------------------
    private final List<CarEntity> cars = new ArrayList<>();
    private final List<GrassZoneEntity> grassZones = new ArrayList<>();
    private final List<LaneMarkerEntity> laneMarkers = new ArrayList<>();
    private final List<CoinEntity> coins = new ArrayList<>();
    private final TrafficLightSystem trafficLightSystem = new TrafficLightSystem();
    private GoalZoneEntity goalZone;
    private PlayerEntity player;

    // -- HUD & rendering --------------------------------------------------------
    private final GameplayHudOverlay hudOverlay = new GameplayHudOverlay();
    private ShapeRenderer shapeRenderer;
    private SpriteBatch hudBatch;
    private GlyphLayout glyphLayout;

    // -- Game state --------------------------------------------------------------
    private int displayedScore = 0;
    private int displayedLives = STARTING_LIVES;
    private int currentLevelNumber = 1;
    private boolean worldInitialized = false;
    private int previousPlayerLaneIndex = TrafficLightController.NO_LANE_INDEX;

    private boolean pendingHit = false;
    private boolean pendingGoal = false;

    // -- Event listeners --------------------------------------------------------
    private final EventListener<PlayerHitEvent> onPlayerHit = event -> pendingHit = true;
    private final EventListener<GoalReachedEvent> onGoalReached = event -> pendingGoal = true;
    private final EventListener<CoinCollectedEvent> onCoinCollected = event -> displayedScore += COIN_SCORE_BONUS;

    public GameplayScene(
            CrossyLaneSession session,
            SceneManager<CrossyLaneSceneKey> sceneManager,
            EntityManager entityManager,
            IOManager ioManager,
            EventBus eventBus,
            LevelRegistry levelRegistry,
            CrossyLaneAudioController audioController) {

        this.session = session;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.ioManager = ioManager;
        this.eventBus = eventBus;
        this.levelRegistry = levelRegistry;
        this.audioController = audioController;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.GAMEPLAY;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();
        if (hudBatch == null) hudBatch = new SpriteBatch();
        if (glyphLayout == null) glyphLayout = new GlyphLayout();

        subscribeEvents();
        audioController.playGameplayMusic();

        if (!worldInitialized) {
            initializeWorld();
            worldInitialized = true;
        }
    }

    @Override
    public void onExit() {
        unsubscribeEvents();
    }

    // -- Event wiring -----------------------------------------------------------

    private void subscribeEvents() {
        eventBus.subscribe(PlayerHitEvent.class, onPlayerHit);
        eventBus.subscribe(GoalReachedEvent.class, onGoalReached);
        eventBus.subscribe(CoinCollectedEvent.class, onCoinCollected);
    }

    private void unsubscribeEvents() {
        eventBus.unsubscribe(PlayerHitEvent.class, onPlayerHit);
        eventBus.unsubscribe(GoalReachedEvent.class, onGoalReached);
        eventBus.unsubscribe(CoinCollectedEvent.class, onCoinCollected);
    }

    // -- Level resolution -------------------------------------------------------

    /**
     * Returns the active LevelDefinition — either from the custom session
     * or from the registry. This is the single point of level resolution.
     */
    private LevelDefinition getActiveLevel() {
        if (session.isCustomMode()) {
            return session.getCustomLevel();
        }
        return levelRegistry.getLevel(currentLevelNumber);
    }

    // -- World initialisation ---------------------------------------------------

    private void initializeWorld() {
        clearWorldEntities();

        LevelDefinition level = getActiveLevel();

        grassZones.add(EntityFactory.createBottomGrass());
        for (GrassZoneEntity grass : grassZones) entityManager.addEntity(grass);

        goalZone = EntityFactory.createGoalZone();
        entityManager.addEntity(goalZone);

        laneMarkers.addAll(EntityFactory.createLaneMarkers(level.getLaneCount()));
        for (LaneMarkerEntity marker : laneMarkers) entityManager.addEntity(marker);

        cars.addAll(EntityFactory.createCarsForLevel(level));
        for (CarEntity car : cars) entityManager.addEntity(car);

        coins.addAll(EntityFactory.createCoinsForRoadLanes(
                level.getLaneCount(), level.getCoinsPerLane(), eventBus));
        coins.addAll(EntityFactory.createCoinsForTopPatch(
                level.getTopPatchCoins(), getRoadTopY(level),
                CrossyLaneConfig.GOAL_ZONE_Y, eventBus));
        for (CoinEntity coin : coins) entityManager.addEntity(coin);

        trafficLightSystem.initialize(level.getTrafficLights(), entityManager, eventBus);

        player = EntityFactory.createPlayer(eventBus);
        entityManager.addEntity(player);
        previousPlayerLaneIndex = getPlayerLaneIndex();
    }

    // -- Update loop ------------------------------------------------------------

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.pushScene(CrossyLaneSceneKey.PAUSE);
            return;
        }

        trafficLightSystem.tick(delta);
        CarWrapSystem.wrapAll(cars, CrossyLaneConfig.WORLD_WIDTH);

        if (player != null) {
            clampPlayerToPlayArea();
        }
    }

    @Override
    public void afterWorldUpdate(float delta) {
        if (pendingHit) {
            pendingHit = false;
            handlePlayerHit();
            return;
        }
        if (pendingGoal) {
            pendingGoal = false;
            handleGoalReached();
            return;
        }

        applyTrafficLightScore();
        previousPlayerLaneIndex = getPlayerLaneIndex();
    }

    // -- Game logic -------------------------------------------------------------

    private void handlePlayerHit() {
        displayedLives--;

        if (displayedLives <= 0) {
            triggerGameOver();
            return;
        }

        if (player != null) player.resetPosition();
        previousPlayerLaneIndex = getPlayerLaneIndex();
    }

    private void handleGoalReached() {
        displayedScore += GOAL_SCORE_BONUS;

        if (!session.isCustomMode()) {
            currentLevelNumber++;
        }

        saveStateToSession();
        session.setPlayerWon(true);
        sceneManager.changeScene(CrossyLaneSceneKey.RESULT);
    }

    private void triggerGameOver() {
        saveStateToSession();
        session.setPlayerWon(false);
        sceneManager.changeScene(CrossyLaneSceneKey.RESULT);
    }

    /** Persists current gameplay state to session before scene transitions. */
    private void saveStateToSession() {
        session.setScore(displayedScore);
        session.setLives(displayedLives);
        session.setLevelNumber(currentLevelNumber);
    }

    // -- Level lifecycle (called by SceneNavigator) -----------------------------

    /**
     * Full reset — new game from level 1 with default score/lives.
     * If session has a custom level, uses that; otherwise uses registry.
     */
    public void resetGame() {
        if (!session.isCustomMode()) {
            currentLevelNumber = 1;
        }
        displayedScore = 0;
        displayedLives = STARTING_LIVES;
        session.setPlayerWon(false);

        worldInitialized = false;
        clearWorldEntities();
        initializeWorld();
        worldInitialized = true;
    }

    /**
     * Load the next level, preserving score and lives from the current run.
     * Called by SceneNavigator.nextLevel() when player chooses "Next Level".
     */
    public void loadNextLevel() {
        // currentLevelNumber was already incremented in handleGoalReached()
        displayedScore = session.getScore();
        displayedLives = session.getLives();
        currentLevelNumber = session.getLevelNumber();

        worldInitialized = false;
        clearWorldEntities();
        initializeWorld();
        worldInitialized = true;
    }

    // -- Teardown ---------------------------------------------------------------

    private void clearWorldEntities() {
        for (CarEntity car : cars) entityManager.removeEntity(car);
        cars.clear();

        for (GrassZoneEntity grass : grassZones) entityManager.removeEntity(grass);
        grassZones.clear();

        for (LaneMarkerEntity marker : laneMarkers) entityManager.removeEntity(marker);
        laneMarkers.clear();

        for (CoinEntity coin : coins) entityManager.removeEntity(coin);
        coins.clear();

        trafficLightSystem.clear(entityManager);

        if (goalZone != null) { entityManager.removeEntity(goalZone); goalZone = null; }
        if (player != null) { entityManager.removeEntity(player); player = null; }

        pendingHit = false;
        pendingGoal = false;
        previousPlayerLaneIndex = TrafficLightController.NO_LANE_INDEX;
    }

    // -- Player helpers ---------------------------------------------------------

    private void clampPlayerToPlayArea() {
        if (player == null) return;
        TransformComponent t = player.getComponent(TransformComponent.class);
        if (t == null) return;

        float x = Math.max(0f, Math.min(t.getPositionX(),
                CrossyLaneConfig.WORLD_WIDTH - t.getWidth()));
        float y = Math.max(0f, Math.min(t.getPositionY(),
                CrossyLaneConfig.PLAY_AREA_HEIGHT - t.getHeight()));

        t.setPosition(x, y);
    }

    private int getPlayerLaneIndex() {
        if (player == null) return TrafficLightController.NO_LANE_INDEX;
        TransformComponent t = player.getComponent(TransformComponent.class);
        if (t == null) return TrafficLightController.NO_LANE_INDEX;

        LevelDefinition level = getActiveLevel();
        float centerY = t.getPositionY() + (t.getHeight() / 2f);
        float roadBottom = CrossyLaneConfig.ROAD_START_Y;
        float roadTop = getRoadTopY(level);

        if (centerY < roadBottom || centerY >= roadTop) {
            return TrafficLightController.NO_LANE_INDEX;
        }
        return (int) ((centerY - roadBottom) / CrossyLaneConfig.LANE_HEIGHT);
    }

    private void applyTrafficLightScore() {
        if (player == null || trafficLightSystem.isEmpty()) return;
        displayedScore += trafficLightSystem.scoreForLaneEntry(
                previousPlayerLaneIndex, getPlayerLaneIndex());
    }

    // -- Rendering --------------------------------------------------------------

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.20f, 0.60f, 0.20f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        LevelDefinition level = getActiveLevel();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawRoad(level);
        shapeRenderer.end();

        ioManager.getOutput().renderEntities(entityManager.getEntities());

        BitmapFont indicatorFont = ioManager.getFontManager().getFont("default", 11);
        trafficLightSystem.renderIndicators(shapeRenderer, hudBatch, indicatorFont, glyphLayout);

        BitmapFont hudFont = ioManager.getFontManager().getFont("default", 14);
        hudOverlay.render(shapeRenderer, hudBatch, hudFont, glyphLayout,
                displayedScore, displayedLives, currentLevelNumber);
    }

    private void drawRoad(LevelDefinition level) {
        shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 1f);
        shapeRenderer.rect(0f, CrossyLaneConfig.ROAD_START_Y,
                CrossyLaneConfig.WORLD_WIDTH, getRoadHeight(level));
    }

    private float getRoadHeight(LevelDefinition level) {
        return level.getLaneCount() * CrossyLaneConfig.LANE_HEIGHT;
    }

    private float getRoadTopY(LevelDefinition level) {
        return CrossyLaneConfig.ROAD_START_Y + getRoadHeight(level);
    }

    // -- Accessors --------------------------------------------------------------

    public int getDisplayedScore() { return displayedScore; }
    public int getDisplayedLives() { return displayedLives; }
    public int getCurrentLevelNumber() { return currentLevelNumber; }

    @Override
    public void dispose() {
        unsubscribeEvents();
        clearWorldEntities();
        if (shapeRenderer != null) { shapeRenderer.dispose(); shapeRenderer = null; }
        if (hudBatch != null) { hudBatch.dispose(); hudBatch = null; }
        worldInitialized = false;
    }

    @Override
    public boolean updatesWorld() { return true; }
}
