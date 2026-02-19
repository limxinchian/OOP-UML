package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.mygdx.game.scene.SceneManager;
import com.mygdx.game.scene.SceneType;

import com.mygdx.game.scene.scenes.MainMenuScene;
import com.mygdx.game.scene.scenes.InstructionsScene;
import com.mygdx.game.scene.scenes.GameScene;
import com.mygdx.game.scene.scenes.GameOverScene;
import com.mygdx.game.scene.scenes.PauseScene;

public class GameMaster extends ApplicationAdapter {

    // Core managers
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private IOManager ioManager;

    // Scene controller
    private SceneManager sceneManager;

    @Override
    public void create() {

        // Create Managers
        entityManager = new EntityManager();
        movementManager = new MovementManager(entityManager);
        collisionManager = new CollisionManager();
        ioManager = new IOManager(null);

        // Add movement strategy
        movementManager.addMovementStrategy(new BasicMovementStrategy());

        // Scene Manager
        sceneManager = new SceneManager();

        sceneManager.registerScene(new MainMenuScene(sceneManager));
        sceneManager.registerScene(new InstructionsScene(sceneManager));
        sceneManager.registerScene(new PauseScene(sceneManager));

        sceneManager.registerScene(
                new GameScene(
                        sceneManager,
                        entityManager,
                        movementManager,
                        collisionManager,
                        ioManager
                )
        );

        sceneManager.registerScene(new GameOverScene(sceneManager));

        // Start at main menu
        sceneManager.start(SceneType.MAIN_MENU);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // SceneManager controls update + render
        sceneManager.update(deltaTime);
        sceneManager.render();
    }

    @Override
    public void dispose() {
        entityManager.shutdown();
        movementManager.shutdown();
        collisionManager.shutdown();
    }
}

