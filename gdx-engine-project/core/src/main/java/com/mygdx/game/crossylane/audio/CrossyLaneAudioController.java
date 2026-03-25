package com.mygdx.game.crossylane.audio;

import com.mygdx.game.crossylane.events.CoinCollectedEvent;
import com.mygdx.game.crossylane.events.PlayerHitEvent;
import com.mygdx.game.crossylane.events.TrafficLightChangedEvent;
import com.mygdx.game.engine.audio.AudioManager;
import com.mygdx.game.engine.event.EventBus;
import com.mygdx.game.engine.event.EventListener;

/**
 * Centralised audio controller for CrossyLane.
 *
 * All game audio — background music and sound effects — is routed through
 * this single class.  No scene or entity calls AudioManager directly;
 * instead they either:
 *   (a) publish an event (coin, hit, light change) that this controller
 *       listens to and plays the appropriate SFX, or
 *   (b) call one of the named music methods (playMenuMusic, etc.)
 *       which this controller translates into AudioManager calls.
 *
 * This satisfies the requirement that audio logic is not scattered across
 * game classes.  Adding a new sound effect means:
 *   1. Define a new event type (if needed)
 *   2. Subscribe here and call audioManager.playSound(path)
 *
 * Asset paths:
 *   SFX:   sfx_coin.wav, sfx_life_lost.wav, sfx_light_red.wav, sfx_light_green.wav
 *   Music: music_menu.wav, music_gameplay.wav, music_win.wav, music_lose.wav
 */
public class CrossyLaneAudioController {

    // -- Asset paths (all audio files in one place) -----------------------------
    private static final String SFX_COIN       = "sfx_coin.mp3";
    private static final String SFX_LIFE_LOST  = "sfx_life_lost.mp3";
    private static final String SFX_LIGHT_RED  = "sfx_light_red.wav";
    private static final String SFX_LIGHT_GREEN = "sfx_light_green.wav";

    private static final String MUSIC_MENU     = "music_menu.mp3";
    private static final String MUSIC_GAMEPLAY = "music_gameplay.mp3";
    private static final String MUSIC_WIN      = "music_win.mp3";
    private static final String MUSIC_LOSE     = "music_lose.mp3";

    // -- Dependencies -----------------------------------------------------------
    private final AudioManager audioManager;
    private final EventBus eventBus;

    // -- Event listeners (held as fields for clean unsubscribe) -----------------
    private final EventListener<CoinCollectedEvent> onCoin;
    private final EventListener<PlayerHitEvent> onHit;
    private final EventListener<TrafficLightChangedEvent> onLightChange;

    // -- Lifecycle --------------------------------------------------------------

    public CrossyLaneAudioController(AudioManager audioManager, EventBus eventBus) {
        if (audioManager == null) throw new IllegalArgumentException("audioManager cannot be null");
        if (eventBus == null) throw new IllegalArgumentException("eventBus cannot be null");

        this.audioManager = audioManager;
        this.eventBus = eventBus;

        // Listeners must be created here (not as field initializers) because
        // Java lambdas capture 'audioManager' and it must be definitely
        // assigned before the lambdas reference it.
        this.onCoin = event -> audioManager.playSound(SFX_COIN);
        this.onHit = event -> audioManager.playSound(SFX_LIFE_LOST);
        this.onLightChange = event -> audioManager.playSound(
                event.isNowGreen() ? SFX_LIGHT_GREEN : SFX_LIGHT_RED);
    }

    /**
     * Subscribes to all game events. Call once during application setup.
     */
    public void subscribe() {
        eventBus.subscribe(CoinCollectedEvent.class, onCoin);
        eventBus.subscribe(PlayerHitEvent.class, onHit);
        eventBus.subscribe(TrafficLightChangedEvent.class, onLightChange);
    }

    /**
     * Unsubscribes from all game events. Call during shutdown.
     */
    public void unsubscribe() {
        eventBus.unsubscribe(CoinCollectedEvent.class, onCoin);
        eventBus.unsubscribe(PlayerHitEvent.class, onHit);
        eventBus.unsubscribe(TrafficLightChangedEvent.class, onLightChange);
    }

    // -- Background music (called by scenes on enter) ---------------------------

    /** Plays the main menu background music (looping). */
    public void playMenuMusic() {
        audioManager.playMusic(MUSIC_MENU, true);
    }

    /** Plays the gameplay background music (looping). */
    public void playGameplayMusic() {
        audioManager.playMusic(MUSIC_GAMEPLAY, true);
    }

    /** Plays the win/level-complete music (looping). */
    public void playWinMusic() {
        audioManager.playMusic(MUSIC_WIN, true);
    }

    /** Plays the game-over/lose music (looping). */
    public void playLoseMusic() {
        audioManager.playMusic(MUSIC_LOSE, true);
    }

    /** Pauses the current background music (e.g. when pushing pause scene). */
    public void pauseMusic() {
        audioManager.pauseMusic();
    }

    /** Resumes background music (e.g. when popping pause scene). */
    public void resumeMusic() {
        audioManager.resumeMusic();
    }

    /** Stops all background music. */
    public void stopMusic() {
        audioManager.stopMusic();
    }
}
