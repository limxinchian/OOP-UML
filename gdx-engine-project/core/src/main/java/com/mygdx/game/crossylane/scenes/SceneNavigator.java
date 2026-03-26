package com.mygdx.game.crossylane.scenes;

import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

/**
 * Centralised navigation helper for all CrossyLane scene transitions.
 *
 * Phase 5 changes:
 * - Added nextLevel() for "continue to next level" flow without resetting score.
 * - Added goToCustomSetup() for sandbox mode entry.
 * - Added startCustomGame() which starts gameplay in custom/sandbox mode.
 */
public class SceneNavigator {

    private final SceneManager<CrossyLaneSceneKey> sceneManager;

    public SceneNavigator(SceneManager<CrossyLaneSceneKey> sceneManager) {
        this.sceneManager = sceneManager;
    }

    /** Start a fresh new game (resets score, lives, level to defaults). */
    public void startGame() {
        GameplayScene gameplayScene = getGameplayScene();
        if (gameplayScene != null) {
            gameplayScene.resetGame();
        }
        sceneManager.resetTo(CrossyLaneSceneKey.GAMEPLAY);
    }

    /**
     * Advance to the next level, keeping score and lives from the current run.
     * Called from the Result screen when the player chose "Next Level".
     */
    public void nextLevel() {
        GameplayScene gameplayScene = getGameplayScene();
        if (gameplayScene != null) {
            gameplayScene.loadNextLevel();
        }
        sceneManager.resetTo(CrossyLaneSceneKey.GAMEPLAY);
    }

    /** Start a custom/sandbox game using the LevelDefinition stored in session. */
    public void startCustomGame() {
        GameplayScene gameplayScene = getGameplayScene();
        if (gameplayScene != null) {
            gameplayScene.resetGame();
        }
        sceneManager.resetTo(CrossyLaneSceneKey.GAMEPLAY);
    }

    public void goToInstructions() {
        sceneManager.changeScene(CrossyLaneSceneKey.INSTRUCTIONS);
    }

    public void goToCustomSetup() {
        sceneManager.changeScene(CrossyLaneSceneKey.CUSTOM_SETUP);
    }

    public void goToMainMenu() {
        sceneManager.resetTo(CrossyLaneSceneKey.MAIN_MENU);
    }

    public void pauseGame() {
        sceneManager.pushScene(CrossyLaneSceneKey.PAUSE);
    }

    public void resumeGame() {
        sceneManager.popScene();
    }

    public void restartGame() {
        GameplayScene gameplayScene = getGameplayScene();
        if (gameplayScene != null) {
            gameplayScene.resetGame();
        }
        sceneManager.resetTo(CrossyLaneSceneKey.GAMEPLAY);
    }

    public void showResult() {
        sceneManager.changeScene(CrossyLaneSceneKey.RESULT);
    }

    private GameplayScene getGameplayScene() {
        IScene<CrossyLaneSceneKey> scene = sceneManager.getScene(CrossyLaneSceneKey.GAMEPLAY);
        if (scene instanceof GameplayScene) {
            return (GameplayScene) scene;
        }
        return null;
    }

    public void goToSettings() {
        sceneManager.changeScene(CrossyLaneSceneKey.SETTINGS);
    }
}
