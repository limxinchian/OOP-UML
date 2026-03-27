package com.mygdx.game.engine.math;

/**
 * Shared numeric utility methods for the engine and game code.
 *
 * Refactor note (Part 2):
 * clampFloat() and clamp() were duplicated across six scene classes.
 * Extracting them here removes the duplication and places generic math
 * in the engine's math package where it belongs.
 *
 * Addresses: DRY principle, Single Responsibility Principle.
 */
public final class MathUtil {

    private MathUtil() { }

    /**
     * Clamps a float value to [min, max].
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Clamps an int value to [min, max].
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
