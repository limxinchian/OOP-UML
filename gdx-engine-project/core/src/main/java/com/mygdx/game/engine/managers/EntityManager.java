package com.mygdx.game.engine.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mygdx.game.engine.ecs.Component;
import com.mygdx.game.engine.ecs.Entity;

public class EntityManager implements IManager {
    private final List<Entity> entities;
    private final List<Entity> entitiesToAdd;
    private final List<Entity> entitiesToRemove;

    public EntityManager() {
        this.entities = new ArrayList<>();
        this.entitiesToAdd = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();
    }

    @Override
    public void initialize() { }

    @Override
    public void update(float deltaTime) {
        if (!entitiesToRemove.isEmpty()) {
            entities.removeAll(entitiesToRemove);
            entitiesToRemove.clear();
        }

        if (!entitiesToAdd.isEmpty()) {
            entities.addAll(entitiesToAdd);
            entitiesToAdd.clear();
        }
    }

    @Override
    public void shutdown() {
        entities.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
    }

    public void addEntity(Entity entity) {
        if (entity == null) throw new IllegalArgumentException("entity cannot be null");
        entitiesToAdd.add(entity);
    }

    public void removeEntity(Entity entity) {
        if (entity == null) throw new IllegalArgumentException("entity cannot be null");
        entitiesToRemove.add(entity);
    }

    public int getEntityCount() {
        return entities.size();
    }

    /** Read-only view. External code should not mutate the engine's entity list. */
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    /** Query helper: all active entities that contain ALL specified components. */
    @SafeVarargs
    public final List<Entity> getEntitiesWith(Class<? extends Component>... componentTypes) {
        if (componentTypes == null || componentTypes.length == 0) {
            return getEntities();
        }

        List<Entity> result = new ArrayList<>();
        outer:
        for (Entity e : entities) {
            if (!e.isActive()) continue;
            for (Class<? extends Component> type : componentTypes) {
                if (type == null) continue;
                if (e.getComponent(type) == null) continue outer;
            }
            result.add(e);
        }
        return result;
    }
}
