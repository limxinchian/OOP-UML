package com.mygdx.game.engine.ecs;

public abstract class Component {
    protected Entity owner;
    protected boolean enabled = true;

    public void onAttach(Entity entity) { this.owner = entity; }
    public void onDetach() { this.owner = null; }

    public Entity getOwner() { return owner; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEnabled() { return enabled; }

    /**
     * Called when the component is permanently removed from an entity or
     * the entity is destroyed.  Subclasses that hold native resources
     * (textures, sounds, etc.) should override this to release them.
     *
     * Addresses: OOP lifecycle management, resource safety.
     */
    public void dispose() { /* default no-op */ }

    public abstract void update(float deltaTime);
}
