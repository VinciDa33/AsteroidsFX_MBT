package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class BulletControlSystem implements IEntityProcessingService, BulletSPI {
    @Override
    public void process(GameData gameData, World world) {

        for (Entity bullet : world.getEntities(Bullet.class)) {
            bullet.getPosition().add(bullet.getVelocity().multiplied(bullet.getSpeed() * gameData.getDeltaSec()));
        }
    }

    @Override
    public Entity createBullet(Entity shooter, GameData gameData, double speed, double radius) {
        Entity bullet = new Bullet();
        ((Bullet) bullet).setShooter(shooter);

        bullet.setPolygonCoordinates(
                0, 1 * radius,
                0.71 * radius, 0.71 * radius,
                1 * radius, 0,
                0.71 * radius, -0.71 * radius,
                0, -1 * radius,
                -0.71 * radius, -0.71 * radius,
                -1 * radius, 0,
                -0.71 * radius, 0.71 * radius
        );

        bullet.setColor(shooter.getColor());
        bullet.setRadius(radius);

        bullet.setSpeed(speed);

        bullet.setPosition(shooter.getPosition().copy());
        bullet.setVelocity(shooter.getVelocity().normalized());

        //Rotation is set once, since bullets do not change direction
        bullet.setRotation(bullet.getVelocity().toAngle());
        return bullet;
    }

    private void setShape(Entity entity) {
    }

}
