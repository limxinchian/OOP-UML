package com.mygdx.game.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;
import com.mygdx.game.engine.scene.SceneType;

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
    public boolean updatesWorld() {
        return false; // ✅ freeze entity/movement/collision/input managers
    }

    @Override
    public void onEnter() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
        layout = new GlyphLayout();
    }

    @Override
    public void onExit() { }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            sceneManager.changeScene(SceneType.GAME);
        }
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

        layout.setText(font, "GAME PAUSED");
        font.draw(batch, layout, (screenW - layout.width) / 2f, screenH / 2f + 80);

        layout.setText(font, "Press R to Resume");
        font.draw(batch, layout, (screenW - layout.width) / 2f, screenH / 2f);

        layout.setText(font, "Press M for Main Menu");
        font.draw(batch, layout, (screenW - layout.width) / 2f, screenH / 2f - 80);

        batch.end();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
