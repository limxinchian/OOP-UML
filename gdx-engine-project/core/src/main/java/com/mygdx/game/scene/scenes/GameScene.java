package com.mygdx.game.scene.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Bird;
import com.mygdx.game.CollisionComponent;
import com.mygdx.game.CollisionManager;
import com.mygdx.game.Entity;
import com.mygdx.game.EntityManager;
import com.mygdx.game.IOManager;
import com.mygdx.game.MovementManager;
import com.mygdx.game.PhysicsComponent;
import com.mygdx.game.TransformComponent;
import com.mygdx.game.scene.IScene;
import com.mygdx.game.scene.SceneManager;
import com.mygdx.game.scene.SceneType;

public class GameScene implements IScene {

    private final SceneManager sceneManager;
    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;

    private ShapeRenderer shapeRenderer;

    private Bird bird;
    private Entity topPipe;
    private Entity bottomPipe;

    // game values (tune later)
    private float gravity = 900f;
    private float pipeVelX = -220f;
    private float gapHeight = 180f;
    private float pipeWidth = 80f;

    // state control
    private boolean initialized = false;

    public GameScene(SceneManager sceneManager,
                     EntityManager entityManager,
                     MovementManager movementManager,
                     CollisionManager collisionManager,
                     IOManager ioManager) {
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.collisionManager = collisionManager;
        this.ioManager = ioManager;
    }

    @Override
    public SceneType getType() {
        return SceneType.GAME;
    }

    @Override
    public void onEnter() {
        if (shapeRenderer == null) shapeRenderer = new ShapeRenderer();

        // ✅ If we are coming back from PAUSE, do NOT reset the game
        if (initialized && sceneManager.getPreviousSceneType() == SceneType.PAUSE) {
            return;
        }

        // Otherwise, new game run (from MAIN_MENU / GAME_OVER etc.)
        initNewRun();
        initialized = true;
    }

    private void initNewRun() {
        // Reset engine state for a new run
        entityManager.shutdown();
        collisionManager.shutdown();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // Bird (circle)
        bird = new Bird(screenW * 0.25f, screenH * 0.5f, 20f); // radius = 20
        entityManager.addEntity(bird);

        // Register bird collider with CollisionManager
        collisionManager.addCollisionComponent(bird.getCollision());

        // Give IOManager the bird to control
        ioManager.setPlayer(bird);

        // Pipes (rectangles)
        float pipeX = screenW + 120f;
        float gapCenterY = screenH * 0.5f;

        float bottomH = gapCenterY - gapHeight / 2f;
        float topY = gapCenterY + gapHeight / 2f;
        float topH = screenH - topY;

        // Bottom pipe
        bottomPipe = new Entity();
        bottomPipe.addComponent(new TransformComponent(pipeX, 0, pipeWidth, bottomH));
        bottomPipe.addComponent(new PhysicsComponent(pipeVelX, 0, 1));
        CollisionComponent bottomCol = new CollisionComponent(1, false);
        bottomPipe.addComponent(bottomCol);

        entityManager.addEntity(bottomPipe);
        collisionManager.addCollisionComponent(bottomCol);

        // Top pipe
        topPipe = new Entity();
        topPipe.addComponent(new TransformComponent(pipeX, topY, pipeWidth, topH));
        topPipe.addComponent(new PhysicsComponent(pipeVelX, 0, 1));
        CollisionComponent topCol = new CollisionComponent(1, false);
        topPipe.addComponent(topCol);

        entityManager.addEntity(topPipe);
        collisionManager.addCollisionComponent(topCol);

        // Flush entitiesToAdd -> entities so MovementManager sees them immediately
        entityManager.update(0);

        // Reset collision flag
        bird.getCollision().reset();
    }

    @Override
    public void update(float delta) {

        // ✅ Pause key (ESC) -> PauseScene
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.changeScene(SceneType.PAUSE);
            return;
        }

        // Gravity (game rule)
        PhysicsComponent bp = bird.getPhysics();
        bp.velocityY -= gravity * delta;

        // Managers update
        entityManager.update(delta);
        ioManager.update(delta);
        movementManager.update(delta);
        collisionManager.update(delta);

        // Collision -> Game Over
        if (bird.getCollision().hasCollided()) {
            sceneManager.changeScene(SceneType.GAME_OVER);
            return;
        }

        // Out of screen -> Game Over
        TransformComponent bt = bird.getTransform();
        float screenH = Gdx.graphics.getHeight();
        if (bt.positionY <= 0 || bt.positionY + bt.height >= screenH) {
            sceneManager.changeScene(SceneType.GAME_OVER);
            return;
        }

        // Respawn pipes if they go off-screen (simple fixed gap)
        TransformComponent tt = (TransformComponent) topPipe.getComponent(TransformComponent.class);
        TransformComponent bb = (TransformComponent) bottomPipe.getComponent(TransformComponent.class);

        if (tt.positionX + tt.width < 0) {
            float screenW = Gdx.graphics.getWidth();
            float newX = screenW + 120f;

            float gapCenterY = screenH * 0.5f;
            float newBottomH = gapCenterY - gapHeight / 2f;
            float newTopY = gapCenterY + gapHeight / 2f;
            float newTopH = screenH - newTopY;

            bb.positionX = newX;
            bb.positionY = 0;
            bb.width = pipeWidth;
            bb.height = newBottomH;

            tt.positionX = newX;
            tt.positionY = newTopY;
            tt.width = pipeWidth;
            tt.height = newTopH;

            // Allow collision again for new pipes
            bird.getCollision().reset();
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.6f, 0.9f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw pipes (rectangles)
        TransformComponent tt = (TransformComponent) topPipe.getComponent(TransformComponent.class);
        TransformComponent bb = (TransformComponent) bottomPipe.getComponent(TransformComponent.class);

        shapeRenderer.setColor(0f, 0.7f, 0f, 1f);
        shapeRenderer.rect(tt.positionX, tt.positionY, tt.width, tt.height);
        shapeRenderer.rect(bb.positionX, bb.positionY, bb.width, bb.height);

        // Draw bird (circle)
        TransformComponent bt = bird.getTransform();
        float r = bird.getRadius();
        shapeRenderer.setColor(1f, 1f, 0f, 1f);
        shapeRenderer.circle(bt.positionX + r, bt.positionY + r, r);

        shapeRenderer.end();
    }

    @Override
    public void onExit() {
        // do nothing; we want state preserved when going to PAUSE
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
    }
}
