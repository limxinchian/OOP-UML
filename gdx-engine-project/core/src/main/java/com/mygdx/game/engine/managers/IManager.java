package com.mygdx.game.engine.managers;

public interface IManager {
    void initialize();
    void update(float deltaTime);
    void shutdown();
}
