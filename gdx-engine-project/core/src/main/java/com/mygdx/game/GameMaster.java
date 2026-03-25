package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.crossylane.audio.CrossyLaneAudioController;
import com.mygdx.game.crossylane.config.LevelRegistry;
import com.mygdx.game.crossylane.state.CrossyLaneSession;
import com.mygdx.game.crossylane.scenes.CrossyLaneSceneKey;
import com.mygdx.game.crossylane.scenes.CustomSetupScene;
import com.mygdx.game.crossylane.scenes.GameplayScene;
import com.mygdx.game.crossylane.scenes.InstructionScene;
import com.mygdx.game.crossylane.scenes.MainMenuScene;
import com.mygdx.game.crossylane.scenes.ResultScene;
import com.mygdx.game.crossylane.scenes.PauseScene;
import com.mygdx.game.engine.core.EngineCore;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.scene.SceneManager;

/**
 * Application entry point — bootstraps the engine and registers all game scenes.
 *
 * Phase 6 changes:
 * - Creates CrossyLaneAudioController and subscribes it to the EventBus.
 * - Passes audioController to MainMenuScene, PauseScene, GameplayScene,
 *   and ResultScene so each can trigger the correct background music.
 * - Unsubscribes audioController on dispose.
 */
public class GameMaster extends ApplicationAdapter {

    private EngineCore<CrossyLaneSceneKey> engine;
    private CrossyLaneSession session;
    private CrossyLaneAudioController audioController;

    @Override
    public void create() {
        engine = new EngineCore<>();
        session = new CrossyLaneSession();

        SceneManager<CrossyLaneSceneKey> sceneManager = engine.getSceneManager();
        IOManager ioManager = engine.getIoManager();
        LevelRegistry levelRegistry = LevelRegistry.createDefaultLevels();

        // Create and subscribe the centralised audio controller
        audioController = new CrossyLaneAudioController(
                ioManager.getAudio(), engine.getEventBus());
        audioController.subscribe();

        sceneManager.registerScene(new MainMenuScene(
                sceneManager, ioManager, session, audioController));

        sceneManager.registerScene(new InstructionScene(sceneManager, ioManager));

        sceneManager.registerScene(new PauseScene(
                sceneManager, ioManager, audioController));

        sceneManager.registerScene(new CustomSetupScene(
                sceneManager, ioManager, session));

        sceneManager.registerScene(new GameplayScene(
                session, sceneManager,
                engine.getEntityManager(), ioManager,
                engine.getEventBus(), levelRegistry,
                audioController));

        sceneManager.registerScene(new ResultScene(
                session, sceneManager, ioManager, levelRegistry, audioController));

        engine.initialize();
        engine.startScene(CrossyLaneSceneKey.MAIN_MENU);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        engine.tick(dt);
        engine.render();
    }

    @Override
    public void dispose() {
        if (audioController != null) {
            audioController.unsubscribe();
        }
        engine.dispose();
    }
}
