package com.mygdx.game.crossylane.config;

/**
 * Central configuration constants for the CrossyLane game.
 * Centralising magic numbers here keeps all other classes clean and easy to tune.
 *
 * Phase 4 changes:
 * - Added HUD_HEIGHT / PLAY_AREA_HEIGHT to separate the HUD bar from gameplay.
 *   The top 50 px of the screen is now reserved for HUD; gameplay happens below.
 * - GOAL_ZONE_Y recalculated from PLAY_AREA_HEIGHT instead of WORLD_HEIGHT.
 * - Removed single CONTROLLED_TRAFFIC_LIGHT_LANE_INDEX / TRAFFIC_LIGHT_POSITION
 *   constants — traffic lights are now defined per-level in LevelDefinition.
 */
public class CrossyLaneConfig {

    // -------------------------------------------------------------------------
    // Screen & layout
    // -------------------------------------------------------------------------
    public static final float WORLD_WIDTH     = 800f;
    public static final float WORLD_HEIGHT    = 600f;

    /** Height of the dedicated HUD bar at the top of the screen. */
    public static final float HUD_HEIGHT      = 50f;

    /** Vertical space available for gameplay (below the HUD bar). */
    public static final float PLAY_AREA_HEIGHT = WORLD_HEIGHT - HUD_HEIGHT; // 550

    // -------------------------------------------------------------------------
    // Grid movement
    // -------------------------------------------------------------------------
    public static final float GRID_STEP = 60f;
    public static final float PLAYER_MOVE_SPEED = 360f;

    // -------------------------------------------------------------------------
    // Entity sizes
    // -------------------------------------------------------------------------
    public static final float PLAYER_WIDTH  = 40f;
    public static final float PLAYER_HEIGHT = 40f;

    public static final float CAR_WIDTH  = 80f;
    public static final float CAR_HEIGHT = 40f;

    public static final float COIN_SIZE = 20f;

    public static final float LANE_MARKER_HEIGHT = 6f;
    public static final float TRAFFIC_LIGHT_WIDTH  = 28f;
    public static final float TRAFFIC_LIGHT_HEIGHT = 56f;

    // -------------------------------------------------------------------------
    // Zone heights
    // -------------------------------------------------------------------------
    public static final float GRASS_ZONE_HEIGHT = 60f;
    public static final float GOAL_ZONE_HEIGHT  = 60f;
    public static final float LANE_HEIGHT       = 60f;

    // -------------------------------------------------------------------------
    // Road layout (Y positions, measured from bottom of play area)
    // -------------------------------------------------------------------------
    public static final float BOTTOM_GRASS_Y = 0f;
    public static final float ROAD_START_Y   = GRASS_ZONE_HEIGHT;                       // 60
    public static final float GOAL_ZONE_Y    = PLAY_AREA_HEIGHT - GOAL_ZONE_HEIGHT;     // 490

    /** Number of traffic lanes (maximum the road supports). */
    public static final int LANE_COUNT = 8;
    public static final int VISIBLE_ROAD_LANE_COUNT = 3;

    // -------------------------------------------------------------------------
    // Traffic light per-lane positioning
    // -------------------------------------------------------------------------
    /** X offset from the right edge of the road where the indicator is placed. */
    public static final float TRAFFIC_LIGHT_RIGHT_MARGIN = 12f;

    // -------------------------------------------------------------------------
    // Player spawn
    // -------------------------------------------------------------------------
    public static final float PLAYER_START_X = (WORLD_WIDTH - PLAYER_WIDTH) / 2f;
    public static final float PLAYER_START_Y = BOTTOM_GRASS_Y + 10f;

    // -------------------------------------------------------------------------
    // Collision layers  (bitmask — each must be a unique power of 2)
    // -------------------------------------------------------------------------
    public static final int LAYER_PLAYER = 1;
    public static final int LAYER_CAR    = 2;
    public static final int LAYER_GOAL   = 4;
    public static final int LAYER_COIN   = 8;
    public static final int LAYER_SAFE   = 16;

    // -------------------------------------------------------------------------
    // Collision masks
    // -------------------------------------------------------------------------
    public static final int MASK_PLAYER = LAYER_CAR | LAYER_GOAL | LAYER_COIN | LAYER_SAFE;
    public static final int MASK_CAR    = LAYER_PLAYER;
    public static final int MASK_GOAL   = LAYER_PLAYER;
    public static final int MASK_COIN   = LAYER_PLAYER;
    public static final int MASK_SAFE   = LAYER_PLAYER;

    // -------------------------------------------------------------------------
    // Traffic light defaults (used by TrafficLightDefinition)
    // -------------------------------------------------------------------------
    public static final float TRAFFIC_LIGHT_SWITCH_INTERVAL = 4f;
    public static final int TRAFFIC_LIGHT_RED_SCORE_PENALTY = -50;
    public static final int TRAFFIC_LIGHT_GREEN_SCORE_BONUS = 0;

    private CrossyLaneConfig() {}
}
