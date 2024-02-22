package dk.sdu.mmmi.cbse.enemysystem;


import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.enemysystem.Enemy;

import java.util.Collection;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;


public class EnemyControlSystem implements IEntityProcessingService {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity enemy : world.getEntities(Enemy.class)) {

            //Tick action cooldown
            ((Enemy) enemy).tickActionTimer(gameData.getDeltaSec());
            ((Enemy) enemy).tickFireTimer(gameData.getDeltaSec());

            if (((Enemy) enemy).canDoAction())
                ((Enemy) enemy).setCurrentAction((int)Math.floor(Math.random() * 3f));

            if (((Enemy) enemy).getCurrentAction() == 1)
                enemy.setRotation(enemy.getRotation() - enemy.getRotationSpeed() * gameData.getDeltaSec());
            else if (((Enemy) enemy).getCurrentAction() == 2)
                enemy.setRotation(enemy.getRotation() + enemy.getRotationSpeed() * gameData.getDeltaSec());

            //Move
            double changeX = Math.cos(Math.toRadians(enemy.getRotation()));
            double changeY = Math.sin(Math.toRadians(enemy.getRotation()));
            enemy.setX(enemy.getX() + changeX * enemy.getSpeed() * gameData.getDeltaSec());
            enemy.setY(enemy.getY() + changeY * enemy.getSpeed() * gameData.getDeltaSec());

            //Fire bullets
            if (((Enemy) enemy).canFire()) {
                getBulletSPIs().stream().findFirst().ifPresent(
                        spi -> {world.addEntity(spi.createBullet(enemy, gameData, 120));}
                );
            }

            if (enemy.getX() < 0) {
                enemy.setX(1);
            }

            if (enemy.getX() > gameData.getDisplayWidth()) {
                enemy.setX(gameData.getDisplayWidth()-1);
            }

            if (enemy.getY() < 0) {
                enemy.setY(1);
            }

            if (enemy.getY() > gameData.getDisplayHeight()) {
                enemy.setY(gameData.getDisplayHeight()-1);
            }


        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
