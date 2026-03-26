package com.mygdx.game.crossylane.entities;

import com.badlogic.gdx.Input.Keys;
import com.mygdx.game.crossylane.audio.CrossyLaneAudioController;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.events.GoalReachedEvent;
import com.mygdx.game.crossylane.events.PlayerHitEvent;
import com.mygdx.game.crossylane.movement.PlayerGridMovementStrategy;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.event.EventBus;
import com.mygdx.game.engine.io.InputComponent;
import com.mygdx.game.engine.movement.MovementComponent;
import com.mygdx.game.engine.render.TextureComponent;

public class PlayerEntity extends Entity {

    private boolean walkToggle = false;
    private final EventBus eventBus;
    private final PlayerGridMovementStrategy movementStrategy = new PlayerGridMovementStrategy();

    public PlayerEntity(float x, float y, EventBus eventBus,
                        CrossyLaneAudioController audioController) {
        if (eventBus == null) throw new IllegalArgumentException("eventBus cannot be null");
        if (audioController == null) throw new IllegalArgumentException("audioController cannot be null");

        this.eventBus = eventBus;

        addComponent(new TransformComponent(
                x, y,
                CrossyLaneConfig.PLAYER_WIDTH,
                CrossyLaneConfig.PLAYER_HEIGHT));

        addComponent(new PhysicsComponent(0f, 0f, 1f));
        addComponent(new MovementComponent(movementStrategy));
        addComponent(createCollisionComponent());
        addComponent(new TextureComponent("player_idle.png"));

        InputComponent input = new InputComponent();

        input.bindJustPressed(Keys.UP, (entity, dt) -> moveUp(entity));
        input.bindJustPressed(Keys.DOWN, (entity, dt) -> moveDown(entity));
        input.bindJustPressed(Keys.LEFT, (entity, dt) -> moveLeft(entity));
        input.bindJustPressed(Keys.RIGHT, (entity, dt) -> moveRight(entity));

        input.bindJustPressed(Keys.W, (entity, dt) -> moveUp(entity));
        input.bindJustPressed(Keys.S, (entity, dt) -> moveDown(entity));
        input.bindJustPressed(Keys.A, (entity, dt) -> moveLeft(entity));
        input.bindJustPressed(Keys.D, (entity, dt) -> moveRight(entity));

        addComponent(input);
    }

    private CollisionComponent createCollisionComponent() {
        return new CollisionComponent(
                CrossyLaneConfig.LAYER_PLAYER,
                CrossyLaneConfig.MASK_PLAYER,
                true) {
            @Override
            public void onCollisionEnter(CollisionComponent other) {
                if (other == null) return;

                int otherLayer = other.getCollisionLayer();

                if (otherLayer == CrossyLaneConfig.LAYER_CAR) {
                    eventBus.publish(new PlayerHitEvent(PlayerEntity.this));
                }

                if (otherLayer == CrossyLaneConfig.LAYER_GOAL) {
                    eventBus.publish(new GoalReachedEvent(PlayerEntity.this));
                }
            }
        };
    }

    private void moveUp(Entity entity) {
        requestMove(entity, 0f, CrossyLaneConfig.GRID_STEP);
        TextureComponent tex = entity.getComponent(TextureComponent.class);
        if (tex != null) {
            tex.setTexture("player_back.png");
            tex.setFlipX(false);
        }
    }

    private void moveDown(Entity entity) {
        requestMove(entity, 0f, -CrossyLaneConfig.GRID_STEP);
        TextureComponent tex = entity.getComponent(TextureComponent.class);
        if (tex != null) {
            tex.setTexture("player_idle.png");
            tex.setFlipX(false);
        }
    }

    private void moveLeft(Entity entity) {
        requestMove(entity, -CrossyLaneConfig.GRID_STEP, 0f);
        TextureComponent tex = entity.getComponent(TextureComponent.class);
        if (tex != null) {
            tex.setTexture(nextWalkFrame());
            tex.setFlipX(true);
        }
    }

    private void moveRight(Entity entity) {
        requestMove(entity, CrossyLaneConfig.GRID_STEP, 0f);
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

    private void requestMove(Entity entity, float dx, float dy) {
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
        if (transform == null || physics == null) return;

        movementStrategy.requestMove(transform, physics, dx, dy);
    }

    public void resetPosition() {
        TransformComponent t = getComponent(TransformComponent.class);
        if (t != null) {
            t.setPosition(
                    CrossyLaneConfig.PLAYER_START_X,
                    CrossyLaneConfig.PLAYER_START_Y);
        }

        TextureComponent tex = getComponent(TextureComponent.class);
        if (tex != null) {
            tex.setTexture("player_idle.png");
            tex.setFlipX(false);
        }

        PhysicsComponent physics = getComponent(PhysicsComponent.class);
        movementStrategy.reset(physics);
        walkToggle = false;
    }
}