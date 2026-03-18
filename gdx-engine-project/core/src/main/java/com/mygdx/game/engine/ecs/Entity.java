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

        Entity currentOwner = component.getOwner();
        if (currentOwner != null && currentOwner != this) {
            throw new IllegalStateException("component is already attached to another entity");
        }

        Component existing = components.get(component.getClass());
        if (existing == component) {
            return; // same instance already attached
        }

        if (existing != null) {
            existing.onDetach();
        }

        component.onAttach(this);
        components.put(component.getClass(), component);
    }

    public void removeComponent(Class<? extends Component> type) {
        if (type == null) return;

        Component removed = components.remove(type);
        if (removed != null) {
            removed.onDetach();
        }
    }

    public void clearComponents() {
        for (Component component : components.values()) {
            if (component != null) {
                component.onDetach();
            }
        }
        components.clear();
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return getComponent(type) != null;
    }

    public <T extends Component> T getComponent(Class<T> type) {
        if (type == null) return null;

        // Fast path: exact type
        Component exact = components.get(type);
        if (exact != null) {
            return type.cast(exact);
        }

        // Polymorphic fallback: allow querying by superclass / interface
        for (Component c : components.values()) {
            if (type.isInstance(c)) {
                return type.cast(c);
            }
        }

        return null;
    }

    public void setActive(boolean active) { this.active = active; }
    public boolean isActive() { return active; }
}
