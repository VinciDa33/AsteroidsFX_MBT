package dk.sdu.mmmi.cbse.enemysystem;


import dk.sdu.mmmi.cbse.common.bullet.BulletParams;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.OnScreenSegment;
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

        int maxEnemyCount = Math.min(1 + (int) Math.floor(gameData.getTime() / 35d), 4);
        if (world.getEntities(Enemy.class).size() < maxEnemyCount) {
            Enemy enemy = new EnemyFactory().createEnemy(gameData, world);
            world.addEntity(enemy);
        }

        for (Entity enemy : world.getEntities(Enemy.class)) {

            RigidbodySegment rigidbody = enemy.getSegment(RigidbodySegment.class);
            ShootingSegment shooting = enemy.getSegment(ShootingSegment.class);
            OnScreenSegment oss = enemy.getSegment(OnScreenSegment.class);

            //Movement
            //Find player entity to track
            Entity player = null;
            List<Entity> playerEntities = world.getEntitiesWithTag(EntityTag.PLAYER);
            if (!playerEntities.isEmpty())
                player = playerEntities.getFirst();

            //Rotate towards player
            if (player != null) {
                RigidbodySegment playerRigidbody = player.getSegment(RigidbodySegment.class);
                double dot = Vector.dot(rigidbody.getVelocity().rotated(90d).normalized(), playerRigidbody.getPosition().subtracted(rigidbody.getPosition()));
                if (dot < 0)
                    rigidbody.rotate(gameData, -1);
                else
                    rigidbody.rotate(gameData, 1);
            }

            //Modify the position using the velocity vector, applying both speed and delta time
            rigidbody.process(gameData, enemy);

            //Process shooting timer
            shooting.process(gameData, enemy);

            //Fire bullets
            if (oss.isOnScreen() && shooting.canFire()) {
                getBulletSPIs().stream().findFirst().ifPresent(
                        spi -> world.addEntity(spi.createBullet(enemy, gameData, world, new BulletParams(rigidbody.getVelocity(),160d, 4d)))
                );
            }
        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
