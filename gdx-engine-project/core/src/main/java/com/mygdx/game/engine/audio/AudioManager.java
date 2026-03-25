package com.mygdx.game.engine.audio;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Engine-level audio manager that loads, caches, and controls audio playback.
 *
 * Manages two asset types:
 *   - {@link Sound} : short-lived effects (coins, hits, beeps).
 *                     Fully loaded into memory, low-latency playback.
 *   - {@link Music} : long-running background tracks, streamed from disk.
 *                     Only one Music track plays at a time (via {@link #playMusic}).
 *
 * Architecture note:
 * AudioManager lives in the engine's audio package and has no knowledge of
 * any specific game.  Game code obtains a reference through IOManager and
 * calls play/stop/pause with logical asset paths.  All assets are loaded on
 * first use and cached for the lifetime of the manager.
 *
 * If an audio file is missing, methods log a warning and return silently
 * rather than crashing, so the game remains playable without audio assets.
 */
public class AudioManager {

    private final Map<String, Sound> soundCache = new HashMap<>();
    private final Map<String, Music> musicCache = new HashMap<>();

    private Music currentMusic;
    private String currentMusicPath;

    private float masterVolume = 1.0f;
    private float musicVolume = 0.7f;
    private float sfxVolume = 1.0f;

    // -----------------------------------------------------------------------
    // Sound effects (short, cached in memory)
    // -----------------------------------------------------------------------

    /**
     * Plays a sound effect once at the current SFX volume.
     *
     * @param path asset path relative to the assets folder (e.g. "sfx_coin.wav")
     */
    public void playSound(String path) {
        playSound(path, sfxVolume * masterVolume);
    }

    /**
     * Plays a sound effect once at a specific volume.
     *
     * @param path   asset path
     * @param volume 0.0 (silent) to 1.0 (full)
     */
    public void playSound(String path, float volume) {
        Sound sound = getOrLoadSound(path);
        if (sound != null) {
            sound.play(clamp01(volume));
        }
    }

    // -----------------------------------------------------------------------
    // Music (streamed, one track at a time)
    // -----------------------------------------------------------------------

    /**
     * Starts playing a music track, replacing any currently playing music.
     * If the requested track is already playing, this is a no-op.
     *
     * @param path    asset path (e.g. "music_menu.wav")
     * @param looping true to loop the track continuously
     */
    public void playMusic(String path, boolean looping) {
        if (path == null) {
            stopMusic();
            return;
        }

        // Already playing this track — don't restart
        if (path.equals(currentMusicPath) && currentMusic != null && currentMusic.isPlaying()) {
            return;
        }

        stopMusic();

        Music music = getOrLoadMusic(path);
        if (music == null) return;

        music.setVolume(clamp01(musicVolume * masterVolume));
        music.setLooping(looping);
        music.play();

        currentMusic = music;
        currentMusicPath = path;
    }

    /**
     * Stops the currently playing music track.
     */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
            currentMusicPath = null;
        }
    }

    /**
     * Pauses the currently playing music. Resumes from the same position
     * when {@link #resumeMusic()} is called.
     */
    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

    /**
     * Resumes music that was previously paused.
     */
    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
        }
    }

    /**
     * @return true if a music track is currently playing.
     */
    public boolean isMusicPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    // -----------------------------------------------------------------------
    // Volume controls
    // -----------------------------------------------------------------------

    /** Master volume multiplier (0.0 – 1.0). Affects both music and SFX. */
    public void setMasterVolume(float volume) {
        this.masterVolume = clamp01(volume);
        applyMusicVolume();
    }

    public float getMasterVolume() { return masterVolume; }

    /** Music volume (0.0 – 1.0), multiplied by master. */
    public void setMusicVolume(float volume) {
        this.musicVolume = clamp01(volume);
        applyMusicVolume();
    }

    public float getMusicVolume() { return musicVolume; }

    /** Sound effect volume (0.0 – 1.0), multiplied by master. */
    public void setSfxVolume(float volume) {
        this.sfxVolume = clamp01(volume);
    }

    public float getSfxVolume() { return sfxVolume; }

    private void applyMusicVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(clamp01(musicVolume * masterVolume));
        }
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    /**
     * Disposes all cached audio assets. Called by IOManager on engine shutdown.
     */
    public void dispose() {
        stopMusic();

        for (Sound s : soundCache.values()) {
            if (s != null) s.dispose();
        }
        soundCache.clear();

        for (Music m : musicCache.values()) {
            if (m != null) m.dispose();
        }
        musicCache.clear();
    }

    // -----------------------------------------------------------------------
    // Internal asset loading (lazy, cached)
    // -----------------------------------------------------------------------

    private Sound getOrLoadSound(String path) {
        if (path == null) return null;

        Sound cached = soundCache.get(path);
        if (cached != null) return cached;

        try {
            if (!Gdx.files.internal(path).exists()) {
                Gdx.app.log("AudioManager", "Sound file not found: " + path);
                return null;
            }
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            soundCache.put(path, sound);
            return sound;
        } catch (Exception e) {
            Gdx.app.error("AudioManager", "Failed to load sound: " + path, e);
            return null;
        }
    }

    private Music getOrLoadMusic(String path) {
        if (path == null) return null;

        Music cached = musicCache.get(path);
        if (cached != null) return cached;

        try {
            if (!Gdx.files.internal(path).exists()) {
                Gdx.app.log("AudioManager", "Music file not found: " + path);
                return null;
            }
            Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
            musicCache.put(path, music);
            return music;
        } catch (Exception e) {
            Gdx.app.error("AudioManager", "Failed to load music: " + path, e);
            return null;
        }
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}
