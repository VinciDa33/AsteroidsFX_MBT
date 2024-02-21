package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class BulletControlSystem implements IEntityProcessingService, BulletSPI {
    @Override
    public void process(GameData gameData, World world, long delta) {

        for (Entity bullet : world.getEntities(Bullet.class)) {
            double changeX = Math.cos(Math.toRadians(bullet.getRotation()));
            double changeY = Math.sin(Math.toRadians(bullet.getRotation()));
            bullet.setX(bullet.getX() + changeX * bullet.getSpeed() * delta * 0.001f);
            bullet.setY(bullet.getY() + changeY * bullet.getSpeed() * delta * 0.001f);
        }
    }

    @Override
    public Entity createBullet(Entity shooter, GameData gameData, double speed) {
        Entity bullet = new Bullet();
        bullet.setPolygonCoordinates(-2,-2,-2,2,2,2,2,-2);
        bullet.setSpeed(speed);
        bullet.setX(shooter.getX());
        bullet.setY(shooter.getY());
        bullet.setRotation(shooter.getRotation());
        return bullet;
    }

    private void setShape(Entity entity) {
    }

}
