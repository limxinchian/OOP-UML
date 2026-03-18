package com.mygdx.game.crossylane.entities.additional_entity;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.render.RenderableComponent;

/**
 * A traffic light that cycles between RED and GREEN states on a timer.
 *
 * Game scenes use getState() / isRed() to decide whether cars in the
 * associated lane should stop or move.
 *
 * Design note: tick() is called manually by the game scene so the engine
 * does not need to know about traffic-light logic.
 *
 * Components attached:
 *  - TransformComponent  : small rectangle at lane edge
 *  - RenderableComponent : red or green, updated each state change
 */
public class TrafficLightEntity extends Entity {

    public enum State { RED, GREEN }

    private State state;
    private float timer;
    private final float redDuration;
    private final float greenDuration;

    public TrafficLightEntity(float x, float y, float redDuration, float greenDuration) {
        this.state        = State.RED;
        this.redDuration  = redDuration;
        this.greenDuration = greenDuration;
        this.timer        = 0f;

        addComponent(new TransformComponent(x, y,
                CrossyLaneConfig.TRAFFIC_LIGHT_WIDTH,
                CrossyLaneConfig.TRAFFIC_LIGHT_HEIGHT));

        // Starts red
        addComponent(RenderableComponent.rectangle(1f, 0f, 0f, 1f));
    }

    /**
     * Advances the timer and flips state when the current duration expires.
     * Must be called each frame by the owning game scene.
     *
     * @param deltaTime seconds since last frame
     */
    public void tick(float deltaTime) {
        timer += deltaTime;
        float duration = (state == State.RED) ? redDuration : greenDuration;

        if (timer >= duration) {
            timer = 0f;
            state = (state == State.RED) ? State.GREEN : State.RED;
            applyColor();
        }
    }

    public State getState()  { return state; }
    public boolean isRed()   { return state == State.RED; }
    public boolean isGreen() { return state == State.GREEN; }

    /** Updates the RenderableComponent colour to match the current state. */
    private void applyColor() {
        RenderableComponent rc = getComponent(RenderableComponent.class);
        if (rc == null) return;

        if (state == State.RED) {
            rc.setColor(1f, 0f, 0f, 1f);
        } else {
            rc.setColor(0f, 1f, 0f, 1f);
        }
    }
}
