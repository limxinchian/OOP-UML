package com.mygdx.game.crossylane.entities;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * A white dividing line drawn between two traffic lanes.
 * Purely decorative — no collision component.
 *
 * Components attached:
 *  - TransformComponent  : spans full screen width at the lane boundary Y
 *  - RenderableComponent : semi-transparent white line
 */
public class LaneMarkerEntity extends Entity {

    /**
     * @param laneIndex the lane boundary index (1 = line between lane 0 and lane 1, etc.)
     */
    public LaneMarkerEntity(int laneIndex) {
        float y = CrossyLaneConfig.ROAD_START_Y
                + (laneIndex * CrossyLaneConfig.LANE_HEIGHT)
                - CrossyLaneConfig.LANE_MARKER_HEIGHT / 2f;

        addComponent(new TransformComponent(
                0f, y,
                CrossyLaneConfig.WORLD_WIDTH,
                CrossyLaneConfig.LANE_MARKER_HEIGHT));

        // Semi-transparent white dashed-line feel
        addComponent(RenderableComponent.rectangle(1f, 1f, 1f, 0.6f));
    }
}