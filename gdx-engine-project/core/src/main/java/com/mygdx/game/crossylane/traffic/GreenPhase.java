package com.mygdx.game.crossylane.traffic;

public final class GreenPhase implements TrafficLightPhase {
    static final GreenPhase INSTANCE = new GreenPhase();

    private GreenPhase() {
    }

    @Override
    public String getName() {
        return "GREEN";
    }

    @Override
    public float getDuration(TrafficLightController controller) {
        return controller.getSwitchInterval();
    }

    @Override
    public int getLaneEntryScoreDelta(TrafficLightController controller) {
        return controller.getGreenLaneEntryScoreDelta();
    }

    @Override
    public TrafficLightPhase nextPhase() {
        return RedPhase.INSTANCE;
    }
}
