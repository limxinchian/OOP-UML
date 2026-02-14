package com.mygdx.game;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class GameMaster extends ApplicationAdapter{
    private EntityManager entityManager;
    private MovementManager movementManager;


    private Entity testEntity;

    @Override
    public void create() {
        entityManager = new EntityManager();
        movementManager = new MovementManager(entityManager);

    
        movementManager.addMovementStrategy(new BasicMovementStrategy());

        testEntity = new Entity();
        testEntity.addComponent(new TransformComponent(0, 0, 50, 50));
        testEntity.addComponent(new PhysicsComponent(10, 0, 1));
        entityManager.addEntity(testEntity);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        entityManager.update(deltaTime);
        movementManager.update(deltaTime);


        TransformComponent transform = (TransformComponent) testEntity.getComponent(TransformComponent.class);
        
        System.out.println("x location: " + transform.positionX);
    }

    @Override
    public void dispose() {
        entityManager.shutdown();
        movementManager.shutdown();
    }
}
