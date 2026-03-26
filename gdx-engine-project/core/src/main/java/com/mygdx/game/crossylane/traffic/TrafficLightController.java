package com.mygdx.game.crossylane.traffic;

/**
 * Owns traffic-light timing and road-entry scoring for one controlled lane.
 */
public class TrafficLightController {
    public static final int NO_LANE_INDEX = -1;

    private final int controlledLaneIndex;
    private final float switchInterval;
    private final int redLaneEntryScoreDelta;
    private final int greenLaneEntryScoreDelta;

    private TrafficLightPhase currentPhase = RedPhase.INSTANCE;
    private float elapsedInPhase = 0f;

    public TrafficLightController(
            int controlledLaneIndex,
            float switchInterval,
            int redLaneEntryScoreDelta,
            int greenLaneEntryScoreDelta) {

        if (controlledLaneIndex < 0) {
            throw new IllegalArgumentException("controlledLaneIndex must be >= 0");
        }
        if (switchInterval <= 0f) {
            throw new IllegalArgumentException("switchInterval must be > 0");
        }

        this.controlledLaneIndex = controlledLaneIndex;
        this.switchInterval = switchInterval;
        this.redLaneEntryScoreDelta = redLaneEntryScoreDelta;
        this.greenLaneEntryScoreDelta = greenLaneEntryScoreDelta;
    }

    public void tick(float delta) {
        if (delta < 0f) {
            throw new IllegalArgumentException("delta cannot be negative");
        }

        elapsedInPhase += delta;
        while (elapsedInPhase >= currentPhase.getDuration(this)) {
            elapsedInPhase -= currentPhase.getDuration(this);
            currentPhase = currentPhase.nextPhase();
        }
    }

    public int scoreForLaneEntry(int previousLaneIndex, int currentLaneIndex) {
        if (!isEnteringRoadAtControlledLane(previousLaneIndex, currentLaneIndex)) {
            return 0;
        }

        return currentPhase.getLaneEntryScoreDelta(this);
    }

    public void reset() {
        currentPhase = RedPhase.INSTANCE;
        elapsedInPhase = 0f;
    }

    public boolean isRed() {
        return currentPhase == RedPhase.INSTANCE;
    }

    public boolean isGreen() {
        return currentPhase == GreenPhase.INSTANCE;
    }

    public String getCurrentPhaseName() {
        return currentPhase.getName();
    }

    public int getControlledLaneIndex() {
        return controlledLaneIndex;
    }

    public float getSwitchInterval() {
        return switchInterval;
    }

    public int getRedLaneEntryScoreDelta() {
        return redLaneEntryScoreDelta;
    }

    public int getGreenLaneEntryScoreDelta() {
        return greenLaneEntryScoreDelta;
    }

    private boolean isEnteringRoadAtControlledLane(int previousLaneIndex, int currentLaneIndex) {
        return previousLaneIndex == NO_LANE_INDEX
                && currentLaneIndex == controlledLaneIndex;
    }
}
