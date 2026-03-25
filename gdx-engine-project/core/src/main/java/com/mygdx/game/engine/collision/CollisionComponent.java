package com.mygdx.game.engine.collision;

import com.mygdx.game.engine.ecs.Component;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.math.Rectangle;
import com.mygdx.game.engine.math.Vector2;

public class CollisionComponent extends Component {
    // Bitmasks. Layer 0 means "no collision".
    private int collisionLayer;
    private int collisionMask;
    private boolean trigger;
    private final Vector2 boundsOffset;

    public CollisionComponent(int collisionLayer, boolean trigger) {
        this(collisionLayer, ~0, trigger);
    }

    public CollisionComponent(int collisionLayer, int collisionMask, boolean trigger) {
        this.collisionLayer = collisionLayer;
        this.collisionMask = collisionMask;
        this.trigger = trigger;
        this.boundsOffset = new Vector2(0, 0);
    }

    public Rectangle getBounds() {
        if (owner == null) return null;

        TransformComponent transform = owner.getComponent(TransformComponent.class);
        if (transform == null) return null;

        return new Rectangle(
            transform.getPositionX() + boundsOffset.getX(),
            transform.getPositionY() + boundsOffset.getY(),
            transform.getWidth(),
            transform.getHeight()
        );
    }

    /** Called once when collision starts (enter). */
    public void onCollisionEnter(CollisionComponent other) {
        // Default: no-op
    }

    /** Called once when collision ends (exit). */
    public void onCollisionExit(CollisionComponent other) {
        // Default: no-op
    }

    public void setCollisionLayer(int layer) {
        this.collisionLayer = layer;
    }

    public int getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionMask(int mask) {
        this.collisionMask = mask;
    }

    public int getCollisionMask() {
        return collisionMask;
    }

    public void setCollisionFilter(int layer, int mask) {
        this.collisionLayer = layer;
        this.collisionMask = mask;
    }

    public boolean canCollideWith(CollisionComponent other) {
        if (other == null) return false;
        if (!this.isEnabled() || !other.isEnabled()) return false;

        if (this.collisionLayer == 0 || other.collisionLayer == 0) return false;

        boolean thisAcceptsOther = (this.collisionMask & other.collisionLayer) != 0;
        boolean otherAcceptsThis = (other.collisionMask & this.collisionLayer) != 0;
        return thisAcceptsOther && otherAcceptsThis;
    }

    public boolean isTrigger() {
        return trigger;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    public Vector2 getBoundsOffset() {
        return boundsOffset.copy();
    }

    public void setBoundsOffset(Vector2 offset) {
        if (offset == null) {
            boundsOffset.set(0f, 0f);
        } else {
            boundsOffset.set(offset);
        }
    }

    public void setBoundsOffset(float x, float y) {
        boundsOffset.set(x, y);
    }

    @Override
    public void update(float deltaTime) {
        // Collision update handled by CollisionManager
    }
}
