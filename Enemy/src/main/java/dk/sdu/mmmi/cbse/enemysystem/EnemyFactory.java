package dk.sdu.mmmi.cbse.enemysystem;

import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.*;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;

import java.util.Random;

public class EnemyFactory {
    public Enemy createEnemy(GameData gameData, World world) {
        Enemy enemyShip = new Enemy();
        enemyShip.setTag(EntityTag.ENEMY);

        //Rendering
        RenderingSegment renderer = new RenderingSegment();
        renderer.setPolygonCoordinates(
                11, 0,
                -8, -7,
                -6, 0,
                -8, 7
        );
        renderer.setColor(240, 100, 80);
        enemyShip.addSegment(renderer);

        //OnScreen
        OnScreenSegment onScreen = new OnScreenSegment(gameData, enemyShip);
        enemyShip.addSegment(onScreen);

        //Collider
        CircleColliderSegment collider = new CircleColliderSegment();
        collider.setRadius(10f);
        collider.addCollisionEvent(EntityTag.ASTEROID, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                OnScreenSegment oss = enemyShip.getSegment(OnScreenSegment.class);
                if (!oss.isOnScreen())
                    return;

                gameData.addScore(500);

                world.removeEntity(enemyShip);
            }
        });

        enemyShip.addSegment(collider);

        //Rigidbody
        RigidbodySegment rigidbody = new RigidbodySegment(enemyShip);

        Random random = new Random();
        Vector screenCenter = gameData.getDisplaySize().divided(2);
        double multiplier = Math.max(gameData.getDisplaySize().x, gameData.getDisplaySize().y);
        Vector direction = new Vector(random.nextDouble(-1, 1), random.nextDouble(-1, 1)).normalized().multiplied(multiplier);
        rigidbody.setPosition(screenCenter.added(direction));

        rigidbody.setVelocity(new Vector(0, 80));
        rigidbody.setRotationSpeed(80);
        rigidbody.setRotationLock(true);
        enemyShip.addSegment(rigidbody);

        //Shooting
        ShootingSegment shooting = new ShootingSegment();
        shooting.setFireCooldown(2f);
        enemyShip.addSegment(shooting);

        return enemyShip;
    }
}
