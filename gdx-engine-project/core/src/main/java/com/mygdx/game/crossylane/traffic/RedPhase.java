package com.mygdx.game.crossylane.traffic;

public final class RedPhase implements TrafficLightPhase {
    static final RedPhase INSTANCE = new RedPhase();

    private RedPhase() {
    }

    @Override
    public String getName() {
        return "RED";
    }

    @Override
    public float getDuration(TrafficLightController controller) {
        return controller.getSwitchInterval();
    }

    @Override
    public int getLaneEntryScoreDelta(TrafficLightController controller) {
        return controller.getRedLaneEntryScoreDelta();
    }

    @Override
    public TrafficLightPhase nextPhase() {
        return GreenPhase.INSTANCE;
    }
}
