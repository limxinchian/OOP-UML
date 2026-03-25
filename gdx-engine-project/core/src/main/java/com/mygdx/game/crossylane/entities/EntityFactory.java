package com.mygdx.game.crossylane.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.config.LaneDefinition;
import com.mygdx.game.crossylane.config.LevelDefinition;
import com.mygdx.game.crossylane.entities.additional_entity.CoinEntity;
import com.mygdx.game.crossylane.entities.additional_entity.SafeStopZoneEntity;
import com.mygdx.game.crossylane.entities.additional_entity.TrafficLightEntity;
import com.mygdx.game.crossylane.entities.additional_entity.ZebraCrossingEntity;
import com.mygdx.game.engine.event.EventBus;

/**
 * Factory class for creating all CrossyLane game entities.
 *
 * Design Pattern: Factory Method
 * -----------------------------------------------------------------------
 * Centralises entity construction so that game scenes never need to know
 * which components each entity requires.
 *
 * Phase 4 changes:
 * - Added createLaneTrafficLight(int laneIndex) which positions the traffic
 *   light entity beside its controlled lane on the right edge of the road.
 * - Removed the old createTrafficLight() that used fixed config coordinates.
 */
public class EntityFactory {
    private static final float COIN_SIDE_MARGIN = 60f;

    private EntityFactory() {}

    // =========================================================================
    // Player
    // =========================================================================

    public static PlayerEntity createPlayer(EventBus eventBus) {
        return new PlayerEntity(
                CrossyLaneConfig.PLAYER_START_X,
                CrossyLaneConfig.PLAYER_START_Y,
                eventBus);
    }

    public static PlayerEntity createPlayer(float x, float y, EventBus eventBus) {
        return new PlayerEntity(x, y, eventBus);
    }

    // =========================================================================
    // Vehicles
    // =========================================================================

    public static CarEntity createCar(float x, int laneIndex, float speed, int direction) {
        float y = CrossyLaneConfig.ROAD_START_Y
                + (laneIndex * CrossyLaneConfig.LANE_HEIGHT)
                + (CrossyLaneConfig.LANE_HEIGHT - CrossyLaneConfig.CAR_HEIGHT) / 2f;

        return new CarEntity(x, y,
                CrossyLaneConfig.CAR_WIDTH,
                CrossyLaneConfig.CAR_HEIGHT,
                speed, direction);
    }

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

    public static List<CarEntity> createCarsForLevel(LevelDefinition level) {
        List<CarEntity> cars = new ArrayList<>();
        for (LaneDefinition lane : level.getLanes()) {
            cars.addAll(createLane(
                    lane.getLaneIndex(),
                    lane.getCarCount(),
                    lane.getSpeed(),
                    lane.getDirection()));
        }
        return cars;
    }

    // =========================================================================
    // World zones
    // =========================================================================

    public static GoalZoneEntity createGoalZone() {
        return new GoalZoneEntity(
                0f,
                CrossyLaneConfig.GOAL_ZONE_Y,
                CrossyLaneConfig.WORLD_WIDTH,
                CrossyLaneConfig.GOAL_ZONE_HEIGHT);
    }

    public static GrassZoneEntity createBottomGrass() {
        return new GrassZoneEntity(
                0f, CrossyLaneConfig.BOTTOM_GRASS_Y,
                CrossyLaneConfig.WORLD_WIDTH,
                CrossyLaneConfig.GRASS_ZONE_HEIGHT);
    }

    public static GrassZoneEntity createTopGrass() {
        return new GrassZoneEntity(
                0f, CrossyLaneConfig.GOAL_ZONE_Y,
                CrossyLaneConfig.WORLD_WIDTH,
                CrossyLaneConfig.GOAL_ZONE_HEIGHT);
    }

    // =========================================================================
    // Lane markers
    // =========================================================================

    public static LaneMarkerEntity createLaneMarker(int laneIndex) {
        return new LaneMarkerEntity(laneIndex);
    }

    public static List<LaneMarkerEntity> createLaneMarkers(int laneCount) {
        List<LaneMarkerEntity> markers = new ArrayList<>();
        for (int i = 1; i < laneCount; i++) {
            markers.add(createLaneMarker(i));
        }
        return markers;
    }

    // =========================================================================
    // Traffic lights (per-lane)
    // =========================================================================

