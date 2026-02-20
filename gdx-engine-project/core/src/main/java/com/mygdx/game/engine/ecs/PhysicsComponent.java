package com.mygdx.game.engine.ecs;

public class PhysicsComponent extends Component {
    public float velocityX;
    public float velocityY;
    public float mass;

    public PhysicsComponent(float velX, float velY, float mass) {
        this.velocityX = velX;
        this.velocityY = velY;
        this.mass = mass;
    }

    @Override
    public void update(float deltaTime) {
        // Physics update handled by MovementManager
    }
}
