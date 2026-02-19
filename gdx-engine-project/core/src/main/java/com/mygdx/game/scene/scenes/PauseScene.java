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

public class PauseScene implements IScene {

    private final SceneManager sceneManager;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    public PauseScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public SceneType getType() {
        return SceneType.PAUSE;
    }

    @Override
    public void onEnter() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f); // bigger text
        layout = new GlyphLayout();
    }

    @Override
    public void onExit() {
        // nothing special
    }

    @Override
    public void update(float delta) {

        // Resume game
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            sceneManager.changeScene(SceneType.GAME);
        }

        // Back to main menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
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

        // Line 1: title
        layout.setText(font, "GAME PAUSED");
        font.draw(batch, layout, (screenW - layout.width) / 2f, screenH / 2f + 80);

        // Line 2
        layout.setText(font, "Press R to Resume");
        font.draw(batch, layout, (screenW - layout.width) / 2f, screenH / 2f);

        // Line 3
        layout.setText(font, "Press M for Main Menu");
        font.draw(batch, layout, (screenW - layout.width) / 2f, screenH / 2f - 80);

        batch.end();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        // GlyphLayout doesn't need dispose
    }
}


