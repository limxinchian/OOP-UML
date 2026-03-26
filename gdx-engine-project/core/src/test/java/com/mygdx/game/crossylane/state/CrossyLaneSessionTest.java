package com.mygdx.game.crossylane.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mygdx.game.crossylane.config.LevelDefinition;
import com.mygdx.game.crossylane.config.LevelRegistry;

public class CrossyLaneSessionTest {

    @Test
    public void customModeSwitchesOnWhenCustomLevelIsSet() {
        CrossyLaneSession session = new CrossyLaneSession();
        LevelDefinition customLevel = LevelRegistry.createDefaultLevels().getLevel(2);

        assertFalse(session.isCustomMode());

        session.setCustomLevel(customLevel);

        assertTrue(session.isCustomMode());
        assertNotNull(session.getCustomLevel());
    }

    @Test
    public void resetRestoresDefaultProgressState() {
        CrossyLaneSession session = new CrossyLaneSession();
        LevelDefinition customLevel = LevelRegistry.createDefaultLevels().getLevel(3);

        session.setPlayerWon(true);
        session.setScore(420);
        session.setLives(1);
        session.setLevelNumber(5);
        session.setCustomLevel(customLevel);

        session.reset();

        assertFalse(session.hasPlayerWon());
        assertEquals(0, session.getScore());
        assertEquals(3, session.getLives());
        assertEquals(1, session.getLevelNumber());
        assertFalse(session.isCustomMode());
    }

    @Test
    public void clearCustomLevelReturnsToRegistryMode() {
        CrossyLaneSession session = new CrossyLaneSession();
        session.setCustomLevel(LevelRegistry.createDefaultLevels().getLevel(1));

        assertTrue(session.isCustomMode());

        session.clearCustomLevel();

        assertFalse(session.isCustomMode());
    }
}
