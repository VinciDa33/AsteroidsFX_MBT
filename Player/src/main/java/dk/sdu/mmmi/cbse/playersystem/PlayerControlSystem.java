package dk.sdu.mmmi.cbse.playersystem;

import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.RigidbodySegment;
import dk.sdu.mmmi.cbse.common.entitysegments.ShootingSegment;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.Collection;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;


public class PlayerControlSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {

        for (Entity player : world.getEntities(Player.class)) {

            RigidbodySegment rigidbody = player.getSegment(RigidbodySegment.class);
            ShootingSegment shooting = player.getSegment(ShootingSegment.class);


            //Controls
            //Rotate the velocity vector based on player input
            if (gameData.getKeys().isDown(GameKeys.LEFT)) {
                rigidbody.rotate(gameData, -1);
            }
            if (gameData.getKeys().isDown(GameKeys.RIGHT)) {
                rigidbody.rotate(gameData, 1);
            }
            //Use velocity vector to change position
            if (gameData.getKeys().isDown(GameKeys.UP)) {
                rigidbody.process(gameData, player);
            }

            //Process shooting timer
            shooting.process(gameData, player);

            if (gameData.getKeys().isDown(GameKeys.SPACE) && shooting.canFire()) {
                getBulletSPIs().stream().findFirst().ifPresent(
                        spi -> world.addEntity(spi.createBullet(player, gameData, 240, 3))
                );
            }

            //World border collision
            if (rigidbody.getPosition().x < 0) {
                rigidbody.getPosition().x = 0;
                rigidbody.getVelocity().x *= -1;
            }

            if (rigidbody.getPosition().x > gameData.getDisplaySize().x) {
                rigidbody.getPosition().x = gameData.getDisplaySize().x - 1;
                rigidbody.getVelocity().x *= -1;
            }

            if (rigidbody.getPosition().y < 0) {
                rigidbody.getPosition().y = 0;
                rigidbody.getVelocity().y *= -1;
            }

            if (rigidbody.getPosition().y > gameData.getDisplaySize().y) {
                rigidbody.getPosition().y = gameData.getDisplaySize().y - 1;
                rigidbody.getVelocity().y *= -1;
            }


        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
