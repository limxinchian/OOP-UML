package com.mygdx.game.crossylane.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.render.FontManager;

/**
 * Renders the gameplay HUD in a fixed top bar that never overlaps the play area.
 *
 * Phase 4 changes:
 * - HUD now occupies a dedicated strip at the top of the screen
 *   (y from PLAY_AREA_HEIGHT to WORLD_HEIGHT).
 * - Draws its own dark background bar first, then text on top.
 * - Completely separated from entity rendering — called after entities.
 */
public class GameplayHudOverlay {

    private static final float BAR_Y = CrossyLaneConfig.PLAY_AREA_HEIGHT;
    private static final float BAR_H = CrossyLaneConfig.HUD_HEIGHT;

    private static final float PAD_X = 16f;
    private static final float TEXT_BASELINE = BAR_Y + BAR_H - 14f;
    private static final float LINE2_BASELINE = BAR_Y + 12f;

    /**
     * Renders the full HUD bar: background + two rows of text.
     *
     * @param shapeRenderer used for the background bar
     * @param spriteBatch   used for text drawing (must NOT already be active)
     * @param font          the font to use for HUD labels
     * @param glyphLayout   shared GlyphLayout for text measurement
     * @param score         current player score
     * @param lives         remaining lives
     * @param level         current level number
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch,
                       BitmapFont font, GlyphLayout glyphLayout,
                       int score, int lives, int level) {

        // --- Background bar ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.10f, 0.10f, 0.10f, 0.92f);
        shapeRenderer.rect(0f, BAR_Y, CrossyLaneConfig.WORLD_WIDTH, BAR_H);
        shapeRenderer.end();

        // --- Text ---
        spriteBatch.begin();
        font.setColor(1f, 1f, 1f, 1f);

        // Row 1 (top):  LEVEL   SCORE   LIVES   GOAL
        font.draw(spriteBatch, "LEVEL: " + level, PAD_X, TEXT_BASELINE);
        font.draw(spriteBatch, "SCORE: " + score, 200f, TEXT_BASELINE);
        font.draw(spriteBatch, "LIVES: " + lives, 400f, TEXT_BASELINE);

        String goal = "GOAL: Reach the safe zone";
        glyphLayout.setText(font, goal);
        font.draw(spriteBatch, goal,
                CrossyLaneConfig.WORLD_WIDTH - glyphLayout.width - PAD_X,
                TEXT_BASELINE);

        // Row 2 (bottom): control hints
        font.setColor(0.8f, 0.8f, 0.8f, 1f);
        font.draw(spriteBatch, "WASD / Arrows: Move  |  ESC: Pause",
                PAD_X, LINE2_BASELINE);

        spriteBatch.end();
    }
}
