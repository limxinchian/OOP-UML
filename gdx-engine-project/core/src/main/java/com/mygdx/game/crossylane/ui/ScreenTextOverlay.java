package com.mygdx.game.crossylane.ui;

import com.mygdx.game.engine.render.OutputManager;

/**
 * Shared text layouts for non-gameplay CrossyLane scenes.
 */
public class ScreenTextOverlay {
    public void renderMainMenu(OutputManager outputManager) {
        if (outputManager == null) {
            throw new IllegalArgumentException("outputManager cannot be null");
        }

        outputManager.drawText("CROSSY LANE", 310f, 520f);
        outputManager.drawText("Start Game", 340f, 430f);
        outputManager.drawText("Instructions", 335f, 390f);
        outputManager.drawText("Press Enter to start when scene flow is wired", 220f, 280f);
        outputManager.drawText("Press I to view instructions when available", 225f, 245f);
    }

    public void renderInstructions(OutputManager outputManager) {
        if (outputManager == null) {
            throw new IllegalArgumentException("outputManager cannot be null");
        }

        outputManager.drawText("HOW TO PLAY", 325f, 520f);
        outputManager.drawText("Move with Arrow keys or WASD", 250f, 440f);
        outputManager.drawText("Avoid moving cars in each lane", 250f, 400f);
        outputManager.drawText("Reach the goal zone to score", 250f, 360f);
        outputManager.drawText("Do not stop in unsafe traffic lanes", 250f, 320f);
        outputManager.drawText("Press ESC to pause during gameplay", 250f, 280f);
        outputManager.drawText("Scene navigation can be wired separately", 245f, 200f);
    }
}
