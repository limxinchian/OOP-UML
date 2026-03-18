package com.mygdx.game.engine.ecs;

public class PhysicsComponent extends Component {
    private float velocityX;
    private float velocityY;
    private float mass;

    public PhysicsComponent(float velX, float velY, float mass) {
        this.velocityX = velX;
        this.velocityY = velY;
        this.mass = (mass <= 0f) ? 1f : mass;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public float getMass() {
        return mass;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public void setVelocity(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void addVelocity(float deltaX, float deltaY) {
        this.velocityX += deltaX;
        this.velocityY += deltaY;
    }

    public void setMass(float mass) {
        if (mass > 0f) {
            this.mass = mass;
        }
    }

    @Override
    public void update(float deltaTime) {
        // Physics update handled by MovementManager
    }
}
