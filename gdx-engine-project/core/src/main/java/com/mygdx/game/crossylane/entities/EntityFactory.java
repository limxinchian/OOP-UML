package com.mygdx.game.crossylane.entities;

import java.util.ArrayList;
import java.util.List;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.entities.additional_entity.CoinEntity;
import com.mygdx.game.crossylane.entities.additional_entity.SafeStopZoneEntity;
import com.mygdx.game.crossylane.entities.additional_entity.TrafficLightEntity;
import com.mygdx.game.crossylane.entities.additional_entity.ZebraCrossingEntity;

/**
 * Factory class for creating all CrossyLane game entities.
 *
 * Design Pattern: Factory Method
 * -----------------------------------------------------------------------
 * Centralises entity construction so that game scenes never need to know
 * which components each entity requires. Adding a new component to an
 * entity type only requires a change here — not in every scene that
 * creates that entity. This also demonstrates clear engine/game separation:
 * the factory only touches crossylane classes, never engine internals directly.
 *
 * Usage example (in GameplayScene.onEnter):
 *   PlayerEntity player  = EntityFactory.createPlayer();
 *   GoalZoneEntity goal  = EntityFactory.createGoalZone();
 *   List<CarEntity> lane = EntityFactory.createLane(0, 3, 150f, 1);
 */
public class EntityFactory {

    // Static utility class — no instances needed
    private EntityFactory() {}

    // =========================================================================
    // Player
    // =========================================================================

    /** Creates the player at the default spawn position. */
    public static PlayerEntity createPlayer() {
        return new PlayerEntity(CrossyLaneConfig.PLAYER_START_X, CrossyLaneConfig.PLAYER_START_Y);
    }

    /** Creates the player at a custom position. */
    public static PlayerEntity createPlayer(float x, float y) {
        return new PlayerEntity(x, y);
    }

    // =========================================================================
    // Vehicles
    // =========================================================================

    /**
     * Creates a single car centred vertically within the given lane.
     *
     * @param x         horizontal spawn position
     * @param laneIndex 0-based lane index from the bottom of the road
     * @param speed     pixels per second
     * @param direction 1 = left→right, -1 = right→left
     */
    public static CarEntity createCar(float x, int laneIndex, float speed, int direction) {
        float y = CrossyLaneConfig.ROAD_START_Y
                + (laneIndex * CrossyLaneConfig.LANE_HEIGHT)
                + (CrossyLaneConfig.LANE_HEIGHT - CrossyLaneConfig.CAR_HEIGHT) / 2f;

        return new CarEntity(x, y,
                CrossyLaneConfig.CAR_WIDTH,
                CrossyLaneConfig.CAR_HEIGHT,
                speed, direction);
    }

    /**
     * Creates a full set of cars evenly spaced across a lane.
     * This is the primary spawn method used by GameplayScene for each road lane.
     *
     * @param laneIndex 0-based lane index
     * @param carCount  number of cars in this lane
     * @param speed     pixels per second
     * @param direction 1 = left→right, -1 = right→left
     * @return list of CarEntity ready to be added to EntityManager
     */
    public static List<CarEntity> createLane(int laneIndex, int carCount, float speed, int direction) {
        List<CarEntity> cars = new ArrayList<>();
        float spacing = CrossyLaneConfig.WORLD_WIDTH / carCount;

        for (int i = 0; i < carCount; i++) {
            float startX = (direction == 1)
                    ? i * spacing
                    : CrossyLaneConfig.WORLD_WIDTH - (i * spacing) - CrossyLaneConfig.CAR_WIDTH;

            cars.add(createCar(startX, laneIndex, speed, direction));
        }

        return cars;
    }

    // =========================================================================
    // World zones
    // =========================================================================

    /** Creates the goal zone spanning the full screen width at the top. */
    public static GoalZoneEntity createGoalZone() {
        return new GoalZoneEntity(
                0f,
                CrossyLaneConfig.GOAL_ZONE_Y,
                CrossyLaneConfig.WORLD_WIDTH,
                CrossyLaneConfig.GOAL_ZONE_HEIGHT);
    }

