package com.mygdx.game.crossylane.audio;

import com.mygdx.game.crossylane.events.CoinCollectedEvent;
import com.mygdx.game.crossylane.events.PlayerHitEvent;
import com.mygdx.game.crossylane.events.TrafficLightChangedEvent;
import com.mygdx.game.engine.audio.AudioManager;
import com.mygdx.game.engine.event.EventBus;
import com.mygdx.game.engine.event.EventListener;

public class CrossyLaneAudioController {

    private static final String SFX_COIN        = "sfx_coin.wav";
    private static final String SFX_LIFE_LOST   = "sfx_life_lost.wav";
    private static final String SFX_LIGHT_RED   = "sfx_light_red.wav";
    private static final String SFX_LIGHT_GREEN = "sfx_light_green.wav";

    private static final String MUSIC_MENU      = "music_menu.mp3";
    private static final String MUSIC_GAMEPLAY  = "music_gameplay.mp3";
    private static final String MUSIC_WIN       = "music_win.mp3";
    private static final String MUSIC_LOSE      = "music_lose.mp3";

    private final AudioManager audioManager;
    private final EventBus eventBus;

    private float masterVolume = 0.2f;

    private final EventListener<CoinCollectedEvent> onCoin;
    private final EventListener<PlayerHitEvent> onHit;
    private final EventListener<TrafficLightChangedEvent> onLightChange;

    public CrossyLaneAudioController(AudioManager audioManager, EventBus eventBus) {
        if (audioManager == null) throw new IllegalArgumentException("audioManager cannot be null");
        if (eventBus == null) throw new IllegalArgumentException("eventBus cannot be null");

        this.audioManager = audioManager;
        this.eventBus = eventBus;

        this.onCoin = event -> audioManager.playSound(SFX_COIN);
        this.onHit = event -> {
            // keep empty because hit sound is now played instantly in PlayerEntity
        };
        this.onLightChange = event -> audioManager.playSound(
                event.isNowGreen() ? SFX_LIGHT_GREEN : SFX_LIGHT_RED
        );

        audioManager.setMasterVolume(masterVolume);
        audioManager.setMusicVolume(1.0f);
        audioManager.setSfxVolume(1.0f);

        audioManager.preloadSound(SFX_COIN);
        audioManager.preloadSound(SFX_LIFE_LOST);
        audioManager.preloadSound(SFX_LIGHT_RED);
        audioManager.preloadSound(SFX_LIGHT_GREEN);
    }

    public void subscribe() {
        eventBus.subscribe(CoinCollectedEvent.class, onCoin);
        eventBus.subscribe(PlayerHitEvent.class, onHit);
        eventBus.subscribe(TrafficLightChangedEvent.class, onLightChange);
    }

    public void unsubscribe() {
        eventBus.unsubscribe(CoinCollectedEvent.class, onCoin);
        eventBus.unsubscribe(PlayerHitEvent.class, onHit);
        eventBus.unsubscribe(TrafficLightChangedEvent.class, onLightChange);
    }

    public void setVolume(float volume) {
        masterVolume = clamp01(volume);
        audioManager.setMasterVolume(masterVolume);
    }

    public float getVolume() {
        return masterVolume;
    }

    public void setMusicVolume(float volume) {
        audioManager.setMusicVolume(clamp01(volume));
    }

    public float getMusicVolume() {
        return audioManager.getMusicVolume();
    }

    public void setSfxVolume(float volume) {
        audioManager.setSfxVolume(clamp01(volume));
    }

    public float getSfxVolume() {
        return audioManager.getSfxVolume();
    }

    public void playMenuMusic() {
        audioManager.playMusic(MUSIC_MENU, true);
    }

    public void playGameplayMusic() {
        audioManager.playMusic(MUSIC_GAMEPLAY, true);
    }

    public void playWinMusic() {
        audioManager.playMusic(MUSIC_WIN, true);
    }

    public void playLoseMusic() {
        audioManager.playMusic(MUSIC_LOSE, true);
    }

    public void pauseMusic() {
        audioManager.pauseMusic();
    }

    public void resumeMusic() {
        audioManager.resumeMusic();
    }

    public void stopMusic() {
        audioManager.stopMusic();
    }

    // NEW: direct instant SFX trigger
    public void playHitSound() {
        audioManager.playSound(SFX_LIFE_LOST);
    }

    private float clamp01(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}