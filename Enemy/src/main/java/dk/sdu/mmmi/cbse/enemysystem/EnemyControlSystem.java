package dk.sdu.mmmi.cbse.enemysystem;


import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.enemysystem.Enemy;
import dk.sdu.mmmi.cbse.playersystem.Player;

import java.util.Collection;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;


public class EnemyControlSystem implements IEntityProcessingService {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity enemy : world.getEntities(Enemy.class)) {

            //Tick cooldown
            ((Enemy) enemy).tickFireTimer(gameData.getDeltaSec());


            //Movement
            //Find player entity to track
            Entity player = world.getEntities(Player.class).getFirst();
            if (player != null) {
                //Calculate vector between enemy and player
                Vector deltaPosition = player.getPosition().subtracted(enemy.getPosition());
                //Modify velocity using the delta vector and delta time to make the modification consistent across devices
                //!CHANGE THE 2 TO A VARIABLE 'TURNING SPEED'
                enemy.getVelocity().add(deltaPosition.normalized().multiplied(2 * gameData.getDeltaSec()));
                //Normalize the velocity vector
                enemy.getVelocity().normalize();
            }
            //Modify the position using the velocity vector, applying both speed and delta time
            enemy.getPosition().add(enemy.getVelocity().multiplied(enemy.getSpeed() * gameData.getDeltaSec()));

            //Set graphical rotation to match the direction of the velocity vector
            enemy.setRotation(enemy.getVelocity().toAngle());


            //Fire bullets
            if (((Enemy) enemy).canFire()) {
                getBulletSPIs().stream().findFirst().ifPresent(
                        spi -> world.addEntity(spi.createBullet(enemy, gameData, 160, 4f))
                );
            }

            //World border collision
            if (enemy.getPosition().x < 0) {
                enemy.getPosition().x = 0;
            }

            if (enemy.getPosition().x > gameData.getDisplaySize().x) {
                enemy.getPosition().x = gameData.getDisplaySize().x - 1;
            }

            if (enemy.getPosition().y < 0) {
                enemy.getPosition().y = 0;
            }

            if (enemy.getPosition().y > gameData.getDisplaySize().y) {
                enemy.getPosition().y = gameData.getDisplaySize().y - 1;
            }
        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
