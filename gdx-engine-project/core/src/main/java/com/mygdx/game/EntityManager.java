package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class EntityManager implements IManager {
    private List<Entity> entities;
    private List<Entity> entitiesToAdd;
    private List<Entity> entitiesToRemove;

    public EntityManager() {
        this.entities = new ArrayList<>();
        this.entitiesToAdd = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();
    }

    // --- IManager ---
    @Override
    public void initialize() { }

    @Override
    public void update(float deltaTime) {
        // Process removals first
        entities.removeAll(entitiesToRemove);
        entitiesToRemove.clear();

        // Include additions
        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();
    }

    @Override
    public void shutdown() {
        entities.clear();
    }

    public void addEntity(Entity entity) {
        entitiesToAdd.add(entity);
    }

    public void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }

    public int getEntityCount() {
        return entities.size();
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
