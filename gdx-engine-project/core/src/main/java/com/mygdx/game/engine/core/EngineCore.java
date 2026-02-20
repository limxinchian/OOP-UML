package com.mygdx.game.engine.core;

import com.mygdx.game.engine.managers.CollisionManager;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.managers.MovementManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

/**
 * EngineCore (non-contextual):
 * Owns the engine managers + scene manager and runs the standard frame pipeline.
 *
 * K = scene key type (enum/string/etc).
 */
public class EngineCore<K> {

    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;

    private final SceneManager<K> sceneManager;

    public EngineCore() {
        this.entityManager = new EntityManager();
        this.movementManager = new MovementManager(entityManager);
        this.collisionManager = new CollisionManager(entityManager);
        this.ioManager = new IOManager(entityManager);
        this.sceneManager = new SceneManager<>();
    }

    /**
     * Initialize all engine managers. Call once after registering scenes.
     */
    public void initialize() {
        ioManager.initialize();
        movementManager.initialize();
        collisionManager.initialize();
        entityManager.initialize();
    }

    /**
     * Engine update pipeline (no rendering).
     */
    public void tick(float dt) {
        // 1) Scene pre-world update (spawning, gravity, scene switching inputs)
        sceneManager.update(dt);

        // 2) World managers update (only if current scene allows it)
        IScene<K> current = sceneManager.getCurrentScene();
        if (current != null && current.updatesWorld()) {
            // Flush spawns from scene update so managers can see new entities immediately
            entityManager.update(0f);

            ioManager.update(dt);
            movementManager.update(dt);
            collisionManager.update(dt);
        }

        // 3) Scene post-world update (collision/out-of-bounds checks etc.)
        sceneManager.afterWorldUpdate(dt);
    }

    /**
     * Render current scene.
     */
    public void render() {
        sceneManager.render();
    }

    /**
     * Shutdown/dispose everything owned by the engine.
     */
    public void dispose() {
        sceneManager.dispose();

        collisionManager.shutdown();
        movementManager.shutdown();
        entityManager.shutdown();
        ioManager.shutdown();
    }

    // --- Accessors (so demo code can wire scenes) ---
    public EntityManager getEntityManager() { return entityManager; }
    public MovementManager getMovementManager() { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public IOManager getIoManager() { return ioManager; }
    public SceneManager<K> getSceneManager() { return sceneManager; }
}
