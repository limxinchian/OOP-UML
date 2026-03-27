package com.mygdx.game.crossylane.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LevelRegistryTest {

    @Test
    public void defaultLevelsUseSingleStartLineTrafficLight() {
        LevelRegistry registry = LevelRegistry.createDefaultLevels();

        assertLevelLight(registry.getLevel(1), 4f, -50, 0);
        assertLevelLight(registry.getLevel(2), 3.5f, -50, 0);
        assertLevelLight(registry.getLevel(3), 2.5f, -75, 0);
        assertLevelLight(registry.getLevel(4), 2f, -100, 0);
        assertLevelLight(registry.getLevel(5), 1.5f, -150, 0);
    }

    private void assertLevelLight(LevelDefinition level, float interval, int redDelta, int greenDelta) {
        assertEquals(1, level.getTrafficLights().size());

        TrafficLightDefinition light = level.getTrafficLights().get(0);
        assertEquals(0, light.getControlledLaneIndex());
        assertEquals(interval, light.getSwitchInterval(), 0.0001f);
        assertEquals(redDelta, light.getRedScoreDelta());
        assertEquals(greenDelta, light.getGreenScoreDelta());
    }
}
