package com.mygdx.game.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class PauseScene implements IScene<DemoSceneKey> {

    private final SceneManager<DemoSceneKey> sceneManager;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private boolean initialized = false;

    public PauseScene(SceneManager<DemoSceneKey> sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public DemoSceneKey getKey() {
        return DemoSceneKey.PAUSE;
    }

    @Override
    public boolean updatesWorld() {
        return false; // freeze entity/movement/collision/input managers
    }

    @Override
    public void onEnter() {
        // Allocate once to avoid leaking GPU resources on repeated pause/resume.
        if (!initialized) {
            batch = new SpriteBatch();
            font = new BitmapFont();
            font.getData().setScale(2f);
            layout = new GlyphLayout();
            initialized = true;
        }
    }

    @Override
    public void onExit() { }

    @Override
    public void update(float delta) {

        // Resume
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.popScene();
            return;
        }

        // Back to menu (hard reset navigation)
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            sceneManager.resetTo(DemoSceneKey.MAIN_MENU);
        }
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (batch == null || font == null || layout == null) return;

        batch.begin();

        String msg =
            "PAUSED\n\n" +
            "Press ESC to Resume\n" +
            "Press M for Main Menu";

        layout.setText(font, msg);

        font.draw(batch, layout,
            (Gdx.graphics.getWidth() - layout.width) / 2f,
            (Gdx.graphics.getHeight() + layout.height) / 2f);

        batch.end();
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        layout = null;
        initialized = false;
    }
}
