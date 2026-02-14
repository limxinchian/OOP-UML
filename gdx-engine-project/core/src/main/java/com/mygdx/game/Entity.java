package com.mygdx.game;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entity {
    private UUID id;
    private boolean active;
    private Map<Class<? extends Component>, Component> components;

    public Entity() {
        this.id = UUID.randomUUID();
        this.active = true;
        this.components = new HashMap<>();
    }

    public void addComponent(Component component) {
        component.onAttach(this);
        components.put(component.getClass(), component);
    }

    public void removeComponent(Class<? extends Component> type) {
        if (components.containsKey(type)) {
            components.get(type).onDetach();
            components.remove(type);
        }
    }

    public Component getComponent(Class<? extends Component> type) {
        return components.get(type);
    }

    public void setActive(boolean active) { this.active = active; }
    public boolean isActive() { return active; }
}