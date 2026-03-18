package com.mygdx.game.engine.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.mygdx.game.engine.ecs.Component;

/**
 * Generic input mapping component.
 * Demo/game logic is expressed via Commands (which may be lambdas).
 */
public class InputComponent extends Component {

    private final Map<Integer, Command> holdBindings = new HashMap<>();
    private final Map<Integer, Command> pressBindings = new HashMap<>();

    public void bindHold(int keyCode, Command command) {
        if (command == null) throw new IllegalArgumentException("command cannot be null");
        holdBindings.put(keyCode, command);
    }

    public void bindJustPressed(int keyCode, Command command) {
        if (command == null) throw new IllegalArgumentException("command cannot be null");
        pressBindings.put(keyCode, command);
    }

    Map<Integer, Command> getHoldBindings() {
        return Collections.unmodifiableMap(holdBindings);
    }

    Map<Integer, Command> getPressBindings() {
        return Collections.unmodifiableMap(pressBindings);
    }

    @Override
    public void update(float deltaTime) {
        // Input is processed by InputManager.
    }
}
