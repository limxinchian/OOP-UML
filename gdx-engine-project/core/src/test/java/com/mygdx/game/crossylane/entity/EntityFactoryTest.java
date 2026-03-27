package com.mygdx.game.crossylane.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.mygdx.game.crossylane.config.CrossyLaneConfig;
import com.mygdx.game.crossylane.layout.LaneLayout;
import com.mygdx.game.engine.entity.TransformComponent;

public class EntityFactoryTest {

    @Test
    public void createBottomGrassUsesConfiguredBounds() {
        GrassZoneEntity grassZone = EntityFactory.createBottomGrass();
        TransformComponent transform = grassZone.getComponent(TransformComponent.class);

        assertNotNull(transform);
        assertEquals(0f, transform.getPositionX(), 0.0001f);
        assertEquals(CrossyLaneConfig.BOTTOM_GRASS_Y, transform.getPositionY(), 0.0001f);
        assertEquals(CrossyLaneConfig.WORLD_WIDTH, transform.getWidth(), 0.0001f);
        assertEquals(CrossyLaneConfig.GRASS_ZONE_HEIGHT, transform.getHeight(), 0.0001f);
    }

    @Test
    public void createLaneMarkersCreatesOneMarkerPerLaneBoundary() {
        LaneLayout laneLayout = LaneLayout.forLaneCount(4);
        List<LaneMarkerEntity> markers = EntityFactory.createLaneMarkers(laneLayout);
        assertEquals(3, markers.size());

        for (LaneMarkerEntity marker : markers) {
            assertNotNull(marker.getComponent(TransformComponent.class));
        }
    }
}
