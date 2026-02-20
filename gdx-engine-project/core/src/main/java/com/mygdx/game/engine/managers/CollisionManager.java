package com.mygdx.game.engine.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.collision.CollisionInfo;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.math.Rectangle;
import com.mygdx.game.engine.math.Vector2;

/**
 * CollisionManager with simple spatial-hash broadphase for better scaling.
 * - Detects overlaps
 * - Fires onCollisionEnter / onCollisionExit
 * - Resolves solid (non-trigger) collisions using MTV
 */
public class CollisionManager implements IManager {

    private final EntityManager entityManager;

    // Tracks collision pairs currently active: key -> pair
    private final Map<String, Pair> activePairs = new HashMap<>();

    // Broadphase tuning
    private static final float DEFAULT_CELL_SIZE = 96f;
    private float cellSize = DEFAULT_CELL_SIZE;

    public CollisionManager(EntityManager entityManager) {
        if (entityManager == null) throw new IllegalArgumentException("entityManager cannot be null");
        this.entityManager = entityManager;
    }

    public void setBroadphaseCellSize(float cellSize) {
        if (cellSize <= 0f) throw new IllegalArgumentException("cellSize must be > 0");
        this.cellSize = cellSize;
    }

    @Override
    public void initialize() { }

    @Override
    public void update(float deltaTime) {
        detectAndResolve();
    }

    @Override
    public void shutdown() {
        activePairs.clear();
    }

