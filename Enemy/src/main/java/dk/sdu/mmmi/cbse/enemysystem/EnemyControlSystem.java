package dk.sdu.mmmi.cbse.enemysystem;


import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.RigidbodySegment;
import dk.sdu.mmmi.cbse.common.entitysegments.ShootingSegment;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;


public class EnemyControlSystem implements IEntityProcessingService {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity enemy : world.getEntities(Enemy.class)) {

            RigidbodySegment rigidbody = enemy.getSegment(RigidbodySegment.class);
            ShootingSegment shooting = enemy.getSegment(ShootingSegment.class);

            //Movement
            //Find player entity to track
            Entity player = null;
            List<Entity> playerEntities = world.getEntitiesWithTag(EntityTag.PLAYER);
            if (!playerEntities.isEmpty())
                player = playerEntities.get(0);

            //Rotate towards player
            if (player != null) {
                RigidbodySegment playerRigidbody = player.getSegment(RigidbodySegment.class);
                if (Vector.dot(rigidbody.getPosition(), playerRigidbody.getPosition()) < 0)
                    rigidbody.rotate(gameData, -1);
                else
                    rigidbody.rotate(gameData, 1);
            }

            //Modify the position using the velocity vector, applying both speed and delta time
            rigidbody.process(gameData, enemy);

            //Process shooting timer
            shooting.process(gameData, enemy);

            //Fire bullets
            if (shooting.canFire()) {
                getBulletSPIs().stream().findFirst().ifPresent(
                        spi -> world.addEntity(spi.createBullet(enemy, gameData, 160, 4f))
                );
            }

            //World border collision
            if (rigidbody.getPosition().x < 0) {
                rigidbody.getPosition().x = 0;
            }

            if (rigidbody.getPosition().x > gameData.getDisplaySize().x) {
                rigidbody.getPosition().x = gameData.getDisplaySize().x - 1;
            }

            if (rigidbody.getPosition().y < 0) {
                rigidbody.getPosition().y = 0;
            }

            if (rigidbody.getPosition().y > gameData.getDisplaySize().y) {
                rigidbody.getPosition().y = gameData.getDisplaySize().y - 1;
            }
        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
