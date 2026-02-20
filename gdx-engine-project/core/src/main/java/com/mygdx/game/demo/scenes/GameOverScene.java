package com.mygdx.game.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class GameOverScene implements IScene<DemoSceneKey> {

    private final SceneManager<DemoSceneKey> sceneManager;

    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont font;
    private GlyphLayout layout;

    private boolean initialized = false;

    public GameOverScene(SceneManager<DemoSceneKey> sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public DemoSceneKey getKey() {
        return DemoSceneKey.GAME_OVER;
    }

    @Override
    public void onEnter() {
        // IMPORTANT: allocate once (prevents "delay"/hitch during scene switch)
        if (!initialized) {
            batch = new SpriteBatch();
            layout = new GlyphLayout();

            titleFont = new BitmapFont();
            titleFont.getData().setScale(2.6f);
            titleFont.setUseIntegerPositions(true);
            titleFont.getRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

            font = new BitmapFont();
            font.getData().setScale(1.5f);
            font.setUseIntegerPositions(true);
            font.getRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

            initialized = true;
        }
    }

    @Override
    public void onExit() { }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            sceneManager.resetTo(DemoSceneKey.GAME);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.resetTo(DemoSceneKey.MAIN_MENU);
        }
    }

    @Override
    public void render() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        layout.setText(titleFont, "GAME OVER");
        float titleX = Math.round((w - layout.width) / 2f);
        float titleY = Math.round(h - 120f);
        titleFont.draw(batch, layout, titleX, titleY);

        String msg = "Press R to Restart\nPress ESC for Menu";
        layout.setText(font, msg);
        float msgX = Math.round((w - layout.width) / 2f);
        float msgY = Math.round(h / 2f);
        font.draw(batch, layout, msgX, msgY);

        batch.end();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (font != null) font.dispose();
    }
}
