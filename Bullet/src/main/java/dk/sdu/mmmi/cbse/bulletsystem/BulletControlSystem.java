package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletParams;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.CircleColliderSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RenderingSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RigidbodySegment;
import dk.sdu.mmmi.cbse.common.entitysegments.TransformSegment;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class BulletControlSystem implements IEntityProcessingService, BulletSPI {
    @Override
    public void process(GameData gameData, World world) {

        for (Entity bullet : world.getEntities(Bullet.class)) {
            bullet.getSegment(RigidbodySegment.class).process(gameData, bullet);
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
                0, 1 * params.radius,
                0.71 * params.radius, 0.71 * params.radius,
                1 * params.radius, 0,
                0.71 * params.radius, -0.71 * params.radius,
                0, -1 * params.radius,
                -0.71 * params.radius, -0.71 * params.radius,
                -1 * params.radius, 0,
                -0.71 * params.radius, 0.71 * params.radius
        );
        if (shooterRenderer != null) renderer.setColor(shooterRenderer.getColor());
        bullet.addSegment(renderer);

        //Collision
        CircleColliderSegment collider = new CircleColliderSegment();
        collider.setRadius(params.radius);

        collider.addCollisionEvent(EntityTag.PLAYER, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                bullet.setDeletionFlag(true);
            }
        });
        collider.addCollisionEvent(EntityTag.ASTEROID, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                bullet.setDeletionFlag(true);
            }
        });
        collider.addCollisionEvent(EntityTag.ENEMY, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                bullet.setDeletionFlag(true);
            }
        });

        bullet.addSegment(collider);

        //Rigidbody
        RigidbodySegment rigidbody = new RigidbodySegment(bullet);
        Vector pos = shooterTransform == null ? new Vector(-10, -10) : shooterTransform.getPosition().copy();
        rigidbody.setPosition(pos);
        rigidbody.setVelocity(params.direction.normalized().multiplied(params.speed));
        rigidbody.setRotationLock(true);
        bullet.addSegment(rigidbody);

        return bullet;
    }

    private void setShape(Entity entity) {
    }

}
