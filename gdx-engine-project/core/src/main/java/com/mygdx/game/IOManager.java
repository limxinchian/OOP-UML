package com.mygdx.game;

public class IOManager implements IManager {

    private InputManager input;
    private OutputManager output;
    private AudioManager audio;

    public IOManager() {
        this.input = new InputManager();
        this.output = new OutputManager();
        this.audio = new AudioManager();
    }

    public InputManager getInput() {
        return input;
    }

    public OutputManager getOutput() {
        return output;
    }

    public AudioManager getAudio() {
        return audio;
    }

    @Override
    public void initialize() { }

    @Override
    public void update(float deltaTime) { }

    @Override
    public void shutdown() {
        output.dispose();
        audio.dispose();
    }
}