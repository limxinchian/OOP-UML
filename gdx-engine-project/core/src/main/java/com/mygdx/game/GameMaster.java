package com.mygdx.game;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class GameMaster extends ApplicationAdapter{
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;

    private Entity movingEntity;
    private Entity stationaryEntity;
    private boolean collisionDetected = false;

    @Override
    public void create() {
        entityManager = new EntityManager();
        movementManager = new MovementManager(entityManager);
        collisionManager = new CollisionManager();

        // Create moving entity (like before)
        movingEntity = new Entity();
        movingEntity.addComponent(new TransformComponent(0, 0, 50, 50));
        movingEntity.addComponent(new PhysicsComponent(10, 0, 1));
        movingEntity.addComponent(new CollisionComponent(1, false));
        entityManager.addEntity(movingEntity);

        // Create stationary entity at position 40 (will collide when moving entity reaches it)
        stationaryEntity = new Entity();
        stationaryEntity.addComponent(new TransformComponent(40, 0, 50, 50));
        stationaryEntity.addComponent(new CollisionComponent(1, false));
        stationaryEntity.addComponent(new PhysicsComponent(0, 0, 1));
        entityManager.addEntity(stationaryEntity);

        // Add collision components to CollisionManager
        collisionManager.addCollisionComponent(
            (CollisionComponent) movingEntity.getComponent(CollisionComponent.class));
        collisionManager.addCollisionComponent(
            (CollisionComponent) stationaryEntity.getComponent(CollisionComponent.class));

        movementManager.addMovementStrategy(new BasicMovementStrategy());
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        entityManager.update(deltaTime);
        movementManager.update(deltaTime);
        collisionManager.update(deltaTime);

        TransformComponent transform = (TransformComponent) movingEntity.getComponent(TransformComponent.class);

        // Only print when collision is first detected
        if (!collisionDetected && transform.positionX >= 40) {
            System.out.println("COLLISION DETECTED at x: " + transform.positionX);
            collisionDetected = true;
        }
    }

    @Override
    public void dispose() {
        entityManager.shutdown();
        movementManager.shutdown();
        collisionManager.shutdown();
    }
}
