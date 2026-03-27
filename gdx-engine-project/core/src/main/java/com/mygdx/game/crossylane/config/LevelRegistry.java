package com.mygdx.game.crossylane.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Central registry of all available level definitions.
 *
 * Phase 5 changes:
 * - Expanded from 3 to 5 levels with clear progressive difficulty:
 *   more cars, faster speeds, and shorter traffic-light windows.
 * - Each level is purely data — no code changes needed to add a 6th.
 */
public class LevelRegistry {

    private final List<LevelDefinition> levels;

    public LevelRegistry(List<LevelDefinition> levels) {
        if (levels == null || levels.isEmpty()) {
            throw new IllegalArgumentException("levels cannot be null or empty");
        }
        this.levels = new ArrayList<>(levels);
    }

    /**
     * Returns the definition for the given 1-based level number.
     * If the requested level exceeds the list, returns the last (hardest) level.
     */
    public LevelDefinition getLevel(int levelNumber) {
        int index = Math.min(levelNumber - 1, levels.size() - 1);
        index = Math.max(0, index);
        return levels.get(index);
    }

    public int getLevelCount() {
        return levels.size();
    }

    /**
     * Returns true if the given level number is the final level in the registry.
     */
    public boolean isFinalLevel(int levelNumber) {
        return levelNumber >= levels.size();
    }

    /**
     * Factory method producing the standard CrossyLane level progression.
     *
     * Difficulty axes per level:
     *   - Vehicle count per lane (2 → 4)
     *   - Vehicle speed (150 → 310 px/s)
     *   - Shared traffic-light switch interval (4s → 1.5s)
     *   - Coins available (generous → scarce)
     */
    public static LevelRegistry createDefaultLevels() {
        return new LevelRegistry(Arrays.asList(

            // Level 1: Gentle introduction
            // 3 lanes, slow traffic, one start-line traffic light, generous coins
            new LevelDefinition(1,
                Arrays.asList(
                    new LaneDefinition(0, 2, 150f, 1),
                    new LaneDefinition(1, 2, 170f, -1),
                    new LaneDefinition(2, 2, 190f, 1)
                ),
                2, 5, 4f,
                Collections.singletonList(
                    new TrafficLightDefinition(0, 4f, -50, 0)
                )),

            // Level 2: Getting faster
            // 3 lanes, +1 car per lane, faster, one start-line traffic light
            new LevelDefinition(2,
                Arrays.asList(
                    new LaneDefinition(0, 3, 180f, 1),
                    new LaneDefinition(1, 2, 210f, -1),
                    new LaneDefinition(2, 3, 230f, 1)
                ),
                2, 4, 3.5f,
                Collections.singletonList(
                    new TrafficLightDefinition(0, 3.5f, -50, 0)
                )),

            // Level 3: Busy road
            // 3 lanes, 3 cars each, faster, stricter start-line traffic light
            new LevelDefinition(3,
                Arrays.asList(
                    new LaneDefinition(0, 3, 210f, 1),
                    new LaneDefinition(1, 3, 250f, -1),
                    new LaneDefinition(2, 3, 260f, 1)
                ),
                1, 3, 2.5f,
                Collections.singletonList(
                    new TrafficLightDefinition(0, 2.5f, -75, 0)
                )),

            // Level 4: Rush hour
            // 3 lanes, 4 cars on outer lanes, faster, tight start-line timing
            new LevelDefinition(4,
                Arrays.asList(
                    new LaneDefinition(0, 4, 240f, 1),
                    new LaneDefinition(1, 3, 280f, -1),
                    new LaneDefinition(2, 4, 260f, 1)
                ),
                1, 2, 2f,
                Collections.singletonList(
                    new TrafficLightDefinition(0, 2f, -100, 0)
                )),

            // Level 5: Mayhem
            // 3 lanes, packed, very fast, one harsh start-line light, scarce coins
            new LevelDefinition(5,
                Arrays.asList(
                    new LaneDefinition(0, 4, 270f, 1),
                    new LaneDefinition(1, 4, 310f, -1),
                    new LaneDefinition(2, 4, 290f, 1)
                ),
                1, 1, 1.5f,
                Collections.singletonList(
                    new TrafficLightDefinition(0, 1.5f, -150, 0)
                ))
        ));
    }
}
