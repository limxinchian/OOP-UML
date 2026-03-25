package com.mygdx.game.crossylane.entities.additional_entity;

import com.mygdx.game.engine.ecs.Entity;
import com.mygdx.game.engine.ecs.TransformComponent;
import com.mygdx.game.engine.render.TextureComponent;

/**
 * A textured traffic light placed beside a lane.
 * Traffic-light behavior lives in the controller, while this entity only handles rendering.
 */
public class TrafficLightEntity extends Entity {

    public TrafficLightEntity(float x, float y, float width, float height) {
        addComponent(new TransformComponent(x, y, width, height));
        addComponent(new TextureComponent("traffic_light.png"));
    }
}
