package com.mygdx.game.crossylane.traffic;

/**
 * State interface for the traffic-light controller.
 * Each phase defines its own duration, score outcome, and next phase.
 */
public interface TrafficLightPhase {
    String getName();

    float getDuration(TrafficLightController controller);

    int getLaneEntryScoreDelta(TrafficLightController controller);

    TrafficLightPhase nextPhase();
}
