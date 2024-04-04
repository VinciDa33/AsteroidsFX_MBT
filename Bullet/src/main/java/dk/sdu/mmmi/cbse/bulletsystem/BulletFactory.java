package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletParams;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.EntityTag;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.entitysegments.*;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;

public class BulletFactory implements BulletSPI {
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

        //OnScreen
        OnScreenSegment onScreen = new OnScreenSegment(gameData, bullet);
        bullet.addSegment(onScreen);

        //Collision
        CircleColliderSegment collider = new CircleColliderSegment();
        collider.setRadius(params.radius);

        collider.addCollisionEvent(EntityTag.PLAYER, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                //If the player was the shooter of this bullet, it will not kill the player
                if (((Bullet) bullet).getShooter().getTag() == EntityTag.PLAYER)
                    return;

                other.setDeletionFlag(true);
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
                //If an enemy was the shooter of this bullet, it will pass through other enemies
                if (((Bullet) bullet).getShooter().getTag() == EntityTag.ENEMY)
                    return;

                other.setDeletionFlag(true);
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
}