    private void detectAndResolve() {
        // Collect colliders + bounds
        List<CollisionComponent> colliders = new ArrayList<>();
        Map<CollisionComponent, Rectangle> boundsCache = new HashMap<>();

        for (Entity e : entityManager.getEntities()) {
            if (!e.isActive()) continue;

            CollisionComponent c = e.getComponent(CollisionComponent.class);
            if (c == null || !c.isEnabled()) continue;

            Rectangle r = c.getBounds();
            if (r == null) continue;

            colliders.add(c);
            boundsCache.put(c, r);
        }

        // Broadphase buckets: cell -> colliders
        Map<Long, List<CollisionComponent>> buckets = new HashMap<>();
        for (CollisionComponent c : colliders) {
            Rectangle r = boundsCache.get(c);
            if (r == null) continue;

            int minX = fastFloor(r.x / cellSize);
            int maxX = fastFloor((r.x + r.width) / cellSize);
            int minY = fastFloor(r.y / cellSize);
            int maxY = fastFloor((r.y + r.height) / cellSize);

            for (int gx = minX; gx <= maxX; gx++) {
                for (int gy = minY; gy <= maxY; gy++) {
                    long key = cellKey(gx, gy);
                    buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(c);
                }
            }
        }

        Map<String, Pair> newPairs = new HashMap<>();
        HashSet<String> checkedThisFrame = new HashSet<>();

        // Narrowphase within each bucket
        for (List<CollisionComponent> bucket : buckets.values()) {
            int size = bucket.size();
            if (size < 2) continue;

            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    CollisionComponent a = bucket.get(i);
                    CollisionComponent b = bucket.get(j);

                    if (!a.canCollideWith(b) || !b.canCollideWith(a)) continue;

                    String key = pairKey(a, b);
                    if (!checkedThisFrame.add(key)) continue;

                    Rectangle ra = boundsCache.get(a);
                    Rectangle rb = boundsCache.get(b);
                    if (ra == null || rb == null) continue;

                    if (!ra.overlaps(rb)) continue;

                    newPairs.put(key, new Pair(a, b));

                    boolean isNew = !activePairs.containsKey(key);
                    if (isNew) {
                        a.onCollisionEnter(b);
                        b.onCollisionEnter(a);
                    }

                    // Resolve only if both are solid
                    if (!a.isTrigger() && !b.isTrigger()) {
                        CollisionInfo info = computeInfo(ra, rb);
                        resolve(a, b, info);
                    }
                }
            }
        }

        // Exit events
        for (String oldKey : new HashSet<>(activePairs.keySet())) {
            if (!newPairs.containsKey(oldKey)) {
                Pair oldPair = activePairs.get(oldKey);
                if (oldPair != null) {
                    oldPair.a.onCollisionExit(oldPair.b);
                    oldPair.b.onCollisionExit(oldPair.a);
                }
            }
        }

        activePairs.clear();
        activePairs.putAll(newPairs);
    }

    private static CollisionInfo computeInfo(Rectangle a, Rectangle b) {
        float aHalfW = a.width / 2f;
        float aHalfH = a.height / 2f;
        float bHalfW = b.width / 2f;
        float bHalfH = b.height / 2f;

        float aCx = a.x + aHalfW;
        float aCy = a.y + aHalfH;
        float bCx = b.x + bHalfW;
        float bCy = b.y + bHalfH;

        // dx/dy from B to A (same as your original CollisionManager logic)
        float dx = aCx - bCx;
        float dy = aCy - bCy;

        float overlapX = (aHalfW + bHalfW) - Math.abs(dx);
        float overlapY = (aHalfH + bHalfH) - Math.abs(dy);

        // MTV moves A out of B (A += mtv)
        Vector2 mtv = new Vector2(0, 0);

        if (overlapX < overlapY) {
            mtv.x = (dx >= 0) ? overlapX : -overlapX;
        } else {
            mtv.y = (dy >= 0) ? overlapY : -overlapY;
        }

        return new CollisionInfo(mtv, overlapX, overlapY);
    }

    private static void resolve(CollisionComponent aCol, CollisionComponent bCol, CollisionInfo info) {
        Entity a = aCol.getOwner();
        Entity b = bCol.getOwner();
        if (a == null || b == null) return;

        TransformComponent ta = a.getComponent(TransformComponent.class);
        TransformComponent tb = b.getComponent(TransformComponent.class);
        if (ta == null || tb == null) return;

        PhysicsComponent pa = a.getComponent(PhysicsComponent.class);
        PhysicsComponent pb = b.getComponent(PhysicsComponent.class);

        boolean aDynamic = (pa != null && pa.isEnabled());
        boolean bDynamic = (pb != null && pb.isEnabled());

        if (!aDynamic && !bDynamic) return;

        float mtvX = info.mtv.x;
        float mtvY = info.mtv.y;

        if (aDynamic && bDynamic) {
            ta.positionX += mtvX / 2f;
            ta.positionY += mtvY / 2f;
            tb.positionX -= mtvX / 2f;
            tb.positionY -= mtvY / 2f;
        } else if (aDynamic) {
            ta.positionX += mtvX;
            ta.positionY += mtvY;
        } else {
            tb.positionX -= mtvX;
            tb.positionY -= mtvY;
        }

        if (aDynamic && pa != null) {
            if (mtvX != 0) pa.velocityX = 0;
            if (mtvY != 0) pa.velocityY = 0;
        }
        if (bDynamic && pb != null) {
            if (mtvX != 0) pb.velocityX = 0;
            if (mtvY != 0) pb.velocityY = 0;
        }
    }

    private static int fastFloor(float v) {
        int i = (int) v;
        return v < i ? i - 1 : i;
    }

    private static long cellKey(int x, int y) {
        return (((long) x) << 32) ^ (y & 0xffffffffL);
    }

    private static String pairKey(CollisionComponent a, CollisionComponent b) {
        UUID ida = (a.getOwner() != null) ? a.getOwner().getId() : null;
        UUID idb = (b.getOwner() != null) ? b.getOwner().getId() : null;

        if (ida == null || idb == null) {
            int ha = System.identityHashCode(a);
            int hb = System.identityHashCode(b);
            int lo = Math.min(ha, hb);
            int hi = Math.max(ha, hb);
            return lo + ":" + hi;
        }

        String sa = ida.toString();
        String sb = idb.toString();
        return (sa.compareTo(sb) <= 0) ? (sa + ":" + sb) : (sb + ":" + sa);
    }

    private static final class Pair {
        final CollisionComponent a;
        final CollisionComponent b;

        Pair(CollisionComponent a, CollisionComponent b) {
            this.a = a;
            this.b = b;
        }
    }
}
