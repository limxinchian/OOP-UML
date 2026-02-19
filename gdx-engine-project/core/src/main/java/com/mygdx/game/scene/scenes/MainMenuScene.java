package com.mygdx.game.scene.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.scene.IScene;
import com.mygdx.game.scene.SceneManager;
import com.mygdx.game.scene.SceneType;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class MainMenuScene implements IScene {

    private final SceneManager sceneManager;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private Rectangle startBtn;
    private Rectangle instructionsBtn;
    private Rectangle exitBtn;
    private BitmapFont titleFont;

    private GlyphLayout layout;

    public MainMenuScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public SceneType getType() {
        return SceneType.MAIN_MENU;
    }

    @Override
    public void onEnter() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        titleFont = new BitmapFont();

        titleFont.getData().setScale(3f);
        font.getData().setScale(1.5f);

        layout = new GlyphLayout();

        
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float btnW = 180;
        float btnH = 70;
        float spacing = 40;

    float totalWidth = btnW * 3 + spacing * 2;

    float startX = (screenW - totalWidth) / 2;

    float y = screenH / 3;

    startBtn = new Rectangle(startX, y, btnW, btnH);
    instructionsBtn = new Rectangle(startX + btnW + spacing, y, btnW, btnH);
    exitBtn = new Rectangle(startX + (btnW + spacing) * 2, y, btnW, btnH);
    }

    @Override
    public void onExit() {
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            sceneManager.changeScene(SceneType.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            sceneManager.changeScene(SceneType.INSTRUCTIONS);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        // Mouse click detection
        if (Gdx.input.justTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (startBtn.contains(mouseX, mouseY)) {
                sceneManager.changeScene(SceneType.GAME);
            } else if (instructionsBtn.contains(mouseX, mouseY)) {
                sceneManager.changeScene(SceneType.INSTRUCTIONS);
            } else if (exitBtn.contains(mouseX, mouseY)) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void render() {
    // Green background
    Gdx.gl.glClearColor(0f, 1f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // Draw buttons (red rectangles)
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(1f, 0f, 0f, 1f);
    shapeRenderer.rect(startBtn.x, startBtn.y, startBtn.width, startBtn.height);
    shapeRenderer.rect(instructionsBtn.x, instructionsBtn.y, instructionsBtn.width, instructionsBtn.height);
    shapeRenderer.rect(exitBtn.x, exitBtn.y, exitBtn.width, exitBtn.height);
    shapeRenderer.end();

    // Draw text
    batch.begin();

    // Center Title 
    layout.setText(titleFont, "Welcome to our game!");
    float titleX = (Gdx.graphics.getWidth() - layout.width) / 2f;
    float titleY = Gdx.graphics.getHeight() * 0.7f;
    titleFont.draw(batch, layout, titleX, titleY);

    // Center Start
    layout.setText(font, "Start");
    float startTextX = startBtn.x + (startBtn.width - layout.width) / 2f;
    float startTextY = startBtn.y + (startBtn.height + layout.height) / 2f;
    font.draw(batch, layout, startTextX, startTextY);

    // Center Instructions
    layout.setText(font, "Instructions");
    float instrTextX = instructionsBtn.x + (instructionsBtn.width - layout.width) / 2f;
    float instrTextY = instructionsBtn.y + (instructionsBtn.height + layout.height) / 2f;
    font.draw(batch, layout, instrTextX, instrTextY);

    // Center Exit
    layout.setText(font, "Exit");
    float exitTextX = exitBtn.x + (exitBtn.width - layout.width) / 2f;
    float exitTextY = exitBtn.y + (exitBtn.height + layout.height) / 2f;
    font.draw(batch, layout, exitTextX, exitTextY);

    batch.end();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}

