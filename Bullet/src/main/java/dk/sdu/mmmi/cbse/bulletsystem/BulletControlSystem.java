package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletParams;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.CircleColliderSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RenderingSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RigidbodySegment;
import dk.sdu.mmmi.cbse.common.entitysegments.TransformSegment;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class BulletControlSystem implements IEntityProcessingService, BulletSPI {
    @Override
    public void process(GameData gameData, World world) {

        for (Entity bullet : world.getEntities(Bullet.class)) {
            bullet.getPosition().add(bullet.getVelocity().multiplied(bullet.getSpeed() * gameData.getDeltaSec()));
        }
    }

    @Override
    public Entity createBullet(Entity shooter, GameData gameData, BulletParams params) {
        TransformSegment shooterTransform = shooter.getSegment(TransformSegment.class);
        RenderingSegment shooterRenderer = shooter.getSegment(RenderingSegment.class);


        Entity bullet = new Bullet();
        bullet.setTag(EntityTag.BULLET);
        ((Bullet) bullet).setShooter(shooter);

        //Rendering
        RenderingSegment renderer = new RenderingSegment();
        renderer.setPolygonCoordinates(
                0, 1 * radius,
                0.71 * radius, 0.71 * radius,
                1 * radius, 0,
                0.71 * radius, -0.71 * radius,
                0, -1 * radius,
                -0.71 * radius, -0.71 * radius,
                -1 * radius, 0,
                -0.71 * radius, 0.71 * radius
        );
        RenderingSegment shooterRenderer = shooter.getSegment(RenderingSegment.class);
        if (shooterRenderer != null) renderer.setColor(shooterRenderer.getColor());
        bullet.addSegment(renderer);

        //Collision
        CircleColliderSegment collider = new CircleColliderSegment();
        collider.setRadius(params.radius);
        bullet.addSegment(collider);

        //Rigidbody
        RigidbodySegment rigidbody = new RigidbodySegment();
        rigidbody.setPosition();

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
