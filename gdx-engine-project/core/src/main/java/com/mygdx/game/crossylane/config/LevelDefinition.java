package com.mygdx.game.crossylane.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable data describing a single game level.
 *
 * Phase 4 changes:
 * - Added a list of TrafficLightDefinition so each level can specify
 *   which lanes have traffic lights and with what timing.
 *   Multiple lights per level are fully supported.
 *
 * Scalability note:
 * Adding a new level is purely data-driven — construct a new LevelDefinition
 * with the desired lanes, coins, and traffic lights, then register it in
 * LevelRegistry.  No scene or entity code needs modification (OCP).
 */
public class LevelDefinition {

    private final int levelNumber;
    private final List<LaneDefinition> lanes;
    private final int coinsPerLane;
    private final int topPatchCoins;
    private final float trafficLightInterval;
    private final List<TrafficLightDefinition> trafficLights;

    /**
     * @param levelNumber          1-based level display number
     * @param lanes                lane configurations for this level
     * @param coinsPerLane         coins to scatter per road lane
     * @param topPatchCoins        coins in the top grass patch
     * @param trafficLightInterval default seconds per traffic light phase
     * @param trafficLights        per-lane traffic light configs (may be empty)
     */
    public LevelDefinition(int levelNumber, List<LaneDefinition> lanes,
                           int coinsPerLane, int topPatchCoins,
                           float trafficLightInterval,
                           List<TrafficLightDefinition> trafficLights) {
        if (levelNumber < 1) throw new IllegalArgumentException("levelNumber must be >= 1");
        if (lanes == null || lanes.isEmpty()) throw new IllegalArgumentException("lanes cannot be null or empty");
        if (trafficLightInterval <= 0f) throw new IllegalArgumentException("trafficLightInterval must be > 0");

        this.levelNumber = levelNumber;
        this.lanes = Collections.unmodifiableList(new ArrayList<>(lanes));
        this.coinsPerLane = Math.max(0, coinsPerLane);
        this.topPatchCoins = Math.max(0, topPatchCoins);
        this.trafficLightInterval = trafficLightInterval;
        this.trafficLights = (trafficLights == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(trafficLights));
    }

    public int getLevelNumber() { return levelNumber; }
    public List<LaneDefinition> getLanes() { return lanes; }
    public int getLaneCount() { return lanes.size(); }
    public int getCoinsPerLane() { return coinsPerLane; }
    public int getTopPatchCoins() { return topPatchCoins; }
    public float getTrafficLightInterval() { return trafficLightInterval; }
    public List<TrafficLightDefinition> getTrafficLights() { return trafficLights; }
}
