package com.mygdx.game.engine.collision;

import com.mygdx.game.engine.math.Vector2;

/**
 * CollisionInfo contains minimal data used for resolution.
 * mtv = minimum translation vector to move A out of B (A += mtv).
 */
public class CollisionInfo {
    private final Vector2 mtv;
    private final float overlapX;
    private final float overlapY;

    public CollisionInfo(Vector2 mtv, float overlapX, float overlapY) {
        this.mtv = (mtv == null) ? new Vector2() : mtv.copy();
        this.overlapX = overlapX;
        this.overlapY = overlapY;
    }

    public Vector2 getMtv() {
        return mtv.copy();
    }

    public float getMtvX() {
        return mtv.getX();
    }

    public float getMtvY() {
        return mtv.getY();
    }

    public float getOverlapX() {
        return overlapX;
    }

    public float getOverlapY() {
        return overlapY;
    }
}
