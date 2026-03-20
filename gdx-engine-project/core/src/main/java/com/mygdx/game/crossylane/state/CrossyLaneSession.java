package com.mygdx.game.crossylane.state;

public class CrossyLaneSession {
    private boolean playerWon = false;

    public void setPlayerWon(boolean playerWon) {
        this.playerWon = playerWon;
    }

    public boolean hasPlayerWon() {
        return playerWon;
    }
}