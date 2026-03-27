package com.mygdx.game.crossylane.entity;

import com.mygdx.game.engine.entity.Entity;
import com.mygdx.game.engine.entity.TransformComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * Decorative grass strip.
 * Used for the spawn zone at the bottom and the safe border at the top.
 * No collision — purely visual background layer.
 *
 * Components attached:
 *  - TransformComponent  : position and size
 *  - RenderableComponent : grass green rectangle
 */
public class GrassZoneEntity extends Entity {

    public GrassZoneEntity(float x, float y, float width, float height) {
        addComponent(new TransformComponent(x, y, width, height));
        // Grass green
        addComponent(RenderableComponent.rectangle(0.18f, 0.62f, 0.18f, 1f));
    }
}