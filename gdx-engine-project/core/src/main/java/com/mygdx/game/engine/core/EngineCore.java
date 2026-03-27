package com.mygdx.game.engine.core;

import com.mygdx.game.engine.event.EventBus;
import com.mygdx.game.engine.managers.CollisionManager;
import com.mygdx.game.engine.managers.EntityManager;
import com.mygdx.game.engine.managers.IOManager;
import com.mygdx.game.engine.managers.MovementManager;
import com.mygdx.game.engine.scene.IScene;
import com.mygdx.game.engine.scene.SceneManager;

/**
 * EngineCore (non-contextual):
 * Owns the engine managers, event bus, and scene manager, then runs
 * the standard frame pipeline.
 *
 * K = scene key type (enum/string/etc).
 *
 * Change log (Part 2 refactor):
 * - Added EventBus as a core engine service (Observer pattern).
 *   This gives scenes and entities a decoupled publish-subscribe channel
 *   without requiring direct references to each other.
 * - Accepts worldWidth / worldHeight so the rendering subsystem can be
 *   configured without importing any game-specific class, ensuring the
 *   engine layer has zero dependencies on game code.
 *
 * Addresses: Engine/Game separation, Dependency Inversion Principle.
 */
public class EngineCore<K> {

    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;
    private final EventBus eventBus;

    private final SceneManager<K> sceneManager;

    private boolean initialized = false;
    private boolean disposed = false;

    /**
     * @param worldWidth  logical world width (passed to OutputManager)
     * @param worldHeight logical world height (passed to OutputManager)
     */
    public EngineCore(float worldWidth, float worldHeight) {
        this.entityManager = new EntityManager();
        this.movementManager = new MovementManager(entityManager);
        this.collisionManager = new CollisionManager(entityManager);
        this.ioManager = new IOManager(entityManager, worldWidth, worldHeight);
        this.eventBus = new EventBus();
        this.sceneManager = new SceneManager<>();
    }

    /**
     * Initialize all engine managers. Call once before ticking/rendering.
     */
    public void initialize() {
        ensureNotDisposed();

        if (initialized) return;

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

        sceneManager.update(dt);

        IScene<K> current = sceneManager.getCurrentScene();
        if (current != null && current.updatesWorld()) {
            entityManager.update(0f);
            ioManager.update(dt);
            movementManager.update(dt);
            collisionManager.update(dt);
        }

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

        sceneManager.dispose();

        ioManager.shutdown();
        collisionManager.shutdown();
        movementManager.shutdown();
        entityManager.shutdown();

        eventBus.clear();

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

    // --- Accessors ---
    public EntityManager getEntityManager() { return entityManager; }
    public MovementManager getMovementManager() { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public IOManager getIoManager() { return ioManager; }
    public EventBus getEventBus() { return eventBus; }
    public SceneManager<K> getSceneManager() { return sceneManager; }
}
