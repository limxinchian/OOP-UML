package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.demo.scenes.GameOverScene;
import com.mygdx.game.demo.scenes.GameScene;
import com.mygdx.game.demo.scenes.InstructionsScene;
import com.mygdx.game.demo.scenes.MainMenuScene;
import com.mygdx.game.demo.scenes.PauseScene;
import com.mygdx.game.engine.managers.CollisionManager;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.managers.MovementManager;
import com.mygdx.game.engine.movement.BasicMovementStrategy;
import com.mygdx.game.engine.scene.SceneManager;
import com.mygdx.game.engine.scene.SceneType;

public class GameMaster extends ApplicationAdapter {

    // Core managers (engine-owned update loop)
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private IOManager ioManager;

    // Scene controller
    private SceneManager sceneManager;

    @Override
    public void create() {
        entityManager = new EntityManager();
        movementManager = new MovementManager(entityManager);
        collisionManager = new CollisionManager(entityManager);
        ioManager = new IOManager(entityManager);

        // Fallback global movement strategy (entities can override with MovementComponent)
        movementManager.addMovementStrategy(new BasicMovementStrategy());

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
        sceneManager.start(SceneType.MAIN_MENU);

        ioManager.initialize();
        movementManager.initialize();
        collisionManager.initialize();
        entityManager.initialize();
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        // 1) Scene pre-world update (spawning, gravity, scene switching inputs)
        sceneManager.update(dt);

        // 2) World managers update (only if current scene allows it)
        if (sceneManager.getCurrentScene() != null && sceneManager.getCurrentScene().updatesWorld()) {
            // Flush spawns from scene update so managers can see new entities immediately
            entityManager.update(0f);

            ioManager.update(dt);
            movementManager.update(dt);
            collisionManager.update(dt);
        }

        // 3) Scene post-world update (collision/out-of-bounds checks etc.)
        sceneManager.afterWorldUpdate(dt);

        // 4) Render current scene
        sceneManager.render();
    }

    @Override
    public void dispose() {
        sceneManager.dispose();

        collisionManager.shutdown();
        movementManager.shutdown();
        entityManager.shutdown();
        ioManager.shutdown();
    }
}
