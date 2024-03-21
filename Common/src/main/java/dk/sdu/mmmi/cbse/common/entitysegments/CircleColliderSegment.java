package dk.sdu.mmmi.cbse.common.entitysegments;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.EntityTag;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CircleColliderSegment implements EntitySegment{
    private double radius;
    private final Map<EntityTag,CollisionEvent> collisionMap = new ConcurrentHashMap<>();
    public void setRadius(double radius) {
        this.radius = radius;
    }
    public double getRadius() {
        return radius;
    }

    public void addCollisionEvent(EntityTag key, CollisionEvent event) {
        collisionMap.put(key, event);
    }
    public void removeCollisionEvent(EntityTag key) {
        collisionMap.remove(key);
    }

    @Override
    public void process(GameData gameData, Entity entity) {

    }
    public void doCollision(Entity other) {
        if (collisionMap.get(other.getTag()) != null)
            collisionMap.get(other.getTag()).onCollision(other);
    }
}
