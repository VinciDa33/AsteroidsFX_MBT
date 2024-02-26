package dk.sdu.mmmi.cbse.collisionSystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

public class CollisionDetector implements IPostEntityProcessingService {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity entity1 : world.getEntities()) {
            for (Entity entity2 : world.getEntities()) {
                if (entity1.getID().equals(entity2.getID()))
                    continue;

                //!!!IMPORTANT!!! So this is a mess...
                if (entity1 instanceof Bullet && ((Bullet) entity1).getShooter().getID().equals(entity2.getID()))
                    continue;
                if (entity2 instanceof Bullet && ((Bullet) entity2).getShooter().getID().equals(entity1.getID()))
                    continue;

                if (this.checkCollision(entity1, entity2)) {
                    entity1.setDeletionFlag(true);
                    entity2.setDeletionFlag(true);
                }
            }
        }
    }

    private boolean checkCollision(Entity e1, Entity e2) {
        double distance = e1.getPosition().distance(e2.getPosition());
        return distance < e1.getRadius() + e2.getRadius();
    }
}
