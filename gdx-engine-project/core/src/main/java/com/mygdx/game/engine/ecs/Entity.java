package com.mygdx.game.engine.ecs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entity {
    private final UUID id;
    private boolean active;
    private final Map<Class<? extends Component>, Component> components;

    public Entity() {
        this.id = UUID.randomUUID();
        this.active = true;
        this.components = new HashMap<>();
    }

    public UUID getId() {
        return id;
    }

    public void addComponent(Component component) {
        if (component == null) throw new IllegalArgumentException("component cannot be null");
        component.onAttach(this);
        components.put(component.getClass(), component);
    }

    public void removeComponent(Class<? extends Component> type) {
        if (components.containsKey(type)) {
            components.get(type).onDetach();
            components.remove(type);
        }
    }

    public <T extends Component> T getComponent(Class<T> type) {
        Component c = components.get(type);
        if (c == null) return null;
        return type.cast(c);
    }

    public void setActive(boolean active) { this.active = active; }
    public boolean isActive() { return active; }
}
