package com.mygdx.game.crossylane.config;

/**
 * Immutable data describing the vehicle configuration of a single traffic lane.
 * Used by LevelDefinition to describe the full road layout.
 *
 * Scalability note:
 * Adding a new lane variant (e.g. faster, more cars) is just a new
 * LaneDefinition instance — no code changes required anywhere.
 */
public class LaneDefinition {

    private final int laneIndex;
    private final int carCount;
    private final float speed;
    private final int direction;

    /**
     * @param laneIndex 0-based lane index from the bottom of the road
     * @param carCount  number of vehicles in this lane
     * @param speed     vehicle speed in pixels per second
     * @param direction 1 = left-to-right, -1 = right-to-left
     */
    public LaneDefinition(int laneIndex, int carCount, float speed, int direction) {
        if (laneIndex < 0) throw new IllegalArgumentException("laneIndex must be >= 0");
        if (carCount < 0) throw new IllegalArgumentException("carCount must be >= 0");
        if (speed < 0f) throw new IllegalArgumentException("speed must be >= 0");
        if (direction != 1 && direction != -1) throw new IllegalArgumentException("direction must be 1 or -1");

        this.laneIndex = laneIndex;
        this.carCount = carCount;
        this.speed = speed;
        this.direction = direction;
    }

    public int getLaneIndex() { return laneIndex; }
    public int getCarCount() { return carCount; }
    public float getSpeed() { return speed; }
    public int getDirection() { return direction; }
}
