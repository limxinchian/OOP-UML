package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private Map<String, Sound> sounds = new HashMap<>();

    public void loadSound(String id, String path) {
        sounds.put(id, Gdx.audio.newSound(Gdx.files.internal(path)));
    }

    public void playSound(String id) {
        if (sounds.containsKey(id)) {
            sounds.get(id).play();
        }
    }

    public void stopSound(String id) {
        if (sounds.containsKey(id)) {
            sounds.get(id).stop();
        }
    }

    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
    }
}