    /**
     * Creates a traffic light entity positioned beside the specified lane.
     * The light is placed at the right edge of the road, vertically centred
     * within the lane.
     *
     * @param laneIndex 0-based lane that this light controls
     */
    public static TrafficLightEntity createLaneTrafficLight(int laneIndex) {
        float laneY = CrossyLaneConfig.ROAD_START_Y
                + (laneIndex * CrossyLaneConfig.LANE_HEIGHT);
        float centredY = laneY + (CrossyLaneConfig.LANE_HEIGHT - CrossyLaneConfig.TRAFFIC_LIGHT_HEIGHT) / 2f;
        float x = CrossyLaneConfig.WORLD_WIDTH
                - CrossyLaneConfig.TRAFFIC_LIGHT_WIDTH
                - CrossyLaneConfig.TRAFFIC_LIGHT_RIGHT_MARGIN;

        return new TrafficLightEntity(x, centredY,
                CrossyLaneConfig.TRAFFIC_LIGHT_WIDTH,
                CrossyLaneConfig.TRAFFIC_LIGHT_HEIGHT);
    }

    // =========================================================================
    // Additional / optional entities
    // =========================================================================

    public static CoinEntity createCoin(float x, float y, EventBus eventBus) {
        return new CoinEntity(x, y, eventBus);
    }

    public static ZebraCrossingEntity createZebraCrossing(int laneIndex) {
        float y = CrossyLaneConfig.ROAD_START_Y + (laneIndex * CrossyLaneConfig.LANE_HEIGHT);
        return new ZebraCrossingEntity(0f, y, CrossyLaneConfig.WORLD_WIDTH);
    }

    public static SafeStopZoneEntity createSafeIsland(float x, int laneIndex, float width) {
        float y = CrossyLaneConfig.ROAD_START_Y + (laneIndex * CrossyLaneConfig.LANE_HEIGHT);
        return new SafeStopZoneEntity(x, y, width, CrossyLaneConfig.LANE_HEIGHT);
    }

    public static List<CoinEntity> createCoinsForRoadLanes(int laneCount, int coinsPerLane, EventBus eventBus) {
        List<CoinEntity> coins = new ArrayList<>();
        if (laneCount <= 0 || coinsPerLane <= 0) return coins;

        float usableWidth = CrossyLaneConfig.WORLD_WIDTH - (2f * COIN_SIDE_MARGIN) - CrossyLaneConfig.COIN_SIZE;
        float slotWidth = usableWidth / coinsPerLane;

        for (int laneIndex = 0; laneIndex < laneCount; laneIndex++) {
            float y = CrossyLaneConfig.ROAD_START_Y
                    + (laneIndex * CrossyLaneConfig.LANE_HEIGHT)
                    + (CrossyLaneConfig.LANE_HEIGHT - CrossyLaneConfig.COIN_SIZE) / 2f;

            for (int coinIndex = 0; coinIndex < coinsPerLane; coinIndex++) {
                float slotStartX = COIN_SIDE_MARGIN + (coinIndex * slotWidth);
                float randomOffset = ThreadLocalRandom.current().nextFloat() * slotWidth;
                float x = Math.min(slotStartX + randomOffset,
                        CrossyLaneConfig.WORLD_WIDTH - COIN_SIDE_MARGIN - CrossyLaneConfig.COIN_SIZE);
                coins.add(createCoin(x, y, eventBus));
            }
        }
        return coins;
    }

    public static List<CoinEntity> createCoinsForTopPatch(int coinCount, float minY, float maxY, EventBus eventBus) {
        List<CoinEntity> coins = new ArrayList<>();
        if (coinCount <= 0 || minY >= maxY) return coins;

        float usableWidth = CrossyLaneConfig.WORLD_WIDTH - (2f * COIN_SIDE_MARGIN) - CrossyLaneConfig.COIN_SIZE;
        float usableHeight = maxY - minY - CrossyLaneConfig.COIN_SIZE;

        for (int i = 0; i < coinCount; i++) {
            float x = COIN_SIDE_MARGIN + ThreadLocalRandom.current().nextFloat() * usableWidth;
            float y = minY + ThreadLocalRandom.current().nextFloat() * usableHeight;
            coins.add(createCoin(x, y, eventBus));
        }
        return coins;
    }
}
