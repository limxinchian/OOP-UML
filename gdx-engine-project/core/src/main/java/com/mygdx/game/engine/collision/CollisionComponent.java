package com.mygdx.game.engine.collision;

import com.mygdx.game.engine.ecs.Component;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.math.Rectangle;
import com.mygdx.game.engine.math.Vector2;

public class CollisionComponent extends Component {
    private int collisionLayer;
    private boolean trigger;
    private Vector2 boundsOffset;

    public CollisionComponent(int collisionLayer, boolean trigger) {
        this.collisionLayer = collisionLayer;
        this.trigger = trigger;
        this.boundsOffset = new Vector2(0, 0);
    }

    public Rectangle getBounds() {
        if (owner == null) return null;

        TransformComponent transform = owner.getComponent(TransformComponent.class);
        if (transform == null) return null;

        return new Rectangle(
            transform.positionX + boundsOffset.x,
            transform.positionY + boundsOffset.y,
            transform.width,
            transform.height
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

    public boolean canCollideWith(CollisionComponent other) {
        if (other == null) return false;
        if (!this.isEnabled() || !other.isEnabled()) return false;

        // Simple rule (your existing behavior):
        // layer 0 means "doesn't collide".
        return this.collisionLayer != 0 && other.collisionLayer != 0;
    }

    public boolean isTrigger() {
        return trigger;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    public Vector2 getBoundsOffset() {
        return boundsOffset;
    }

    public void setBoundsOffset(Vector2 offset) {
        this.boundsOffset = offset;
    }

    @Override
    public void update(float deltaTime) {
        // Collision update handled by CollisionManager
    }
}
