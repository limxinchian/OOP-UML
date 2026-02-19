package com.mygdx.game.scene.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.scene.IScene;
import com.mygdx.game.scene.SceneManager;
import com.mygdx.game.scene.SceneType;

public class GameOverScene implements IScene {

    private final SceneManager sceneManager;

    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont font;
    private GlyphLayout layout;

    public GameOverScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public SceneType getType() {
        return SceneType.GAME_OVER;
    }

    @Override
    public void onEnter() {

        batch = new SpriteBatch();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);

        font = new BitmapFont();
        font.getData().setScale(1.5f);

        layout = new GlyphLayout();
    }

    @Override
    public void onExit() {
    }

    @Override
    public void update(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            sceneManager.changeScene(SceneType.GAME);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.changeScene(SceneType.MAIN_MENU);
        }
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        batch.begin();

        // GAME OVER Title
        layout.setText(titleFont, "GAME OVER");
        float titleX = (screenW - layout.width) / 2f;
        float titleY = screenH * 0.7f;
        titleFont.draw(batch, layout, titleX, titleY);

        // Restart Text
        layout.setText(font, "Press R to Restart");
        float restartX = (screenW - layout.width) / 2f;
        float restartY = screenH * 0.5f;
        font.draw(batch, layout, restartX, restartY);

        // Menu Text
        layout.setText(font, "Press ESC for Main Menu");
        float menuX = (screenW - layout.width) / 2f;
        float menuY = screenH * 0.45f;
        font.draw(batch, layout, menuX, menuY);

        batch.end();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
    }
}
