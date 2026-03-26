package com.mygdx.game.crossylane.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;

/**
 * Improved HUD:
 * - Taller black bar
 * - Proper spacing for 2 rows
 * - Cleaner alignment
 */
public class GameplayHudOverlay {

    // Make the bar taller (this is the main fix)
    private static final float BAR_Y = CrossyLaneConfig.PLAY_AREA_HEIGHT;
    private static final float BAR_H = 70f; // increased from config

    private static final float PAD_X = 20f;

    // Better spacing
    private static final float LINE1_Y = BAR_Y + BAR_H - 30f;
    private static final float LINE2_Y = BAR_Y + 12f;

    public void render(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch,
                       BitmapFont font, GlyphLayout glyphLayout,
                       int score, int lives, int level) {


        // BLACK BACKGROUND BAR

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Shadow
        shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        shapeRenderer.rect(0f, BAR_Y - 4f, CrossyLaneConfig.WORLD_WIDTH, BAR_H + 4f);

        // Main bar
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 0.95f);
        shapeRenderer.rect(0f, BAR_Y, CrossyLaneConfig.WORLD_WIDTH, BAR_H);

        // Top highlight line
        shapeRenderer.setColor(0.25f, 0.25f, 0.25f, 1f);
        shapeRenderer.rect(0f, BAR_Y + BAR_H - 4f, CrossyLaneConfig.WORLD_WIDTH, 4f);

        shapeRenderer.end();

        // TEXT

        spriteBatch.begin();

        font.setColor(1f, 1f, 1f, 1f);

        // Row 1
        font.draw(spriteBatch, "LEVEL: " + level, PAD_X, LINE1_Y);
        font.draw(spriteBatch, "SCORE: " + score, 200f, LINE1_Y);
        font.draw(spriteBatch, "LIVES: " + lives, 400f, LINE1_Y);

        String goal = "GOAL: Reach the safe zone";
        glyphLayout.setText(font, goal);
        font.draw(spriteBatch, goal,
                CrossyLaneConfig.WORLD_WIDTH - glyphLayout.width - PAD_X,
                LINE1_Y);

        // Row 2 
        font.setColor(0.8f, 0.8f, 0.8f, 1f);

        String controls = "WASD / Arrows: Move   |   ESC: Pause";
        font.draw(spriteBatch, controls, PAD_X, LINE2_Y);

        spriteBatch.end();
    }
}