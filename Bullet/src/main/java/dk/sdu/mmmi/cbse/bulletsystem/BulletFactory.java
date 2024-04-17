package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletParams;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.*;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BulletFactory implements BulletSPI {
    @Override
    public Entity createBullet(Entity shooter, GameData gameData, World world, BulletParams params) {
        TransformSegment shooterTransform = shooter.getSegment(TransformSegment.class);
        RenderingSegment shooterRenderer = shooter.getSegment(RenderingSegment.class);


        Bullet bullet = new Bullet();
        bullet.setTag(EntityTag.BULLET);
        bullet.setShooter(shooter);

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
                if (bullet.getShooter().getTag() == EntityTag.PLAYER)
                    return;

                world.removeEntity(other);
                world.removeEntity(bullet);
            }
        });
        collider.addCollisionEvent(EntityTag.ASTEROID, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                world.removeEntity(bullet);
            }
        });
        collider.addCollisionEvent(EntityTag.ENEMY, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                //If an enemy was the shooter of this bullet, it will pass through other enemies
                if (((Bullet) bullet).getShooter().getTag() == EntityTag.ENEMY)
                    return;

                //Handle score
                String url = String.format("http://localhost:6060/update?amount=%d", 500);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                try {
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }

                //gameData.addScore(500);

                world.removeEntity(other);
                world.removeEntity(bullet);
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
