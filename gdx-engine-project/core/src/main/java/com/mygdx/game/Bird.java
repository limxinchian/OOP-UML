package com.mygdx.game;

public class Bird extends Entity {

    private float radius;

    public Bird(float x, float y, float radius) {
        super();

        this.radius = radius;

        float diameter = radius * 2f;

        // Add ECS components
        this.addComponent(new TransformComponent(x, y, diameter, diameter));
        this.addComponent(new PhysicsComponent(0, 0, 1));
        this.addComponent(new BirdCollisionComponent(1, false));
    }

    public float getRadius() {
        return radius;
    }

    public BirdCollisionComponent getCollision() {
        return (BirdCollisionComponent)
                this.getComponent(BirdCollisionComponent.class);
    }

    public TransformComponent getTransform() {
        return (TransformComponent)
                this.getComponent(TransformComponent.class);
    }

    public PhysicsComponent getPhysics() {
        return (PhysicsComponent)
                this.getComponent(PhysicsComponent.class);
    }
}


