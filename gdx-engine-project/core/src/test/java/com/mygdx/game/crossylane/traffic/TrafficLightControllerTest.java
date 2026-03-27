package com.mygdx.game.crossylane.traffic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for TrafficLightController.
 *
 * Updated (Part 2) to reflect corrected traffic-light behaviour:
 *  - The light is global: entering ANY road lane from off-road triggers scoring.
 *  - RED  = penalty (negative delta).
 *  - GREEN = safe passage, no penalty AND no bonus (delta = 0).
 *  - Moving between road lanes does not re-trigger scoring.
 *  - Penalty can occur multiple times per round (each grass→road entry is independent).
 */
public class TrafficLightControllerTest {
    private static final int CONTROLLED_LANE = 0;
    private static final float SWITCH_INTERVAL = 4f;
    private static final int RED_SCORE_DELTA = -50;
    private static final int GREEN_SCORE_DELTA = 0;

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
    public void deductsScoreWhenEnteringAnyLaneOnRed() {
        TrafficLightController controller = createController();

        // Entering lane 0 from off-road on red → penalty
        assertEquals(RED_SCORE_DELTA, controller.scoreForLaneEntry(
                TrafficLightController.NO_LANE_INDEX, 0));
    }

    @Test
    public void deductsScoreWhenEnteringNonZeroLaneOnRed() {
        TrafficLightController controller = createController();

        // Entering lane 1 from off-road on red → same penalty
        assertEquals(RED_SCORE_DELTA, controller.scoreForLaneEntry(
                TrafficLightController.NO_LANE_INDEX, 1));

        // Entering lane 2 from off-road on red → same penalty
        assertEquals(RED_SCORE_DELTA, controller.scoreForLaneEntry(
                TrafficLightController.NO_LANE_INDEX, 2));
    }

    @Test
    public void noScoreChangeWhenEnteringOnGreen() {
        TrafficLightController controller = createController();
        controller.tick(SWITCH_INTERVAL); // switch to green

        // Entering any lane on green → no bonus, no penalty
        assertEquals(0, controller.scoreForLaneEntry(
                TrafficLightController.NO_LANE_INDEX, 0));
        assertEquals(0, controller.scoreForLaneEntry(
                TrafficLightController.NO_LANE_INDEX, 1));
    }

    @Test
    public void doesNotRepeatScoreWhileRemainingOnRoad() {
        TrafficLightController controller = createController();

        // Staying on the same lane → no score
        assertEquals(0, controller.scoreForLaneEntry(0, 0));
    }

    @Test
    public void penalisesAgainAfterLeavingRoadAndReEnteringOnRed() {
        TrafficLightController controller = createController();

        // First entry: grass → lane 0 on red → penalty
        assertEquals(RED_SCORE_DELTA, controller.scoreForLaneEntry(
                TrafficLightController.NO_LANE_INDEX, 0));

        // Move between road lanes → no score
        assertEquals(0, controller.scoreForLaneEntry(0, 2));

        // Leave road entirely
        assertEquals(0, controller.scoreForLaneEntry(2, TrafficLightController.NO_LANE_INDEX));

        // Re-enter road on red → penalty again
        assertEquals(RED_SCORE_DELTA, controller.scoreForLaneEntry(
                TrafficLightController.NO_LANE_INDEX, 1));
    }

    @Test
    public void doesNotScoreWhenMovingBetweenRoadLanes() {
        TrafficLightController controller = createController();

        assertEquals(0, controller.scoreForLaneEntry(0, 1));
        assertEquals(0, controller.scoreForLaneEntry(1, 0));
        assertEquals(0, controller.scoreForLaneEntry(1, 2));
    }

    @Test
    public void doesNotScoreWhenLeavingRoad() {
        TrafficLightController controller = createController();

        // Road → grass → no score regardless of light colour
        assertEquals(0, controller.scoreForLaneEntry(0, TrafficLightController.NO_LANE_INDEX));
    }

    private TrafficLightController createController() {
        return new TrafficLightController(
                CONTROLLED_LANE,
                SWITCH_INTERVAL,
                RED_SCORE_DELTA,
                GREEN_SCORE_DELTA);
    }
}
