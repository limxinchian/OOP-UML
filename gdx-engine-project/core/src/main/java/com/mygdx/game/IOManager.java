package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class IOManager implements IManager {

    private Entity player;

    private float jumpVelocity = 350f;
    private float moveSpeed = 200f;

    public IOManager(Entity player) {
        this.player = player;
    }

    @Override
    public void initialize() { }

    
    public void setPlayer(Entity player) {
        this.player = player;
    }

    @Override
    public void update(float deltaTime) {

        if (player == null) return;

        PhysicsComponent physics =
            (PhysicsComponent) player.getComponent(PhysicsComponent.class);

        if (physics == null) return;

        // Reset horizontal velocity each frame
        physics.velocityX = 0;

        // Up arrow → jump
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            physics.velocityY = jumpVelocity;
        }

        // Left arrow → move left
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            physics.velocityX = -moveSpeed;
        }

        // Right arrow → move right
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            physics.velocityX = moveSpeed;
        }
    }

    @Override
    public void shutdown() {
        player = null;
    }
}
