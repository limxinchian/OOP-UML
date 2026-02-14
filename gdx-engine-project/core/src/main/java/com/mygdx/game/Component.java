package com.mygdx.game;

public abstract class Component {
    protected Entity owner;
    protected boolean enabled = true;

    public void onAttach(Entity entity) { this.owner = entity; }
    public void onDetach() { this.owner = null; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEnabled() { return enabled; }
    
    public abstract void update(float deltaTime);
}
