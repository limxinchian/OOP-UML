package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.crossylane.scenes.CrossyLaneSceneKey;
import com.mygdx.game.crossylane.scenes.GameplayScene;
import com.mygdx.game.crossylane.scenes.InstructionScene;
import com.mygdx.game.crossylane.scenes.MainMenuScene;
import com.mygdx.game.crossylane.scenes.ResultScene;
import com.mygdx.game.crossylane.scenes.PauseScene;
import com.mygdx.game.engine.core.EngineCore;
import com.mygdx.game.engine.scene.SceneManager;

public class GameMaster extends ApplicationAdapter {

    private EngineCore<CrossyLaneSceneKey> engine;

    @Override
    public void create() {
        engine = new EngineCore<>();

        // Wire scenes to engine-owned managers
        SceneManager<CrossyLaneSceneKey> sceneManager = engine.getSceneManager();

        sceneManager.registerScene(new MainMenuScene(sceneManager));
        sceneManager.registerScene(new InstructionScene(sceneManager));
        sceneManager.registerScene(new PauseScene(sceneManager));

        sceneManager.registerScene(
            new GameplayScene(
                sceneManager,
                engine.getEntityManager(),
                engine.getMovementManager(),
                engine.getCollisionManager(),
                engine.getIoManager()
            )
        );

        sceneManager.registerScene(new ResultScene(sceneManager));

        // Initialize engine managers FIRST, then start the first scene
        engine.initialize();
        engine.startScene(CrossyLaneSceneKey.GAMEPLAY);
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
