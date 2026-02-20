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

    private boolean initialized = false;
    private boolean disposed = false;

    public EngineCore() {
        this.entityManager = new EntityManager();
        this.movementManager = new MovementManager(entityManager);
        this.collisionManager = new CollisionManager(entityManager);
        this.ioManager = new IOManager(entityManager);
        this.sceneManager = new SceneManager<>();
    }

    /**
     * Initialize all engine managers. Call once before ticking/rendering.
     */
    public void initialize() {
        ensureNotDisposed();

        if (initialized) return;

        // Dependency-friendly init order:
        // 1) Entity registry
        // 2) World systems that read/write entities
        // 3) IO (input/output)
        entityManager.initialize();
        movementManager.initialize();
        collisionManager.initialize();
        ioManager.initialize();

        initialized = true;
    }

    /**
     * Optional convenience: start the first scene after initialization.
     */
    public void startScene(K key) {
        ensureInitialized();
        sceneManager.start(key);
    }

    /**
     * Engine update pipeline (no rendering).
     */
    public void tick(float dt) {
        ensureInitialized();

        // 1) Scene pre-world update (spawning, gravity, scene switching inputs)
        sceneManager.update(dt);

        // 2) World managers update (only if current scene allows it)
        IScene<K> current = sceneManager.getCurrentScene();
        if (current != null && current.updatesWorld()) {
            // Flush scene spawns/removals first so systems see a consistent world this frame
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
        ensureInitialized();
        sceneManager.render();
    }

    /**
     * Shutdown/dispose everything owned by the engine.
     */
    public void dispose() {
        if (disposed) return;

        // Dispose scenes first while managers still exist (safer for future scenes).
        sceneManager.dispose();

        // Reverse shutdown order (opposite of initialize)
        ioManager.shutdown();
        collisionManager.shutdown();
        movementManager.shutdown();
        entityManager.shutdown();

        initialized = false;
        disposed = true;
    }

    private void ensureInitialized() {
        if (disposed) {
            throw new IllegalStateException("EngineCore is disposed");
        }
        if (!initialized) {
            throw new IllegalStateException("EngineCore not initialized. Call initialize() first.");
        }
    }

    private void ensureNotDisposed() {
        if (disposed) {
            throw new IllegalStateException("EngineCore is disposed");
        }
    }

    // --- Accessors (so demo code can wire scenes) ---
    public EntityManager getEntityManager() { return entityManager; }
    public MovementManager getMovementManager() { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public IOManager getIoManager() { return ioManager; }
    public SceneManager<K> getSceneManager() { return sceneManager; }
}
