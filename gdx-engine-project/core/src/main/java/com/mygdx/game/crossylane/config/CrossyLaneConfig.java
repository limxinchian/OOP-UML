package com.mygdx.game.crossylane.config;

/**
 * Central configuration constants for the CrossyLane game.
 * Centralising magic numbers here keeps all other classes clean and easy to tune.
 */
public class CrossyLaneConfig {

    // -------------------------------------------------------------------------
    // World dimensions
    // -------------------------------------------------------------------------
    public static final float WORLD_WIDTH  = 800f;
    public static final float WORLD_HEIGHT = 600f;

    // -------------------------------------------------------------------------
    // Grid movement
    // -------------------------------------------------------------------------
    /** Pixels the player moves per key press. */
    public static final float GRID_STEP = 60f;

    // -------------------------------------------------------------------------
    // Entity sizes
    // -------------------------------------------------------------------------
    public static final float PLAYER_WIDTH  = 40f;
    public static final float PLAYER_HEIGHT = 40f;

    public static final float CAR_WIDTH  = 80f;
    public static final float CAR_HEIGHT = 40f;

    public static final float COIN_SIZE = 20f;

    public static final float LANE_MARKER_HEIGHT = 6f;
    public static final float TRAFFIC_LIGHT_WIDTH  = 20f;
    public static final float TRAFFIC_LIGHT_HEIGHT = 40f;

    // -------------------------------------------------------------------------
    // Zone heights
    // -------------------------------------------------------------------------
    public static final float GRASS_ZONE_HEIGHT = 60f;
    public static final float GOAL_ZONE_HEIGHT  = 60f;
    public static final float LANE_HEIGHT       = 60f;

    // -------------------------------------------------------------------------
    // Road layout (Y positions, measured from bottom)
    // -------------------------------------------------------------------------
    public static final float BOTTOM_GRASS_Y = 0f;
    public static final float ROAD_START_Y   = GRASS_ZONE_HEIGHT;               // 60
    public static final float GOAL_ZONE_Y    = WORLD_HEIGHT - GOAL_ZONE_HEIGHT; // 540

    /** Number of traffic lanes in the road. */
    public static final int LANE_COUNT = 8;

    // -------------------------------------------------------------------------
    // Player spawn
    // -------------------------------------------------------------------------
    public static final float PLAYER_START_X = (WORLD_WIDTH - PLAYER_WIDTH) / 2f;
    public static final float PLAYER_START_Y = BOTTOM_GRASS_Y + 10f;

    // -------------------------------------------------------------------------
    // Collision layers  (bitmask — each must be a unique power of 2)
    // -------------------------------------------------------------------------
    public static final int LAYER_PLAYER = 1;   // 00001
    public static final int LAYER_CAR    = 2;   // 00010
    public static final int LAYER_GOAL   = 4;   // 00100
    public static final int LAYER_COIN   = 8;   // 01000
    public static final int LAYER_SAFE   = 16;  // 10000

    // -------------------------------------------------------------------------
    // Collision masks  (which layers does each entity care about?)
    // -------------------------------------------------------------------------
    public static final int MASK_PLAYER = LAYER_CAR | LAYER_GOAL | LAYER_COIN | LAYER_SAFE;
    public static final int MASK_CAR    = LAYER_PLAYER;
    public static final int MASK_GOAL   = LAYER_PLAYER;
    public static final int MASK_COIN   = LAYER_PLAYER;
    public static final int MASK_SAFE   = LAYER_PLAYER;

    // -------------------------------------------------------------------------
    // Traffic light timing defaults
    // -------------------------------------------------------------------------
    public static final float TRAFFIC_LIGHT_RED_DURATION   = 4f;
    public static final float TRAFFIC_LIGHT_GREEN_DURATION = 3f;

    // Prevent instantiation
    private CrossyLaneConfig() {}
}