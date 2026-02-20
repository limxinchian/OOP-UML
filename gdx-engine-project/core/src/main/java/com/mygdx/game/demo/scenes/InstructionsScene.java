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

public class InstructionsScene implements IScene {

    private final SceneManager sceneManager;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    public InstructionsScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public SceneType getType() {
        return SceneType.INSTRUCTIONS;
    }

    @Override
    public void onEnter() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();
    }

    @Override
    public void onExit() {
    }

    @Override
    public void update(float delta) {
        // Back to main menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.changeScene(SceneType.MAIN_MENU);
        }
    }

    @Override
    public void render() {
        // Background
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        batch.begin();

        // Title centered
        layout.setText(font, "HOW TO PLAY");
        float titleX = (screenW - layout.width) / 2f;
        float titleY = screenH * 0.75f;
        font.draw(batch, layout, titleX, titleY);

        // Instructions
        float x = screenW * 0.25f;
        float y = screenH * 0.60f;
        float gap = 40f;

        font.draw(batch, "ARROWS - Move", x, y - gap);
        font.draw(batch, "Avoid obstacles!", x, y - gap * 2);
        font.draw(batch, "ESC    - Back to Main Menu", x, y - gap * 3);

        batch.end();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
