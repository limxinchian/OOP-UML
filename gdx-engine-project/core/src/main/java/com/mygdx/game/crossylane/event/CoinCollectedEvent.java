package com.mygdx.game.crossylane.event;

import com.mygdx.game.crossylane.entity.CoinEntity;
import com.mygdx.game.engine.event.GameEvent;

/**
 * Published when the player walks over a coin.
 * Subscribers handle score increment and any visual/audio feedback.
 */
public class CoinCollectedEvent implements GameEvent {

    private final CoinEntity coin;

    public CoinCollectedEvent(CoinEntity coin) {
        this.coin = coin;
    }

    public CoinEntity getCoin() {
        return coin;
    }
}
