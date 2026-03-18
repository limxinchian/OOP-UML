package com.mygdx.game.crossylane.entities;

import com.badlogic.gdx.Input.Keys;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.io.InputComponent;
import com.mygdx.game.engine.render.TextureComponent;

public class PlayerEntity extends Entity {

    private boolean walkToggle = false;

    public PlayerEntity(float x, float y) {
        addComponent(new TransformComponent(
                x,
                y,
                CrossyLaneConfig.PLAYER_WIDTH,
                CrossyLaneConfig.PLAYER_HEIGHT
        ));

        addComponent(new CollisionComponent(
                CrossyLaneConfig.LAYER_PLAYER,
                CrossyLaneConfig.MASK_PLAYER,
                true
        ));

        addComponent(new TextureComponent("player_idle.png"));

        InputComponent input = new InputComponent();
        input.bindJustPressed(Keys.UP,    (entity, dt) -> moveUp(entity));
        input.bindJustPressed(Keys.DOWN,  (entity, dt) -> moveDown(entity));
        input.bindJustPressed(Keys.LEFT,  (entity, dt) -> moveLeft(entity));
        input.bindJustPressed(Keys.RIGHT, (entity, dt) -> moveRight(entity));
        addComponent(input);
    }

    private void moveUp(Entity entity) {
        move(entity, 0, CrossyLaneConfig.GRID_STEP);
        TextureComponent tex = entity.getComponent(TextureComponent.class);
        if (tex != null) {
            tex.setTexture("player_back.png");
            tex.setFlipX(false);
        }
    }

    private void moveDown(Entity entity) {
        move(entity, 0, -CrossyLaneConfig.GRID_STEP);
        TextureComponent tex = entity.getComponent(TextureComponent.class);
        if (tex != null) {
            tex.setTexture("player_idle.png");
            tex.setFlipX(false);
        }
    }

    private void moveLeft(Entity entity) {
        move(entity, -CrossyLaneConfig.GRID_STEP, 0);
        TextureComponent tex = entity.getComponent(TextureComponent.class);
        if (tex != null) {
            tex.setTexture(nextWalkFrame());
            tex.setFlipX(true);
        }
    }

    private void moveRight(Entity entity) {
        move(entity, CrossyLaneConfig.GRID_STEP, 0);
        TextureComponent tex = entity.getComponent(TextureComponent.class);
        if (tex != null) {
            tex.setTexture(nextWalkFrame());
            tex.setFlipX(false);
        }
    }

    private String nextWalkFrame() {
        walkToggle = !walkToggle;
        return walkToggle ? "player_walk1.png" : "player_walk2.png";
    }

    private static void move(Entity entity, float dx, float dy) {
        TransformComponent t = entity.getComponent(TransformComponent.class);
        if (t == null) return;

        float newX = t.getPositionX() + dx;
        float newY = t.getPositionY() + dy;

        newX = Math.max(0f, Math.min(newX, CrossyLaneConfig.WORLD_WIDTH - t.getWidth()));
        newY = Math.max(0f, Math.min(newY, CrossyLaneConfig.WORLD_HEIGHT - t.getHeight()));

        t.setPosition(newX, newY);
    }

    public void resetPosition() {
        TransformComponent t = getComponent(TransformComponent.class);
        if (t != null) {
            t.setPosition(
                    CrossyLaneConfig.PLAYER_START_X,
                    CrossyLaneConfig.PLAYER_START_Y
            );
        }

        TextureComponent tex = getComponent(TextureComponent.class);
        if (tex != null) {
            tex.setTexture("player_idle.png");
            tex.setFlipX(false);
        }

        walkToggle = false;
    }
}