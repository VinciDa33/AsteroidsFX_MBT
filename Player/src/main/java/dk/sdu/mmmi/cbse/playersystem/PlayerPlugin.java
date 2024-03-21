package dk.sdu.mmmi.cbse.playersystem;

import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.*;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
public class PlayerPlugin implements IGamePluginService {

    public PlayerPlugin() {
    }

    @Override
    public void start(GameData gameData, World world) {

        // Add entities to the world
        Entity player = createPlayerShip(gameData);
        world.addEntity(player);
    }

    private Entity createPlayerShip(GameData gameData) {

        Player playerShip = new Player();
        playerShip.setTag(EntityTag.PLAYER);

        //Rendering
        RenderingSegment renderer = new RenderingSegment();
        renderer.setPolygonCoordinates(
                7, 0,
                -8, -6,
                -5, 0,
                -8, 6
        );
        renderer.setColor(140, 220, 240);
        playerShip.addSegment(renderer);

        //Collider
        CircleColliderSegment collider = new CircleColliderSegment();
        collider.setRadius(6f);

        collider.addCollisionEvent(EntityTag.ASTEROID, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                playerShip.setDeletionFlag(true);
            }
        });
        collider.addCollisionEvent(EntityTag.ENEMY, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                playerShip.setDeletionFlag(true);
            }
        });
        collider.addCollisionEvent(EntityTag.BULLET, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                playerShip.setDeletionFlag(true);
            }
        });

        playerShip.addSegment(collider);

        //Rigidbody
        RigidbodySegment rigidbody = new RigidbodySegment(playerShip);
        rigidbody.setPosition(gameData.getDisplaySize().divided(2d));
        rigidbody.setVelocity(new Vector(0, -140));
        rigidbody.setRotationSpeed(250);
        rigidbody.setRotationLock(true);
        playerShip.addSegment(rigidbody);

        //Shooting
        ShootingSegment shooting = new ShootingSegment();
        shooting.setFireCooldown(0.5f);
        playerShip.addSegment(shooting);

        return playerShip;
    }

    @Override
    public void stop(GameData gameData, World world) {

    }

}
