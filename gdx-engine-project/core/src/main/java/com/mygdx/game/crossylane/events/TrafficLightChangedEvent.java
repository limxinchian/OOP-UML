package com.mygdx.game.crossylane.events;

import com.mygdx.game.engine.event.GameEvent;

/**
 * Published when any traffic light changes phase.
 * Subscribers (e.g. AudioController) play the appropriate sound effect.
 */
public class TrafficLightChangedEvent implements GameEvent {

    private final int laneIndex;
    private final boolean isNowGreen;

    public TrafficLightChangedEvent(int laneIndex, boolean isNowGreen) {
        this.laneIndex = laneIndex;
        this.isNowGreen = isNowGreen;
    }

    public int getLaneIndex() { return laneIndex; }
    public boolean isNowGreen() { return isNowGreen; }
    public boolean isNowRed() { return !isNowGreen; }
}
