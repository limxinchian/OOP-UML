package com.mygdx.game.engine.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
            for (Entity entity : entitiesToRemove) {
                if (entity == null) continue;

                if (removeById(entities, entity.getId())) {
                    entity.clearComponents();
                }
            }
            entitiesToRemove.clear();
        }

        if (!entitiesToAdd.isEmpty()) {
            for (Entity entity : entitiesToAdd) {
                if (entity == null) continue;
                if (!containsEntityId(entities, entity.getId())) {
                    entities.add(entity);
                }
            }
            entitiesToAdd.clear();
        }
    }

    @Override
    public void shutdown() {
        // Detach components cleanly from all known entities.
        Set<UUID> seen = new HashSet<>();
        detachAllUnique(entities, seen);
        detachAllUnique(entitiesToAdd, seen);
        detachAllUnique(entitiesToRemove, seen);

        entities.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
    }

    public void addEntity(Entity entity) {
        if (entity == null) throw new IllegalArgumentException("entity cannot be null");

        UUID id = entity.getId();

        // Cancel a pending removal for the same entity (same frame re-add case).
        removeById(entitiesToRemove, id);

        if (containsEntityId(entities, id) || containsEntityId(entitiesToAdd, id)) {
            return; // prevent duplicates
        }

        entitiesToAdd.add(entity);
    }

    public void removeEntity(Entity entity) {
        if (entity == null) throw new IllegalArgumentException("entity cannot be null");

        UUID id = entity.getId();

        // If it was queued to add but not yet active, remove immediately and clean up.
        if (removeById(entitiesToAdd, id)) {
            entity.clearComponents();
            return;
        }

        if (!containsEntityId(entities, id)) {
            return; // nothing to remove
        }

        if (!containsEntityId(entitiesToRemove, id)) {
            entitiesToRemove.add(entity);
        }
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

    private static boolean containsEntityId(List<Entity> list, UUID id) {
        if (id == null) return false;
        for (Entity entity : list) {
            if (entity != null && id.equals(entity.getId())) {
                return true;
            }
        }
        return false;
    }

    private static boolean removeById(List<Entity> list, UUID id) {
        if (id == null) return false;
        for (int i = 0; i < list.size(); i++) {
            Entity entity = list.get(i);
            if (entity != null && id.equals(entity.getId())) {
                list.remove(i);
                return true;
            }
        }
        return false;
    }

    private static void detachAllUnique(List<Entity> list, Set<UUID> seen) {
        for (Entity entity : list) {
            if (entity == null) continue;
            if (seen.add(entity.getId())) {
                entity.clearComponents();
            }
        }
    }
}
