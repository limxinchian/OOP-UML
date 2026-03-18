package com.mygdx.game.crossylane.entities;

import java.util.concurrent.ThreadLocalRandom;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.render.TextureComponent;

public class CarEntity extends Entity {

    private static final String[] CAR_TEXTURES = {
            "ambulance.png",
            "bus.png",
            "police.png",
            "van_small.png"
    };

    private final float speed;
    private final int direction;

    public CarEntity(float x, float y, float width, float height, float speed, int direction) {
        this.speed = speed;
        this.direction = direction;

        addComponent(new TransformComponent(x, y, width, height));

        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_CAR,
                CrossyLaneConfig.MASK_CAR,
                true
        ));

        addComponent(new PhysicsComponent(speed * direction, 0f, 1f));

        String texturePath = pickRandomTexture();
        TextureComponent textureComponent = new TextureComponent(texturePath);
        textureComponent.setFlipX(direction < 0);
        addComponent(textureComponent);
    }

    private String pickRandomTexture() {
        int index = ThreadLocalRandom.current().nextInt(CAR_TEXTURES.length);
        return CAR_TEXTURES[index];
    }

    public float getSpeed() {
        return speed;
    }

    public int getDirection() {
        return direction;
    }
}