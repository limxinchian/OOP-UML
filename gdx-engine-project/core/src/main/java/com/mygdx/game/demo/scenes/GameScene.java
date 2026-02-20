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
import com.mygdx.game.engine.scene.SceneType;

public class GameScene implements IScene {

    private final SceneManager sceneManager;
    private final EntityManager entityManager;
    private final MovementManager movementManager;     // kept for constructor compatibility + demo extensibility
    private final CollisionManager collisionManager;   // kept for constructor compatibility
    private final IOManager ioManager;

    private Bird bird;

    private final List<Entity> topPipes = new ArrayList<>();
    private final List<Entity> bottomPipes = new ArrayList<>();

    // demo/game values (contextual)
    private float gravity = 900f;
    private float pipeVelX = -220f;
    private float gapHeight = 180f;
    private float pipeWidth = 80f;

    // moved from old IOManager (contextual → demo)
    private float jumpVelocity = 350f;
    private float moveSpeed = 200f;

    // scaling demo toggle
    private int pipePairs = 1;          // press 1 or 2
    private float pipeSpacing = 260f;

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
        // ✅ If we are coming back from PAUSE, do NOT reset the game
        if (initialized && sceneManager.getPreviousSceneType() == SceneType.PAUSE) {
            return;
        }

        initNewRun();
        initialized = true;
    }

    private void initNewRun() {
        // Reset world state for a new run
        entityManager.shutdown();
        collisionManager.shutdown();

        topPipes.clear();
        bottomPipes.clear();

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // Bird
        bird = new Bird(screenW * 0.25f, screenH * 0.5f, 20f);
        bird.addComponent(RenderableComponent.circle(bird.getRadius(), 1f, 1f, 0f, 1f));

        // Input bindings are demo logic via Command lambdas
        InputComponent input = new InputComponent();
        input.bindJustPressed(Input.Keys.UP, (e, dt) -> {
            PhysicsComponent p = e.getComponent(PhysicsComponent.class);
            if (p != null) p.velocityY = jumpVelocity;
        });
        input.bindHold(Input.Keys.LEFT, (e, dt) -> {
            PhysicsComponent p = e.getComponent(PhysicsComponent.class);
            if (p != null) p.velocityX = -moveSpeed;
        });
        input.bindHold(Input.Keys.RIGHT, (e, dt) -> {
            PhysicsComponent p = e.getComponent(PhysicsComponent.class);
            if (p != null) p.velocityX = moveSpeed;
        });
        bird.addComponent(input);

        // Optional per-entity movement strategy (shows extensibility)
        bird.addComponent(new MovementComponent(new BasicMovementStrategy()));

        entityManager.addEntity(bird);

        // Pipes
        float startX = screenW + 120f;
        for (int i = 0; i < pipePairs; i++) {
            float pipeX = startX + i * pipeSpacing;
            spawnPipePair(pipeX, screenH);
        }

        // Flush entities immediately so managers see everything this frame
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
        // ✅ Pause key (ESC) -> PauseScene
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.changeScene(SceneType.PAUSE);
            return;
        }

        // ✅ Scaling demo: press 1 or 2 to restart with different pipe counts
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

        // Game rule: gravity + reset horizontal each frame (contextual demo logic)
        PhysicsComponent bp = bird.getPhysics();
        bp.velocityX = 0;
        bp.velocityY -= gravity * delta;

        // Respawn pipes if off-screen
        recyclePipesIfNeeded();
    }

    private void recyclePipesIfNeeded() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // Find right-most pipe X to recycle after it
        float maxX = -Float.MAX_VALUE;
        for (Entity t : topPipes) {
            TransformComponent tt = t.getComponent(TransformComponent.class);
            if (tt != null) maxX = Math.max(maxX, tt.positionX);
        }

        for (int i = 0; i < topPipes.size(); i++) {
            Entity top = topPipes.get(i);
            Entity bottom = bottomPipes.get(i);

            TransformComponent tt = top.getComponent(TransformComponent.class);
            TransformComponent bb = bottom.getComponent(TransformComponent.class);
            if (tt == null || bb == null) continue;

            if (tt.positionX + tt.width < 0) {
                float newX = Math.max(screenW + 120f, maxX + pipeSpacing);

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

                maxX = newX;
            }
        }
    }

    @Override
    public void afterWorldUpdate(float delta) {
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
        }
    }

    @Override
    public void render() {
        // Sky blue background
        ioManager.getOutput().beginFrame(0.6f, 0.9f, 1f, 1f);
        ioManager.getOutput().renderEntities(entityManager.getEntities());
        ioManager.getOutput().endFrame();
    }

    @Override
    public void onExit() {
        // preserve state when going to PAUSE
    }

    @Override
    public void dispose() {
        // OutputManager is disposed by IOManager.shutdown()
    }
}
