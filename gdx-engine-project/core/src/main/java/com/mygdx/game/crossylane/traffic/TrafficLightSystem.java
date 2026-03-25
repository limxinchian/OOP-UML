package com.mygdx.game.crossylane.traffic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.crossylane.config.TrafficLightDefinition;
import com.mygdx.game.crossylane.entities.EntityFactory;
import com.mygdx.game.crossylane.entities.additional_entity.TrafficLightEntity;
import com.mygdx.game.crossylane.events.TrafficLightChangedEvent;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.event.EventBus;
import com.mygdx.game.engine.managers.EntityManager;

/**
 * Manages all per-lane traffic lights for the current level.
 *
 * Phase 6 changes:
 * - Accepts an optional EventBus. When provided, publishes a
 *   TrafficLightChangedEvent every time any light changes phase,
 *   enabling the AudioController to play the appropriate SFX.
 * - Tracks previous phase state per controller to detect transitions.
 */
public class TrafficLightSystem {

    /** Pairs a controller with its visual entity. */
    public static final class LaneTrafficLight {
        private final TrafficLightController controller;
        private final TrafficLightEntity entity;

        LaneTrafficLight(TrafficLightController controller, TrafficLightEntity entity) {
            this.controller = controller;
            this.entity = entity;
        }

        public TrafficLightController getController() { return controller; }
        public TrafficLightEntity getEntity() { return entity; }
    }

    private final List<LaneTrafficLight> lights = new ArrayList<>();
    /** Tracks whether each controller was green before tick(), to detect transitions. */
    private final Map<TrafficLightController, Boolean> previousGreenState = new HashMap<>();
    private EventBus eventBus;

    private static final float INDICATOR_WIDTH  = 52f;
    private static final float INDICATOR_HEIGHT = 20f;
    private static final float INDICATOR_PADDING = 4f;

    /**
     * Builds all traffic lights from the level's definitions.
     *
     * @param definitions level config entries
     * @param entityManager for registering visual entities
     * @param eventBus optional — if non-null, phase changes are published
     */
    public void initialize(List<TrafficLightDefinition> definitions,
                           EntityManager entityManager, EventBus eventBus) {
        clear(entityManager);
        this.eventBus = eventBus;

        if (definitions == null || definitions.isEmpty()) return;

        for (TrafficLightDefinition def : definitions) {
            TrafficLightController controller = new TrafficLightController(
                    def.getControlledLaneIndex(),
                    def.getSwitchInterval(),
                    def.getRedScoreDelta(),
                    def.getGreenScoreDelta());

            TrafficLightEntity entity = EntityFactory.createLaneTrafficLight(def.getControlledLaneIndex());
            entityManager.addEntity(entity);

            lights.add(new LaneTrafficLight(controller, entity));
            previousGreenState.put(controller, controller.isGreen());
        }
    }

    /** Advances all controllers and publishes events for any phase transitions. */
    public void tick(float delta) {
        for (LaneTrafficLight lt : lights) {
            boolean wasPreviouslyGreen = Boolean.TRUE.equals(previousGreenState.get(lt.controller));

            lt.controller.tick(delta);

            boolean isNowGreen = lt.controller.isGreen();

            // Detect phase transition
            if (isNowGreen != wasPreviouslyGreen && eventBus != null) {
                eventBus.publish(new TrafficLightChangedEvent(
                        lt.controller.getControlledLaneIndex(), isNowGreen));
            }

            previousGreenState.put(lt.controller, isNowGreen);
        }
    }

    /**
     * Returns the total score delta for a player lane transition.
     */
    public int scoreForLaneEntry(int previousLaneIndex, int currentLaneIndex) {
        int total = 0;
        for (LaneTrafficLight lt : lights) {
            total += lt.controller.scoreForLaneEntry(previousLaneIndex, currentLaneIndex);
        }
        return total;
    }

    /** Renders a coloured indicator + phase label beside each controlled lane. */
    public void renderIndicators(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch,
                                  BitmapFont font, GlyphLayout glyphLayout) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (LaneTrafficLight lt : lights) {
            TransformComponent t = lt.entity.getComponent(TransformComponent.class);
            if (t == null) continue;

            float ix = t.getPositionX() + t.getWidth() + INDICATOR_PADDING;
            float iy = t.getPositionY() + (t.getHeight() - INDICATOR_HEIGHT) / 2f;

            if (lt.controller.isRed()) {
                shapeRenderer.setColor(0.85f, 0.15f, 0.15f, 0.9f);
            } else {
                shapeRenderer.setColor(0.15f, 0.75f, 0.15f, 0.9f);
            }
            shapeRenderer.rect(ix, iy, INDICATOR_WIDTH, INDICATOR_HEIGHT);
        }
        shapeRenderer.end();

        spriteBatch.begin();
        for (LaneTrafficLight lt : lights) {
            TransformComponent t = lt.entity.getComponent(TransformComponent.class);
            if (t == null) continue;

            float ix = t.getPositionX() + t.getWidth() + INDICATOR_PADDING;
            float iy = t.getPositionY() + (t.getHeight() - INDICATOR_HEIGHT) / 2f;

            String label = lt.controller.getCurrentPhaseName();
            glyphLayout.setText(font, label);
            font.setColor(1f, 1f, 1f, 1f);
            font.draw(spriteBatch, label,
                    ix + (INDICATOR_WIDTH - glyphLayout.width) / 2f,
                    iy + INDICATOR_HEIGHT - (INDICATOR_HEIGHT - glyphLayout.height) / 2f);
        }
        spriteBatch.end();
    }

    /** Removes all entities from the world and clears internal state. */
    public void clear(EntityManager entityManager) {
        for (LaneTrafficLight lt : lights) {
            entityManager.removeEntity(lt.entity);
        }
        lights.clear();
        previousGreenState.clear();
        eventBus = null;
    }

    public List<LaneTrafficLight> getLights() {
        return Collections.unmodifiableList(lights);
    }

    public boolean isEmpty() {
        return lights.isEmpty();
    }
}
