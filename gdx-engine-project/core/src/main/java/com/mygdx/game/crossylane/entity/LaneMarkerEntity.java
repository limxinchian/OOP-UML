package com.mygdx.game.crossylane.entity;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.entity.Entity;
import com.mygdx.game.engine.entity.TransformComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * A white dividing line drawn between two traffic lanes.
 * Purely decorative — no collision component.
 *
 * Refactor note (Part 2):
 * Previously calculated its own Y from raw config constants, which
 * bypassed LaneLayout and could desynchronize at non-standard lane
 * counts.  Now accepts a pre-computed Y from the factory so all
 * lane-position logic flows through LaneLayout consistently.
 *
 * Addresses: Low coupling, Single Responsibility (layout logic stays
 * in LaneLayout, not duplicated here).
 *
 * Components attached:
 *  - TransformComponent  : spans full screen width at the lane boundary Y
 *  - RenderableComponent : semi-transparent white line
 */
public class LaneMarkerEntity extends Entity {

    /**
     * @param markerY the pre-computed Y position of the lane boundary
     */
    public LaneMarkerEntity(float markerY) {
        addComponent(new TransformComponent(
                0f, markerY,
                CrossyLaneConfig.WORLD_WIDTH,
                CrossyLaneConfig.LANE_MARKER_HEIGHT));

        // Semi-transparent white dashed-line feel
        addComponent(RenderableComponent.rectangle(1f, 1f, 1f, 0.6f));
    }
}
