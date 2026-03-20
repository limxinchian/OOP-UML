package com.mygdx.game.crossylane.movement;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.movement.MovementStrategy;

public class PlayerGridMovementStrategy implements MovementStrategy {

    private boolean moving = false;
    private float targetX;
    private float targetY;

    public void requestMove(TransformComponent transform, PhysicsComponent physics, float dx, float dy) {
        if (transform == null || physics == null || moving) {
            return;
        }

        float nextX = clamp(
                transform.getPositionX() + dx,
                0f,
                CrossyLaneConfig.WORLD_WIDTH - transform.getWidth());

        float nextY = clamp(
                transform.getPositionY() + dy,
                0f,
                CrossyLaneConfig.WORLD_HEIGHT - transform.getHeight());

        if (nextX == transform.getPositionX() && nextY == transform.getPositionY()) {
            physics.setVelocity(0f, 0f);
            return;
        }

        targetX = nextX;
        targetY = nextY;
        moving = true;

        float vx = 0f;
        float vy = 0f;

        if (targetX > transform.getPositionX()) {
            vx = CrossyLaneConfig.PLAYER_MOVE_SPEED;
        } else if (targetX < transform.getPositionX()) {
            vx = -CrossyLaneConfig.PLAYER_MOVE_SPEED;
        }

        if (targetY > transform.getPositionY()) {
            vy = CrossyLaneConfig.PLAYER_MOVE_SPEED;
        } else if (targetY < transform.getPositionY()) {
            vy = -CrossyLaneConfig.PLAYER_MOVE_SPEED;
        }

        physics.setVelocity(vx, vy);
    }

    @Override
    public void applyMovement(TransformComponent transform, PhysicsComponent physics, float deltaTime) {
        if (transform == null || physics == null) {
            return;
        }

        if (!moving) {
            physics.setVelocity(0f, 0f);
            return;
        }

        transform.translate(
                physics.getVelocityX() * deltaTime,
                physics.getVelocityY() * deltaTime);

        boolean reachedX = false;
        boolean reachedY = false;

        if (physics.getVelocityX() > 0f) {
            reachedX = transform.getPositionX() >= targetX;
        } else if (physics.getVelocityX() < 0f) {
            reachedX = transform.getPositionX() <= targetX;
        } else {
            reachedX = true;
        }

        if (physics.getVelocityY() > 0f) {
            reachedY = transform.getPositionY() >= targetY;
        } else if (physics.getVelocityY() < 0f) {
            reachedY = transform.getPositionY() <= targetY;
        } else {
            reachedY = true;
        }

        if (reachedX && reachedY) {
            transform.setPosition(targetX, targetY);
            physics.setVelocity(0f, 0f);
            moving = false;
        }
    }

    public void reset(PhysicsComponent physics) {
        moving = false;
        if (physics != null) {
            physics.setVelocity(0f, 0f);
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}