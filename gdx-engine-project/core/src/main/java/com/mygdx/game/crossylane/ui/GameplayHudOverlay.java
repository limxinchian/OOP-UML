package com.mygdx.game.crossylane.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;

/**
 * Gameplay HUD renderer that stays inside the configured HUD layer.
 */
public class GameplayHudOverlay {

    private static final float BAR_Y = CrossyLaneConfig.PLAY_AREA_HEIGHT;
    private static final float PAD_X = 16f;
    private static final float EXTRA_BOTTOM_COVER = 8f;

    public void render(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch,
                       BitmapFont font, GlyphLayout glyphLayout,
                       int score, int lives, int level) {
        float barHeight = CrossyLaneConfig.HUD_HEIGHT;
        float barBottomY = BAR_Y - EXTRA_BOTTOM_COVER;
        float visualBarHeight = barHeight + EXTRA_BOTTOM_COVER;
        float line1Y = BAR_Y + barHeight - Math.max(8f, barHeight * 0.22f);
        float line2Y = BAR_Y + Math.max(16f, barHeight * 0.34f);
        float contentWidth = CrossyLaneConfig.WORLD_WIDTH - (2f * PAD_X);
        float sectionWidth = contentWidth / 4f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        shapeRenderer.rect(0f, barBottomY - 1f, CrossyLaneConfig.WORLD_WIDTH, visualBarHeight + 1f);

        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 0.95f);
        shapeRenderer.rect(0f, barBottomY, CrossyLaneConfig.WORLD_WIDTH, visualBarHeight);

        shapeRenderer.setColor(0.25f, 0.25f, 0.25f, 1f);
        shapeRenderer.rect(0f, BAR_Y + barHeight - 2f, CrossyLaneConfig.WORLD_WIDTH, 2f);

        // Bottom separator so the playfield starts cleanly under the HUD.
        shapeRenderer.setColor(0f, 0f, 0f, 0.55f);
        shapeRenderer.rect(0f, BAR_Y - 1f, CrossyLaneConfig.WORLD_WIDTH, 1f);

        shapeRenderer.end();

        spriteBatch.begin();
        font.setColor(1f, 1f, 1f, 1f);

        float levelX = PAD_X;
        float scoreX = PAD_X + sectionWidth;
        float livesX = PAD_X + (2f * sectionWidth);

        font.draw(spriteBatch, "LEVEL: " + level, levelX, line1Y);
        font.draw(spriteBatch, "SCORE: " + score, scoreX, line1Y);
        font.draw(spriteBatch, "LIVES: " + lives, livesX, line1Y);

        String goal = "GOAL: Reach the safe zone";
        glyphLayout.setText(font, goal);
        font.draw(spriteBatch, goal,
                CrossyLaneConfig.WORLD_WIDTH - glyphLayout.width - PAD_X,
                line1Y);

        font.setColor(0.8f, 0.8f, 0.8f, 1f);
        String controls = "WASD / Arrows: Move   |   ESC: Pause";
        glyphLayout.setText(font, controls);
        if (glyphLayout.width > contentWidth) {
            controls = "Move: WASD/Arrows   ESC: Pause";
        }
        font.draw(spriteBatch, controls, PAD_X, line2Y);

        spriteBatch.end();
    }
}