package com.mygdx.game.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class InstructionsScene implements IScene<DemoSceneKey> {

    private final SceneManager<DemoSceneKey> sceneManager;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    public InstructionsScene(SceneManager<DemoSceneKey> sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public DemoSceneKey getKey() {
        return DemoSceneKey.INSTRUCTIONS;
    }

    @Override
    public void onEnter() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.6f);
        layout = new GlyphLayout();
    }

    @Override
    public void onExit() { }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            sceneManager.resetTo(DemoSceneKey.MAIN_MENU);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        String text =
            "Instructions:\n\n" +
            "- UP to jump\n" +
            "- LEFT/RIGHT to move\n" +
            "- ESC to pause\n\n" +
            "Press ESC/BACKSPACE to return.";

        layout.setText(font, text);
        font.draw(batch, layout,
            (Gdx.graphics.getWidth() - layout.width) / 2f,
            Gdx.graphics.getHeight() - 120);

        batch.end();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
