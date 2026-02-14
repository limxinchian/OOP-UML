package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
        
        entities.removeAll(entitiesToRemove);
        entitiesToRemove.clear();

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
