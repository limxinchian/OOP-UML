package com.mygdx.game.crossylane.scenes;

import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class SceneNavigator {

    private final SceneManager<CrossyLaneSceneKey> sceneManager;

    public SceneNavigator(SceneManager<CrossyLaneSceneKey> sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void startGame() {
        GameplayScene gameplayScene = getGameplayScene();
        if (gameplayScene != null) {
            gameplayScene.resetGame();
        }
        sceneManager.resetTo(CrossyLaneSceneKey.GAMEPLAY);
    }

    public void goToInstructions() {
        sceneManager.changeScene(CrossyLaneSceneKey.INSTRUCTIONS);
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
}