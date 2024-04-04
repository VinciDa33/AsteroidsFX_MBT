package dk.sdu.mmmi.cbse.collisionSystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.entitysegments.CircleColliderSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.TransformSegment;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

public class CollisionDetector implements IPostEntityProcessingService {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity entity1 : world.getEntities()) {
            for (Entity entity2 : world.getEntities()) {

                TransformSegment trans1 = entity1.getSegment(TransformSegment.class);
                TransformSegment trans2 = entity2.getSegment(TransformSegment.class);

                CircleColliderSegment coll1 = entity1.getSegment(CircleColliderSegment.class);
                CircleColliderSegment coll2 = entity2.getSegment(CircleColliderSegment.class);

                if (entity1.getID().equals(entity2.getID()))
                    continue;
                if (trans1 == null || coll1 == null)
                    continue;
                if (trans2 == null || coll2 == null)
                    continue;

                if (this.checkCollision(trans1.getPosition(), coll1.getRadius(), trans2.getPosition(), coll2.getRadius())) {
                    coll1.doCollision(entity2);
                    coll2.doCollision(entity1);
                }
            }
        }
    }

    private boolean checkCollision(Vector pos1, double radius1, Vector pos2, double radius2) {
        double distance = Vector.distance(pos1, pos2);
        return distance < radius1 + radius2;
    }
}
