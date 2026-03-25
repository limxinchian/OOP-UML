package com.mygdx.game.crossylane.config;

/**
 * Immutable data describing one traffic light and the lane it controls.
 *
 * Scalability note:
 * Adding a traffic light to a lane = adding a TrafficLightDefinition to the
 * level's list.  No scene, entity, or system code needs modification.
 */
public class TrafficLightDefinition {

    private final int controlledLaneIndex;
    private final float switchInterval;
    private final int redScoreDelta;
    private final int greenScoreDelta;

    /**
     * @param controlledLaneIndex 0-based lane this light governs
     * @param switchInterval      seconds per phase (red / green)
     * @param redScoreDelta       score change for entering on red (typically negative)
     * @param greenScoreDelta     score change for entering on green (typically positive)
     */
    public TrafficLightDefinition(int controlledLaneIndex, float switchInterval,
                                  int redScoreDelta, int greenScoreDelta) {
        if (controlledLaneIndex < 0) throw new IllegalArgumentException("controlledLaneIndex must be >= 0");
        if (switchInterval <= 0f) throw new IllegalArgumentException("switchInterval must be > 0");

        this.controlledLaneIndex = controlledLaneIndex;
        this.switchInterval = switchInterval;
        this.redScoreDelta = redScoreDelta;
        this.greenScoreDelta = greenScoreDelta;
    }

    public int getControlledLaneIndex() { return controlledLaneIndex; }
    public float getSwitchInterval() { return switchInterval; }
    public int getRedScoreDelta() { return redScoreDelta; }
    public int getGreenScoreDelta() { return greenScoreDelta; }
}
