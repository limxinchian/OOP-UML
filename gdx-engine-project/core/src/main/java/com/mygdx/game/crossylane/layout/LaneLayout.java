package com.mygdx.game.crossylane.layout;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;

/**
 * Shared vertical geometry for road bands and the grass separators between
 * them. Spawning, rendering, and lane hit-testing should all use this helper
 * so every system agrees on lane positions.
 */
public final class LaneLayout {
    public static final int NO_LANE_INDEX = -1;

    private final int laneCount;
    private final float separatorHeight;

    private LaneLayout(int laneCount, float separatorHeight) {
        this.laneCount = laneCount;
        this.separatorHeight = separatorHeight;
    }

    public static LaneLayout forLaneCount(int laneCount) {
        if (laneCount <= 0) {
            throw new IllegalArgumentException("laneCount must be > 0");
        }

        float availableSpace = CrossyLaneConfig.GOAL_ZONE_Y - CrossyLaneConfig.ROAD_START_Y;
        float roadHeight = laneCount * CrossyLaneConfig.LANE_HEIGHT;
        int separatorCount = Math.max(0, laneCount - 1);

        float computedSeparatorHeight = 0f;
        if (separatorCount > 0) {
            float remainingSpace = Math.max(0f, availableSpace - roadHeight);
            computedSeparatorHeight = Math.min(
                    CrossyLaneConfig.LANE_HEIGHT,
                    remainingSpace / separatorCount);
        }

        return new LaneLayout(laneCount, computedSeparatorHeight);
    }

    public int getLaneCount() {
        return laneCount;
    }

    public float getSeparatorHeight() {
        return separatorHeight;
    }

    public float getLaneBaseY(int laneIndex) {
        validateLaneIndex(laneIndex);
        return CrossyLaneConfig.ROAD_START_Y
                + (laneIndex * (CrossyLaneConfig.LANE_HEIGHT + separatorHeight));
    }

    public float getRoadTopY() {
        return getLaneBaseY(laneCount - 1) + CrossyLaneConfig.LANE_HEIGHT;
    }

    public float getRoadBlockHeight() {
        return getRoadTopY() - CrossyLaneConfig.ROAD_START_Y;
    }

    public int getLaneIndexForY(float y) {
        for (int laneIndex = 0; laneIndex < laneCount; laneIndex++) {
            float laneBaseY = getLaneBaseY(laneIndex);
            if (y >= laneBaseY && y < laneBaseY + CrossyLaneConfig.LANE_HEIGHT) {
                return laneIndex;
            }
        }
        return NO_LANE_INDEX;
    }

    private void validateLaneIndex(int laneIndex) {
        if (laneIndex < 0 || laneIndex >= laneCount) {
            throw new IllegalArgumentException("laneIndex out of range: " + laneIndex);
        }
    }
}
