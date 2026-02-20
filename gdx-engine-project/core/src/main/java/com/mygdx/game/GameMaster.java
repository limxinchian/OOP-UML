package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.demo.scenes.DemoSceneKey;
import com.mygdx.game.demo.scenes.GameOverScene;
import com.mygdx.game.demo.scenes.GameScene;
import com.mygdx.game.demo.scenes.InstructionsScene;
import com.mygdx.game.demo.scenes.MainMenuScene;
import com.mygdx.game.demo.scenes.PauseScene;
import com.mygdx.game.engine.core.EngineCore;
import com.mygdx.game.engine.scene.SceneManager;

public class GameMaster extends ApplicationAdapter {

    private EngineCore<DemoSceneKey> engine;

    @Override
    public void create() {
        engine = new EngineCore<>();

        // Wire scenes to engine-owned managers
        SceneManager<DemoSceneKey> sceneManager = engine.getSceneManager();

        sceneManager.registerScene(new MainMenuScene(sceneManager));
        sceneManager.registerScene(new InstructionsScene(sceneManager));
        sceneManager.registerScene(new PauseScene(sceneManager));

        sceneManager.registerScene(
            new GameScene(
                sceneManager,
                engine.getEntityManager(),
                engine.getMovementManager(),
                engine.getCollisionManager(),
                engine.getIoManager()
            )
        );

        sceneManager.registerScene(new GameOverScene(sceneManager));

        // Initialize engine managers FIRST, then start the first scene
        engine.initialize();
        engine.startScene(DemoSceneKey.MAIN_MENU);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        engine.tick(dt);
        engine.render();
    }

    @Override
    public void dispose() {
        engine.dispose();
    }
}
