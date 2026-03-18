package com.mygdx.game.engine.ecs;

public abstract class Component {
    protected Entity owner;
    protected boolean enabled = true;

    public void onAttach(Entity entity) { this.owner = entity; }
    public void onDetach() { this.owner = null; }

    public Entity getOwner() { return owner; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEnabled() { return enabled; }

    public abstract void update(float deltaTime);
}
