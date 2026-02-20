package com.mygdx.game.demo.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.demo.entities.Bird;
import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.io.InputComponent;
import com.mygdx.game.engine.managers.CollisionManager;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.managers.MovementManager;
import com.mygdx.game.engine.movement.BasicMovementStrategy;
import com.mygdx.game.engine.movement.MovementComponent;
import com.mygdx.game.engine.render.RenderableComponent;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

public class GameScene implements IScene<DemoSceneKey> {

    private final SceneManager<DemoSceneKey> sceneManager;
    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;

    private Bird bird;

    private final List<Entity> topPipes = new ArrayList<>();
    private final List<Entity> bottomPipes = new ArrayList<>();

    // Demo/game values
    private float gravity = 900f;
    private float pipeVelX = -220f;
    private float gapHeight = 180f;
    private float pipeWidth = 80f;

    // Demo control values
    private float jumpVelocity = 350f;
    private float moveSpeed = 200f;

    // Scaling demo
    private int pipePairs = 20;      // press 1 or 2
    private float pipeSpacing = 260f;

    private boolean initialized = false;

    public GameScene(SceneManager<DemoSceneKey> sceneManager,
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
    public DemoSceneKey getKey() {
        return DemoSceneKey.GAME;
    }

    @Override
    public void onEnter() {
        // If resuming from pause (stack pop), do NOT reset the run
        if (initialized && sceneManager.getPreviousSceneKey() == DemoSceneKey.PAUSE) {
            return;
        }

        initNewRun();
        initialized = true;
    }

    private void initNewRun() {
        // Clear world state for a new run
        entityManager.shutdown();
        collisionManager.shutdown();

        topPipes.clear();
        bottomPipes.clear();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // Bird
        bird = new Bird(screenW * 0.25f, screenH * 0.5f, 20f);

        // Rendering
        bird.addComponent(RenderableComponent.circle(bird.getRadius(), 1f, 1f, 0f, 1f));

        // Input
        InputComponent input = new InputComponent();
        input.bindJustPressed(Input.Keys.UP, (e, dt) -> {
            PhysicsComponent p = e.getComponent(PhysicsComponent.class);
            if (p != null) p.setVelocityY(jumpVelocity);
        });
        input.bindHold(Input.Keys.LEFT, (e, dt) -> {
            PhysicsComponent p = e.getComponent(PhysicsComponent.class);
            if (p != null) p.setVelocityX(-moveSpeed);
        });
        input.bindHold(Input.Keys.RIGHT, (e, dt) -> {
            PhysicsComponent p = e.getComponent(PhysicsComponent.class);
            if (p != null) p.setVelocityX(moveSpeed);
        });
        bird.addComponent(input);

        // Per-entity movement
        bird.addComponent(new MovementComponent(new BasicMovementStrategy()));

        entityManager.addEntity(bird);

        // Pipes
        float startX = screenW + 120f;
        for (int i = 0; i < pipePairs; i++) {
            float x = startX + i * pipeSpacing;
            spawnPipePair(x, screenH);
        }

        // Flush spawned entities so managers can see them immediately
        entityManager.update(0f);

        // Reset collision flag
        bird.getCollision().reset();
    }

    private void spawnPipePair(float pipeX, float screenH) {
        float gapCenterY = screenH * 0.5f;

        float bottomH = gapCenterY - gapHeight / 2f;
        float topY = gapCenterY + gapHeight / 2f;
        float topH = screenH - topY;

        // Bottom pipe
        Entity bottomPipe = new Entity();
        bottomPipe.addComponent(new TransformComponent(pipeX, 0, pipeWidth, bottomH));
        bottomPipe.addComponent(new PhysicsComponent(pipeVelX, 0, 1));
        bottomPipe.addComponent(new CollisionComponent(1, false));
        bottomPipe.addComponent(RenderableComponent.rectangle(0f, 0.7f, 0f, 1f));
        bottomPipe.addComponent(new MovementComponent(new BasicMovementStrategy()));

        // Top pipe
        Entity topPipe = new Entity();
        topPipe.addComponent(new TransformComponent(pipeX, topY, pipeWidth, topH));
        topPipe.addComponent(new PhysicsComponent(pipeVelX, 0, 1));
        topPipe.addComponent(new CollisionComponent(1, false));
        topPipe.addComponent(RenderableComponent.rectangle(0f, 0.7f, 0f, 1f));
        topPipe.addComponent(new MovementComponent(new BasicMovementStrategy()));

        entityManager.addEntity(bottomPipe);
        entityManager.addEntity(topPipe);

        bottomPipes.add(bottomPipe);
        topPipes.add(topPipe);
    }

    @Override
    public void update(float delta) {
        // Pause (stack overlay)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.pushScene(DemoSceneKey.PAUSE);
            return;
        }

        // Scaling demo (restart run with different counts)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            pipePairs = 1;
            initNewRun();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            pipePairs = 20;
            initNewRun();
            return;
        }

        if (bird == null) return;

        // Gravity + stop horizontal drift unless input sets it this frame
        PhysicsComponent bp = bird.getPhysics();
        if (bp != null) {
            bp.setVelocityX(0f);
            bp.setVelocityY(bp.getVelocityY() - gravity * delta);
        }

        recyclePipesIfNeeded();
    }

    private void recyclePipesIfNeeded() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // Find right-most pipe X
        float maxX = -Float.MAX_VALUE;
        for (Entity t : topPipes) {
            TransformComponent tt = t.getComponent(TransformComponent.class);
            if (tt != null) maxX = Math.max(maxX, tt.getPositionX());
        }

        for (int i = 0; i < topPipes.size(); i++) {
            Entity top = topPipes.get(i);
            Entity bottom = bottomPipes.get(i);

            TransformComponent tt = top.getComponent(TransformComponent.class);
            TransformComponent bb = bottom.getComponent(TransformComponent.class);
            if (tt == null || bb == null) continue;

            if (tt.getRight() < 0f) {
                float newX = Math.max(screenW + 120f, maxX + pipeSpacing);

                float gapCenterY = screenH * 0.5f;
                float newBottomH = gapCenterY - gapHeight / 2f;
                float newTopY = gapCenterY + gapHeight / 2f;
                float newTopH = screenH - newTopY;

                bb.setPosition(newX, 0f);
                bb.setSize(pipeWidth, newBottomH);

                tt.setPosition(newX, newTopY);
                tt.setSize(pipeWidth, newTopH);

                maxX = newX;
            }
        }
    }

    @Override
    public void afterWorldUpdate(float delta) {
        if (bird == null) return;

        // 1) Collision with pipes/rectangles -> Game Over
        if (bird.getCollision() != null && bird.getCollision().hasCollided()) {
            sceneManager.resetTo(DemoSceneKey.GAME_OVER);
            return;
        }

        // 2) Out of screen on ANY side -> Game Over
        TransformComponent bt = bird.getTransform();
        if (bt == null) return;

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        boolean hitLeft   = bt.getPositionX() <= 0f;
        boolean hitRight  = bt.getRight() >= screenW;
        boolean hitBottom = bt.getPositionY() <= 0f;
        boolean hitTop    = bt.getTop() >= screenH;

        if (hitLeft || hitRight || hitBottom || hitTop) {
            sceneManager.resetTo(DemoSceneKey.GAME_OVER);
        }
    }

    @Override
    public void render() {
        ioManager.getOutput().beginFrame(0.6f, 0.9f, 1f, 1f);
        ioManager.getOutput().renderEntities(entityManager.getEntities());
        ioManager.getOutput().endFrame();
    }

    @Override
    public void onExit() { }

    @Override
    public void dispose() { }
}
