package dk.sdu.mmmi.cbse.playersystem;

import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.Collection;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;


public class PlayerControlSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {

        for (Entity player : world.getEntities(Player.class)) {

            //Tick firing cooldown
            ((Player) player).tickFireTimer(gameData.getDeltaSec());

            //Controls
            //Rotate the velocity vector based on player input
            if (gameData.getKeys().isDown(GameKeys.LEFT)) {
                player.getVelocity().rotate(-player.getRotationSpeed() * gameData.getDeltaSec());
            }
            if (gameData.getKeys().isDown(GameKeys.RIGHT)) {
                player.getVelocity().rotate(player.getRotationSpeed() * gameData.getDeltaSec());
            }
            //Use velocity vector to change position
            if (gameData.getKeys().isDown(GameKeys.UP)) {
                player.getPosition().add(player.getVelocity().multiplied(player.getSpeed() * gameData.getDeltaSec()));
            }

            //Set graphical rotation to match the direction of the velocity vector
            player.setRotation(player.getVelocity().toAngle());


            if (gameData.getKeys().isDown(GameKeys.SPACE) && ((Player) player).canFire()) {
                getBulletSPIs().stream().findFirst().ifPresent(
                        spi -> world.addEntity(spi.createBullet(player, gameData, 240, 3))
                );
            }

            //World border collision
            if (player.getPosition().x < 0) {
                player.getPosition().x = 0;
            }

            if (player.getPosition().x > gameData.getDisplaySize().x) {
                player.getPosition().x = gameData.getDisplaySize().x - 1;
            }

            if (player.getPosition().y < 0) {
                player.getPosition().y = 0;
            }

            if (player.getPosition().y > gameData.getDisplaySize().y) {
                player.getPosition().y = gameData.getDisplaySize().y - 1;
            }


        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
