package dk.sdu.mmmi.cbse.enemysystem;

import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.CircleColliderSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RenderingSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RigidbodySegment;
import dk.sdu.mmmi.cbse.common.entitysegments.ShootingSegment;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

public class EnemyPlugin implements IGamePluginService {

    public EnemyPlugin() {
    }

    @Override
    public void start(GameData gameData, World world) {

        // Add entities to the world
        Entity enemy = createEnemyShip(gameData);
        world.addEntity(enemy);
    }

    private Entity createEnemyShip(GameData gameData) {

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

        //Collider
        CircleColliderSegment collider = new CircleColliderSegment();
        collider.setRadius(10f);

        collider.addCollisionEvent(EntityTag.ASTEROID, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                enemyShip.setDeletionFlag(true);
            }
        });
        collider.addCollisionEvent(EntityTag.BULLET, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                enemyShip.setDeletionFlag(true);
            }
        });

        enemyShip.addSegment(collider);

        //Rigidbody
        RigidbodySegment rigidbody = new RigidbodySegment(enemyShip);
        rigidbody.setPosition(gameData.getDisplaySize().divided(3d));
        rigidbody.setVelocity(new Vector(0, 80));
        rigidbody.setRotationSpeed(80);
        rigidbody.setRotationLock(true);
        enemyShip.addSegment(rigidbody);

        //Shooting
        ShootingSegment shooting = new ShootingSegment();
        shooting.setFireCooldown(1f);
        enemyShip.addSegment(shooting);

        return enemyShip;
    }

    @Override
    public void stop(GameData gameData, World world) {

    }
}
