package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class CollisionManager implements IManager {
    private List<CollisionComponent> collisionComponents;

    public CollisionManager() {
        this.collisionComponents = new ArrayList<>();
    }

    @Override
    public void initialize() { }

    @Override
    public void update(float deltaTime) {
        detectCollisions();
    }

    @Override
    public void shutdown() {
        collisionComponents.clear();
    }

    public void detectCollisions() {
        // Check every pair of collision components
        for (int i = 0; i < collisionComponents.size(); i++) {
            for (int j = i + 1; j < collisionComponents.size(); j++) {
                CollisionComponent a = collisionComponents.get(i);
                CollisionComponent b = collisionComponents.get(j);
                
                if (canCollide(a, b) && checkCollision(a, b)) {
                    handleCollision(a, b);
                }
            }
        }
    }

    public boolean checkCollision(CollisionComponent a, CollisionComponent b) {
        if (a.getBounds() == null || b.getBounds() == null) {
            return false;
        }
        return a.getBounds().overlaps(b.getBounds());
    }

    protected void handleCollision(CollisionComponent a, CollisionComponent b) {
        a.onCollisionEnter(b);
        b.onCollisionEnter(a);
    }

    public boolean canCollide(CollisionComponent a, CollisionComponent b) {
        return a.canCollideWith(b);
    }

    public void addCollisionComponent(CollisionComponent component) {
        collisionComponents.add(component);
    }

    public void removeCollisionComponent(CollisionComponent component) {
        collisionComponents.remove(component);
    }
}
