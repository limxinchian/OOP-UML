package com.mygdx.game.crossylane.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.entities.additional_entity.CoinEntity;
import com.mygdx.game.crossylane.entities.additional_entity.SafeStopZoneEntity;
import com.mygdx.game.crossylane.entities.additional_entity.TrafficLightEntity;
import com.mygdx.game.crossylane.entities.additional_entity.ZebraCrossingEntity;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.math.Vector2;

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
    private static final float COIN_SIDE_MARGIN = 60f;

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
     * Creates a coin at a random position that doesn't overlap with existing entities
     * @param existingEntities List of entities to check for overlaps
     * @param minY Minimum Y position
     * @param maxY Maximum Y position
     * @param minX Minimum X position
     * @param maxX Maximum X position
     * @return A coin entity at a non-overlapping position, or null if no valid position found
     */
    public static CoinEntity createCoinWithOverlapCheck(List<Entity> existingEntities, float minY, float maxY, float minX, float maxX) {
        final int MAX_RETRIES = 50;
        final float COIN_SIZE = CrossyLaneConfig.COIN_SIZE;
        final float OVERLAP_PADDING = 8f; // Additional padding to prevent coins from being too close
        
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            // Generate random position
            float randomX = minX + (float)(Math.random() * (maxX - minX));
            float randomY = minY + (float)(Math.random() * (maxY - minY));
            
            // Create a temporary coin to check for overlaps
            CoinEntity tempCoin = new CoinEntity(randomX, randomY);
            
            // Check for overlaps with existing entities using collision bounds
            boolean overlaps = false;
            for (Entity entity : existingEntities) {
                if (entity instanceof CoinEntity) {
                    // Get collision components for both coins
                    com.mygdx.game.engine.collision.CollisionComponent tempCollision = 
                        tempCoin.getComponent(com.mygdx.game.engine.collision.CollisionComponent.class);
                    com.mygdx.game.engine.collision.CollisionComponent existingCollision = 
                        entity.getComponent(com.mygdx.game.engine.collision.CollisionComponent.class);
                    
                    if (tempCollision != null && existingCollision != null) {
                        com.mygdx.game.engine.math.Rectangle tempBounds = tempCollision.getBounds();
                        com.mygdx.game.engine.math.Rectangle existingBounds = existingCollision.getBounds();
                        
                        if (tempBounds != null && existingBounds != null) {
                            // Expand bounds by padding to create separation
                            float expandedWidth = tempBounds.getWidth() + OVERLAP_PADDING;
                            float expandedHeight = tempBounds.getHeight() + OVERLAP_PADDING;
                            float expandedX = tempBounds.getX() - (OVERLAP_PADDING / 2);
                            float expandedY = tempBounds.getY() - (OVERLAP_PADDING / 2);
                            
                            com.mygdx.game.engine.math.Rectangle expandedBounds = 
                                new com.mygdx.game.engine.math.Rectangle(
                                    expandedX, expandedY, expandedWidth, expandedHeight);
                            
                            if (expandedBounds.overlaps(existingBounds)) {
                                overlaps = true;
                                break;
                            }
                        }
                    }
                }
            }
            
            // If no overlaps found, return the coin
            if (!overlaps) {
                return tempCoin;
            }
        }
        
        // If we couldn't find a valid position after max retries, return null
        return null;
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

    /** Generates random coin spawn coordinates for a visible road. */
    public static List<Vector2> createCoinSpawnPositionsInRoad(float roadY, float roadHeight, int coinCount) {
        List<Vector2> positions = new ArrayList<>();
        float coinY = roadY + (roadHeight - CrossyLaneConfig.COIN_SIZE) / 2f;
        float maxCoinX = CrossyLaneConfig.WORLD_WIDTH - COIN_SIDE_MARGIN - CrossyLaneConfig.COIN_SIZE;

        for (int i = 0; i < coinCount; i++) {
            float coinX = ThreadLocalRandom.current().nextFloat(COIN_SIDE_MARGIN, maxCoinX);
            positions.add(new Vector2(coinX, coinY));
        }

        return positions;
    }

    /**
     * Generates random coin spawn coordinates for a fixed set of visible road lanes.
     */
    public static List<Vector2> createCoinSpawnPositionsForRoads(float[] roadYs,
                                                                 float roadHeight,
                                                                 int coinsPerLane) {
        List<Vector2> allPositions = new ArrayList<>();

        for (float roadY : roadYs) {
            allPositions.addAll(createCoinSpawnPositionsInRoad(roadY, roadHeight, coinsPerLane));
        }

        return allPositions;
    }
}
