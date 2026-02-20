package com.mygdx.game.demo.entities;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.math.Rectangle;

public class BirdCollisionComponent extends CollisionComponent {

    private boolean collided = false;

    // Small touch padding so "touching" feels instant
    private static final float HIT_PAD = 1.5f;

    public BirdCollisionComponent(int collisionLayer, boolean trigger) {
        super(collisionLayer, trigger);
    }

    @Override
    public Rectangle getBounds() {
        Rectangle base = super.getBounds();
        if (base == null) return null;

        // Swept AABB: include where the bird will move this frame
        // This prevents visible "late" collisions on frame spikes.
        float dt = Gdx.graphics.getDeltaTime();
        // clamp to avoid massive hitbox if the app stalls / alt-tabs
        if (dt > 0.05f) dt = 0.05f;

        float vx = 0f;
        float vy = 0f;
        if (getOwner() != null) {
            PhysicsComponent p = getOwner().getComponent(PhysicsComponent.class);
            if (p != null) {
                vx = p.getVelocityX();
                vy = p.getVelocityY();
            }
        }

        float x = base.getX();
        float y = base.getY();
        float w = base.getWidth();
        float h = base.getHeight();

        // Expand in movement direction + a small constant pad
        if (vx >= 0f) {
            x -= HIT_PAD;
            w += (vx * dt) + (HIT_PAD * 2f);
        } else {
            x += (vx * dt) - HIT_PAD; // vx is negative
            w += (-vx * dt) + (HIT_PAD * 2f);
        }

        if (vy >= 0f) {
            y -= HIT_PAD;
            h += (vy * dt) + (HIT_PAD * 2f);
        } else {
            y += (vy * dt) - HIT_PAD; // vy is negative
            h += (-vy * dt) + (HIT_PAD * 2f);
        }

        return new Rectangle(x, y, w, h);
    }

    @Override
    public void onCollisionEnter(CollisionComponent other) {
        collided = true;
    }

    public boolean hasCollided() {
        return collided;
    }

    public void reset() {
        collided = false;
    }
}
