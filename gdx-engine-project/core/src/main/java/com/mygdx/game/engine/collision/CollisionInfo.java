package com.mygdx.game.engine.collision;

import com.mygdx.game.engine.math.Vector2;

/**
 * CollisionInfo contains minimal data used for resolution.
 * mtv = minimum translation vector to move A out of B (A += mtv).
 */
public class CollisionInfo {
    public final Vector2 mtv;
    public final float overlapX;
    public final float overlapY;

    public CollisionInfo(Vector2 mtv, float overlapX, float overlapY) {
        this.mtv = mtv;
        this.overlapX = overlapX;
        this.overlapY = overlapY;
    }
}
