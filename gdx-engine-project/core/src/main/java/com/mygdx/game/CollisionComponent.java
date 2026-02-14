package com.mygdx.game;

public class CollisionComponent extends Component {
    private int collisionLayer;
    private boolean trigger;
    private Vector2 boundsOffset;
    private TransformComponent transform;

    public CollisionComponent(int collisionLayer, boolean trigger) {
        this.collisionLayer = collisionLayer;
        this.trigger = trigger;
        this.boundsOffset = new Vector2(0, 0);
    }

    public Rectangle getBounds() {
        if (owner == null) return null;
        
        Component comp = owner.getComponent(TransformComponent.class);
        if (comp == null || !(comp instanceof TransformComponent)) return null;
        
        transform = (TransformComponent) comp;
        return new Rectangle(
            transform.positionX + boundsOffset.x,
            transform.positionY + boundsOffset.y,
            transform.width,
            transform.height
        );
    }

    public void onCollisionEnter(CollisionComponent other) {
        // Added a collision event
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
        
        // Added collision layer to check    
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
