package com.mygdx.game.crossylane.ui;

import com.mygdx.game.engine.render.OutputManager;

/**
 * CrossyLane-specific HUD renderer.
 * Keeps gameplay scene focused on scene flow while UI layout stays isolated here.
 */
public class GameplayHudOverlay {
    private static final float HUD_LEFT_X = 24f;
    private static final float HUD_RIGHT_X = 560f;
    private static final float HUD_CENTER_X = 250f;
    private static final float HUD_TOP_Y = 576f;
    private static final float HUD_LINE_GAP = 28f;
    private static final float PROMPT_LEFT_X = 24f;
    private static final float PROMPT_TOP_Y = 48f;
    private static final float PROMPT_LINE_GAP = 24f;

    public void render(OutputManager outputManager, int score, int lives, int level) {
        if (outputManager == null) {
            throw new IllegalArgumentException("outputManager cannot be null");
        }

        outputManager.drawText("MENU: ESC", HUD_LEFT_X, HUD_TOP_Y);
        outputManager.drawText("LEVEL: " + level, HUD_LEFT_X, HUD_TOP_Y - HUD_LINE_GAP);

        outputManager.drawText("SCORE: " + score, HUD_CENTER_X, HUD_TOP_Y);
        outputManager.drawText("LIVES: " + lives, HUD_CENTER_X, HUD_TOP_Y - HUD_LINE_GAP);

        outputManager.drawText("GOAL", HUD_RIGHT_X, HUD_TOP_Y);
        outputManager.drawText("Reach the safe zone to score", HUD_RIGHT_X - 120f, HUD_TOP_Y - HUD_LINE_GAP);

        outputManager.drawText("Arrow keys / WASD to move", PROMPT_LEFT_X, PROMPT_TOP_Y);
        outputManager.drawText("Middle lane light: red = -50, green = +50", PROMPT_LEFT_X, PROMPT_TOP_Y - PROMPT_LINE_GAP);
    }
}
