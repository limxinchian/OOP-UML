package com.mygdx.game.engine.audio;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    private final Map<String, Sound> soundCache = new HashMap<>();
    private final Map<String, Music> musicCache = new HashMap<>();

    private Music currentMusic;
    private String currentMusicPath;

    private float masterVolume = 1.0f;
    private float musicVolume = 0.7f;
    private float sfxVolume = 1.0f;

    public void playSound(String path) {
        playSound(path, sfxVolume * masterVolume);
    }

    public void playSound(String path, float volume) {
        Sound sound = getOrLoadSound(path);
        if (sound != null) {
            sound.play(clamp01(volume));
        }
    }

    public void playMusic(String path, boolean looping) {
        if (path == null) {
            stopMusic();
            return;
        }

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

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
            currentMusicPath = null;
        }
    }

    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
        }
    }

    public boolean isMusicPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = clamp01(volume);
        applyMusicVolume();
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = clamp01(volume);
        applyMusicVolume();
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = clamp01(volume);
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    private void applyMusicVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(clamp01(musicVolume * masterVolume));
        }
    }

    // NEW: preload short SFX once so first playback has no lag
    public void preloadSound(String path) {
        getOrLoadSound(path);
    }

    // NEW: optional preload for music too
    public void preloadMusic(String path) {
        getOrLoadMusic(path);
    }

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