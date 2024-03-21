package dk.sdu.mmmi.cbse.common.events;

import dk.sdu.mmmi.cbse.common.data.Entity;

public interface CollisionEvent {
    void onCollision(Entity other);
}
