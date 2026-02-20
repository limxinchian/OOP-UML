package com.mygdx.game.demo.entities;

import com.mygdx.game.engine.collision.CollisionComponent;

public class BirdCollisionComponent extends CollisionComponent {

    private boolean collided = false;

    public BirdCollisionComponent(int collisionLayer, boolean trigger) {
        super(collisionLayer, trigger);
    }

    @Override
    public void onCollisionEnter(CollisionComponent other) {
        // When CollisionManager detects overlap,
        // this method will be called automatically
        collided = true;
    }

    public boolean hasCollided() {
        return collided;
    }

    public void reset() {
        collided = false;
    }
}

