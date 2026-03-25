package com.mygdx.game.crossylane.traffic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TrafficLightControllerTest {
    private static final int CONTROLLED_LANE = 1;
    private static final float SWITCH_INTERVAL = 4f;
    private static final int RED_SCORE_DELTA = -50;
    private static final int GREEN_SCORE_DELTA = 50;

    @Test
    public void startsRedThenTransitionsGreenAndBackToRed() {
        TrafficLightController controller = createController();

        assertTrue(controller.isRed());
        assertEquals("RED", controller.getCurrentPhaseName());

        controller.tick(SWITCH_INTERVAL);
        assertTrue(controller.isGreen());
        assertEquals("GREEN", controller.getCurrentPhaseName());

        controller.tick(SWITCH_INTERVAL);
        assertTrue(controller.isRed());
        assertFalse(controller.isGreen());
    }

    @Test
    public void deductsScoreWhenEnteringControlledLaneOnRed() {
        TrafficLightController controller = createController();

        assertEquals(RED_SCORE_DELTA, controller.scoreForLaneEntry(0, CONTROLLED_LANE));
    }

    @Test
    public void addsScoreWhenEnteringControlledLaneOnGreen() {
        TrafficLightController controller = createController();
        controller.tick(SWITCH_INTERVAL);

        assertEquals(GREEN_SCORE_DELTA, controller.scoreForLaneEntry(0, CONTROLLED_LANE));
    }

    @Test
    public void doesNotRepeatScoreWhileRemainingInControlledLane() {
        TrafficLightController controller = createController();

        assertEquals(0, controller.scoreForLaneEntry(CONTROLLED_LANE, CONTROLLED_LANE));
    }

    @Test
    public void scoresAgainAfterLeavingAndReEnteringControlledLane() {
        TrafficLightController controller = createController();

        assertEquals(RED_SCORE_DELTA, controller.scoreForLaneEntry(0, CONTROLLED_LANE));
        assertEquals(0, controller.scoreForLaneEntry(CONTROLLED_LANE, 2));

        controller.tick(SWITCH_INTERVAL);
        assertEquals(GREEN_SCORE_DELTA, controller.scoreForLaneEntry(2, CONTROLLED_LANE));
    }

    private TrafficLightController createController() {
        return new TrafficLightController(
                CONTROLLED_LANE,
                SWITCH_INTERVAL,
                RED_SCORE_DELTA,
                GREEN_SCORE_DELTA);
    }
}
