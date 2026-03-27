package com.mygdx.game.crossylane.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.entity.Entity;
import com.mygdx.game.engine.entity.TransformComponent;
import com.mygdx.game.engine.render.TextureComponent;

/**
 * The safe goal area at the top of the screen.
 * When the player enters this zone, the level is won.
 *
 * Renders as a black-and-white checkered flag pattern generated at runtime
 * via Pixmap — no external asset file required.
 *
 * Components attached:
 *  - TransformComponent  : position and size
 *  - CollisionComponent  : trigger, LAYER_GOAL, only detects LAYER_PLAYER
 *  - TextureComponent    : procedural checkered flag pattern
 */
public class GoalZoneEntity extends Entity {

    private static final int TILE_SIZE = 10;

    public GoalZoneEntity(float x, float y, float width, float height) {
        addComponent(new TransformComponent(x, y, width, height));

        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_GOAL,
                CrossyLaneConfig.MASK_GOAL,
                true));

        addComponent(new TextureComponent(
                createCheckeredTexture((int) width, (int) height)));
    }

    private static Texture createCheckeredTexture(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
                boolean even = ((px / TILE_SIZE) + (py / TILE_SIZE)) % 2 == 0;
                pixmap.setColor(even ? Color.WHITE : Color.BLACK);
                pixmap.drawPixel(px, py);
            }
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
