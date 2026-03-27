package com.mygdx.game.crossylane.layout;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;

public class LaneLayoutTest {

    @Test
    public void singleLaneLayoutUsesNoSeparators() {
        LaneLayout layout = LaneLayout.forLaneCount(1);

        assertEquals(0f, layout.getSeparatorHeight(), 0.0001f);
        assertEquals(CrossyLaneConfig.ROAD_START_Y, layout.getLaneBaseY(0), 0.0001f);
        assertEquals(CrossyLaneConfig.ROAD_START_Y + CrossyLaneConfig.LANE_HEIGHT,
                layout.getRoadTopY(), 0.0001f);
    }

    @Test
    public void threeLaneLayoutUsesFullHeightSeparators() {
        LaneLayout layout = LaneLayout.forLaneCount(3);

        assertEquals(CrossyLaneConfig.LANE_HEIGHT, layout.getSeparatorHeight(), 0.0001f);
        assertEquals(CrossyLaneConfig.ROAD_START_Y, layout.getLaneBaseY(0), 0.0001f);
        assertEquals(CrossyLaneConfig.ROAD_START_Y + (2f * CrossyLaneConfig.LANE_HEIGHT),
                layout.getLaneBaseY(1), 0.0001f);
        assertEquals(CrossyLaneConfig.ROAD_START_Y + (4f * CrossyLaneConfig.LANE_HEIGHT),
                layout.getLaneBaseY(2), 0.0001f);
        assertEquals(LaneLayout.NO_LANE_INDEX, layout.getLaneIndexForY(150f));
        assertEquals(1, layout.getLaneIndexForY(210f));
    }

    @Test
    public void fiveLaneLayoutCompressesSeparatorsToFitPlayArea() {
        LaneLayout layout = LaneLayout.forLaneCount(5);

        assertEquals(32.5f, layout.getSeparatorHeight(), 0.0001f);
        assertEquals(60f, layout.getLaneBaseY(0), 0.0001f);
        assertEquals(152.5f, layout.getLaneBaseY(1), 0.0001f);
        assertEquals(245f, layout.getLaneBaseY(2), 0.0001f);
        assertEquals(337.5f, layout.getLaneBaseY(3), 0.0001f);
        assertEquals(430f, layout.getLaneBaseY(4), 0.0001f);
        assertEquals(CrossyLaneConfig.GOAL_ZONE_Y, layout.getRoadTopY(), 0.0001f);
        assertEquals(CrossyLaneConfig.GOAL_ZONE_Y - CrossyLaneConfig.ROAD_START_Y,
                layout.getRoadBlockHeight(), 0.0001f);
        assertEquals(LaneLayout.NO_LANE_INDEX, layout.getLaneIndexForY(130f));
        assertEquals(1, layout.getLaneIndexForY(170f));
    }
}