    /** Creates a custom-sized goal zone. */
    public static GoalZoneEntity createGoalZone(float x, float y, float width, float height) {
        return new GoalZoneEntity(x, y, width, height);
    }

    /** Creates the bottom grass spawn zone. */
    public static GrassZoneEntity createBottomGrass() {
        return new GrassZoneEntity(
                0f, CrossyLaneConfig.BOTTOM_GRASS_Y,
                CrossyLaneConfig.WORLD_WIDTH,
                CrossyLaneConfig.GRASS_ZONE_HEIGHT);
    }

    /** Creates the top grass zone behind the goal. */
    public static GrassZoneEntity createTopGrass() {
        return new GrassZoneEntity(
                0f, CrossyLaneConfig.GOAL_ZONE_Y,
                CrossyLaneConfig.WORLD_WIDTH,
                CrossyLaneConfig.GOAL_ZONE_HEIGHT);
    }

    // =========================================================================
    // Lane markers
    // =========================================================================

    /** Creates a single lane divider line at the boundary of laneIndex. */
    public static LaneMarkerEntity createLaneMarker(int laneIndex) {
        return new LaneMarkerEntity(laneIndex);
    }

    /**
     * Creates all lane divider lines for the full road.
     * Call once during scene setup to draw the road grid.
     */
    public static List<LaneMarkerEntity> createAllLaneMarkers() {
        List<LaneMarkerEntity> markers = new ArrayList<>();
        for (int i = 1; i < CrossyLaneConfig.LANE_COUNT; i++) {
            markers.add(createLaneMarker(i));
        }
        return markers;
    }

    // =========================================================================
    // Additional / optional entities
    // =========================================================================

    /** Creates a collectible coin at a fixed position. */
    public static CoinEntity createCoin(float x, float y) {
        return new CoinEntity(x, y);
    }

    /**
     * Creates a zebra crossing spanning the full road width at a given lane.
     *
     * @param laneIndex 0-based lane index
     */
    public static ZebraCrossingEntity createZebraCrossing(int laneIndex) {
        float y = CrossyLaneConfig.ROAD_START_Y + (laneIndex * CrossyLaneConfig.LANE_HEIGHT);
        return new ZebraCrossingEntity(0f, y, CrossyLaneConfig.WORLD_WIDTH);
    }

    /**
     * Creates a safe waiting island in the middle of the road.
     *
     * @param x         horizontal position of the island
     * @param laneIndex 0-based lane where the island sits
     * @param width     island width in pixels
     */
    public static SafeStopZoneEntity createSafeIsland(float x, int laneIndex, float width) {
        float y = CrossyLaneConfig.ROAD_START_Y + (laneIndex * CrossyLaneConfig.LANE_HEIGHT);
        return new SafeStopZoneEntity(x, y, width, CrossyLaneConfig.LANE_HEIGHT);
    }

    /**
     * Creates a traffic light at the left edge of a lane.
     *
     * @param laneIndex    0-based lane index
     * @param redDuration  seconds the light stays red
     * @param greenDuration seconds the light stays green
     */
    public static TrafficLightEntity createTrafficLight(int laneIndex,
                                                        float redDuration,
                                                        float greenDuration) {
        float x = 5f;
        float y = CrossyLaneConfig.ROAD_START_Y
                + (laneIndex * CrossyLaneConfig.LANE_HEIGHT)
                + (CrossyLaneConfig.LANE_HEIGHT - CrossyLaneConfig.TRAFFIC_LIGHT_HEIGHT) / 2f;

        return new TrafficLightEntity(x, y, redDuration, greenDuration);
    }

    /**
     * Creates a traffic light using default timing from config.
     */
    public static TrafficLightEntity createTrafficLight(int laneIndex) {
        return createTrafficLight(laneIndex,
                CrossyLaneConfig.TRAFFIC_LIGHT_RED_DURATION,
                CrossyLaneConfig.TRAFFIC_LIGHT_GREEN_DURATION);
    }
}