package com.mygdx.game.crossylane.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.crossylane.entities.CarEntity;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.crossylane.entities.CarEntity;
import com.mygdx.game.engine.managers.CollisionManager;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.managers.MovementManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class GameplayScene implements IScene<CrossyLaneSceneKey> {
    private final SceneManager<CrossyLaneSceneKey> sceneManager;
    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;

    private final List<CarEntity> cars = new ArrayList<>();

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;

    public GameplayScene(
            SceneManager<CrossyLaneSceneKey> sceneManager,
            EntityManager entityManager,
            MovementManager movementManager,
            CollisionManager collisionManager,
            IOManager ioManager) {
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.collisionManager = collisionManager;
        this.ioManager = ioManager;
    }

    @Override
    public CrossyLaneSceneKey getKey() {
        return CrossyLaneSceneKey.GAMEPLAY;
    }

    @Override
    public void onEnter() {
        cars.clear();

        CarEntity car1 = new CarEntity(0f, 260f, 80f, 40f, 150f, 1);
        CarEntity car2 = new CarEntity(700f, 360f, 80f, 40f, 180f, -1);

        cars.add(car1);
        cars.add(car2);

        entityManager.addEntity(car1);
        entityManager.addEntity(car2);
    }

    @Override
    public void onExit() {
        for (CarEntity car : cars) {
            entityManager.removeEntity(car);
        }
        cars.clear();
    }

    @Override
    public void update(float delta) {
        updateCars(delta);
    }

    private void updateCars(float delta) {
        for (CarEntity car : cars) {
            TransformComponent transform = car.getComponent(TransformComponent.class);
            if (transform == null)
                continue;

            float newX = transform.getPositionX() + car.getSpeed() * car.getDirection() * delta;
            transform.setPositionX(newX);

            if (car.getDirection() == 1 && transform.getPositionX() > WORLD_WIDTH) {
                transform.setPositionX(-transform.getWidth());
            }

            if (car.getDirection() == -1 && transform.getPositionX() + transform.getWidth() < 0) {
                transform.setPositionX(WORLD_WIDTH);
            }
        }
    }

    @Override
    public void afterWorldUpdate(float delta) {
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.6f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ioManager.getOutput().beginFrame(0.2f, 0.6f, 0.2f, 1f);
        ioManager.getOutput().renderEntities(entityManager.getEntities());
        ioManager.getOutput().endFrame();
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean updatesWorld() {
        return true;
    }
}