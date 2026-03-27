package com.mygdx.game.crossylane.state;

import com.mygdx.game.crossylane.config.LevelDefinition;

/**
 * Shared game session state that persists across scene transitions.
 *
 * Phase 5 changes:
 * - Stores score, lives, and level number so the Result screen can show
 *   them and the "Next Level" flow can resume without resetting.
 * - Holds an optional custom LevelDefinition for sandbox mode.
 *   When non-null, GameplayScene uses this instead of the registry.
 */
public class CrossyLaneSession {

    private boolean playerWon = false;
    private int score = 0;
    private int lives = 3;
    private int levelNumber = 1;

    /** If non-null, GameplayScene uses this instead of the LevelRegistry. */
    private LevelDefinition customLevel = null;

    // -- Win / lose flag --------------------------------------------------------

    public void setPlayerWon(boolean playerWon) { this.playerWon = playerWon; }
    public boolean hasPlayerWon() { return playerWon; }

    // -- Score / lives / level --------------------------------------------------

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }

    public int getLevelNumber() { return levelNumber; }
    public void setLevelNumber(int levelNumber) { this.levelNumber = levelNumber; }

    // -- Custom level -----------------------------------------------------------

    public LevelDefinition getCustomLevel() { return customLevel; }
    public void setCustomLevel(LevelDefinition customLevel) { this.customLevel = customLevel; }
    public boolean isCustomMode() { return customLevel != null; }

    /** Clears the custom level so the next game uses the registry. */
    public void clearCustomLevel() { this.customLevel = null; }

    // -- Convenience reset ------------------------------------------------------

    /** Resets all session state for a fresh new game. */
    public void reset() {
        playerWon = false;
        score = 0;
        lives = 3;
        levelNumber = 1;
        customLevel = null;
    }
}
