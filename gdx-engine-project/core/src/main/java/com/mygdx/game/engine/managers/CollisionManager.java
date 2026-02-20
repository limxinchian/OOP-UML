package com.mygdx.game.engine.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.mygdx.game.engine.collision.CollisionComponent;
import com.mygdx.game.engine.collision.CollisionInfo;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.PhysicsComponent;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.math.Rectangle;
import com.mygdx.game.engine.math.Vector2;

public class CollisionManager implements IManager {

    private final EntityManager entityManager;

    // Tracks collision pairs currently active: key -> pair
    private final Map<String, Pair> activePairs = new HashMap<>();

    public CollisionManager(EntityManager entityManager) {
        if (entityManager == null) throw new IllegalArgumentException("entityManager cannot be null");
        this.entityManager = entityManager;
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
        // Collect colliders from current entities (no manual registration needed)
        List<CollisionComponent> colliders = new ArrayList<>();
        for (Entity e : entityManager.getEntities()) {
            if (!e.isActive()) continue;
            CollisionComponent c = e.getComponent(CollisionComponent.class);
            if (c != null && c.isEnabled()) {
                colliders.add(c);
            }
        }

        Map<String, Pair> newPairs = new HashMap<>();

        for (int i = 0; i < colliders.size(); i++) {
            for (int j = i + 1; j < colliders.size(); j++) {
                CollisionComponent a = colliders.get(i);
                CollisionComponent b = colliders.get(j);

                if (!a.canCollideWith(b) || !b.canCollideWith(a)) continue;

                Rectangle ra = a.getBounds();
                Rectangle rb = b.getBounds();
                if (ra == null || rb == null) continue;

                if (!ra.overlaps(rb)) continue;

                String key = pairKey(a, b);
                Pair pair = new Pair(a, b);
                newPairs.put(key, pair);

                boolean isNew = !activePairs.containsKey(key);
                if (isNew) {
                    a.onCollisionEnter(b);
                    b.onCollisionEnter(a);
                }

                // Resolution capability (engine feature):
                // only resolve if both are NOT triggers.
                if (!a.isTrigger() && !b.isTrigger()) {
                    CollisionInfo info = computeInfo(ra, rb);
                    resolve(a, b, info);
                }
            }
        }

        // Exits: anything that was active but not present now
        HashSet<String> oldKeys = new HashSet<>(activePairs.keySet());
        for (String oldKey : oldKeys) {
            if (!newPairs.containsKey(oldKey)) {
                Pair oldPair = activePairs.get(oldKey);
                oldPair.a.onCollisionExit(oldPair.b);
                oldPair.b.onCollisionExit(oldPair.a);
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

    private void resolve(CollisionComponent aCol, CollisionComponent bCol, CollisionInfo info) {
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

        // If neither can move, nothing to resolve.
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

        // Stop velocity along the axis we resolved
        if (aDynamic && pa != null) {
            if (mtvX != 0) pa.velocityX = 0;
            if (mtvY != 0) pa.velocityY = 0;
        }
        if (bDynamic && pb != null) {
            if (mtvX != 0) pb.velocityX = 0;
            if (mtvY != 0) pb.velocityY = 0;
        }
    }

    private static String pairKey(CollisionComponent a, CollisionComponent b) {
        int ha = System.identityHashCode(a);
        int hb = System.identityHashCode(b);
        int lo = Math.min(ha, hb);
        int hi = Math.max(ha, hb);
        return lo + ":" + hi;
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
