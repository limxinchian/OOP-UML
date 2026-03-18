package com.mygdx.game.engine.io;

import com.mygdx.game.engine.ecs.Entity;

@FunctionalInterface
public interface Command {
    void execute(Entity entity, float deltaTime);
}
