package com.mygdx.game.crossylane.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.entities.CarEntity;
import com.mygdx.game.crossylane.entities.EntityFactory;
import com.mygdx.game.crossylane.entities.GoalZoneEntity;
import com.mygdx.game.crossylane.entities.GrassZoneEntity;
import com.mygdx.game.crossylane.entities.LaneMarkerEntity;
import com.mygdx.game.crossylane.entities.PlayerEntity;
import com.mygdx.game.engine.managers.CollisionManager;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.managers.MovementManager;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class GameplayScene implements IScene<CrossyLaneSceneKey> {
    private final SceneManager<CrossyLaneSceneKey> sceneManager;
    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;

    private PlayerEntity player;
    private GoalZoneEntity goalZone;
    private GrassZoneEntity bottomGrass;
    private GrassZoneEntity topGrass;
    private final List<CarEntity> cars = new ArrayList<>();
    private final List<LaneMarkerEntity> laneMarkers = new ArrayList<>();

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
        // --- Background zones ---
        bottomGrass = EntityFactory.createBottomGrass();
        topGrass    = EntityFactory.createTopGrass();
        entityManager.addEntity(bottomGrass);
        entityManager.addEntity(topGrass);

        // --- Goal zone ---
        goalZone = EntityFactory.createGoalZone();
        entityManager.addEntity(goalZone);

        // --- Lane markers ---
        laneMarkers.clear();
        for (LaneMarkerEntity marker : EntityFactory.createAllLaneMarkers()) {
            laneMarkers.add(marker);
            entityManager.addEntity(marker);
        }

        // --- Cars: 3 lanes, increasing difficulty ---
        // Lane 0: 2 cars going right, slow
        for (CarEntity car : EntityFactory.createLane(0, 2, 120f, 1)) {
            cars.add(car);
            entityManager.addEntity(car);
        }
        // Lane 1: 3 cars going left, medium
        for (CarEntity car : EntityFactory.createLane(1, 3, 160f, -1)) {
            cars.add(car);
            entityManager.addEntity(car);
        }
        // Lane 2: 2 cars going right, fast
        for (CarEntity car : EntityFactory.createLane(2, 2, 220f, 1)) {
            cars.add(car);
            entityManager.addEntity(car);
        }

        // --- Player (spawned last so it renders on top) ---
        player = EntityFactory.createPlayer();
        entityManager.addEntity(player);
    }

    @Override
    public void onExit() {
        entityManager.removeEntity(player);
        entityManager.removeEntity(goalZone);
        entityManager.removeEntity(bottomGrass);
        entityManager.removeEntity(topGrass);
        for (CarEntity car : cars)           entityManager.removeEntity(car);
        for (LaneMarkerEntity m : laneMarkers) entityManager.removeEntity(m);
        cars.clear();
        laneMarkers.clear();
    }

    @Override
    public void update(float delta) {
        wrapCars();
    }

    /** Wraps cars that go off-screen back to the other side */
    private void wrapCars() {
        for (CarEntity car : cars) {
            TransformComponent t = car.getComponent(TransformComponent.class);
            if (t == null) continue;

            if (car.getDirection() == 1 && t.getPositionX() > CrossyLaneConfig.WORLD_WIDTH) {
                t.setPositionX(-t.getWidth());
            } else if (car.getDirection() == -1 && t.getRight() < 0) {
                t.setPositionX(CrossyLaneConfig.WORLD_WIDTH);
            }
        }
    }

    @Override
    public void afterWorldUpdate(float delta) { }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f); // grey = road colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ioManager.getOutput().beginFrame(0.4f, 0.4f, 0.4f, 1f);
        ioManager.getOutput().renderEntities(entityManager.getEntities());
        ioManager.getOutput().endFrame();
    }

    @Override
    public void dispose() { }

    @Override
    public boolean updatesWorld() {
        return true;
    }
}
