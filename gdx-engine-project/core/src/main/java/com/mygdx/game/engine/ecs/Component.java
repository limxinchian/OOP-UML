package com.mygdx.game.engine.ecs;

public abstract class Component {
    protected Entity owner;
    protected boolean enabled = true;

    public void onAttach(Entity entity) { this.owner = entity; }
    public void onDetach() { this.owner = null; }

    public Entity getOwner() { return owner; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEnabled() { return enabled; }

    public void update(float deltaTime) {
    // Default no-op: component updates are manager-driven.
    // Subclasses may override if self-update logic is needed.
}
}
