package com.mygdx.game.crossylane.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Shared visual theme helpers for menu-like scenes.
 *
 * Centralizes palette and shape treatment so scenes stay consistent and
 * maintainable while keeping scene logic focused on interaction/state.
 */
public final class MenuUiTheme {

    private MenuUiTheme() {
    }

    public static void drawBackdrop(ShapeRenderer shapeRenderer, float screenW, float screenH) {
        shapeRenderer.setColor(0.08f, 0.12f, 0.09f, 1f);
        shapeRenderer.rect(0, 0, screenW, screenH);

        shapeRenderer.setColor(0.06f, 0.10f, 0.08f, 1f);
        shapeRenderer.rect(0, 0, screenW * 0.18f, screenH);
        shapeRenderer.rect(screenW * 0.82f, 0, screenW * 0.18f, screenH);

        shapeRenderer.setColor(0.10f, 0.17f, 0.11f, 1f);
        shapeRenderer.rect(screenW * 0.20f, 0, screenW * 0.60f, screenH);
    }

    public static void drawPanel(ShapeRenderer shapeRenderer,
                                 float x, float y,
                                 float width, float height) {
        shapeRenderer.setColor(0f, 0f, 0f, 0.30f);
        shapeRenderer.rect(x + 10f, y - 10f, width, height);

        shapeRenderer.setColor(0.10f, 0.18f, 0.10f, 0.92f);
        shapeRenderer.rect(x, y, width, height);

        shapeRenderer.setColor(0.18f, 0.32f, 0.18f, 1f);
        shapeRenderer.rect(x, y + height - 16f, width, 16f);
    }

    public static void drawCard(ShapeRenderer shapeRenderer,
                                float x, float y,
                                float width, float height) {
        shapeRenderer.setColor(0f, 0f, 0f, 0.20f);
        shapeRenderer.rect(x + 4f, y - 4f, width, height);

        shapeRenderer.setColor(0.20f, 0.38f, 0.20f, 1f);
        shapeRenderer.rect(x, y, width, height);

        shapeRenderer.setColor(0.30f, 0.50f, 0.30f, 1f);
        shapeRenderer.rect(x, y + height - 6f, width, 6f);
    }

    public static void drawButton(ShapeRenderer shapeRenderer,
                                  float x, float y,
                                  float width, float height,
                                  boolean selected) {
        shapeRenderer.setColor(0f, 0f, 0f, 0.22f);
        shapeRenderer.rect(x + 4f, y - 4f, width, height);

        if (selected) {
            shapeRenderer.setColor(0.90f, 0.80f, 0.20f, 1f);
        } else {
            shapeRenderer.setColor(0.26f, 0.48f, 0.26f, 1f);
        }
        shapeRenderer.rect(x, y, width, height);

        if (selected) {
            shapeRenderer.setColor(0.98f, 0.90f, 0.40f, 1f);
        } else {
            shapeRenderer.setColor(0.36f, 0.58f, 0.36f, 1f);
        }
        shapeRenderer.rect(x, y + height - 8f, width, 8f);
    }
}