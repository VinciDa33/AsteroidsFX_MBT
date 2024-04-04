package dk.sdu.mmmi.cbse.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.*;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.List;
import java.util.Random;

public class AsteroidControlSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {

        int maxAsteroidCount = Math.min(2 + (int) Math.floor(gameData.getTime() / 20d), 7);

        //Spawn new asteroids
        if (world.getEntities(Asteroid.class).size() < maxAsteroidCount) {
            Asteroid asteroid = new AsteroidFactory().createAsteroid(gameData, 3);
            world.addEntity(asteroid);
        }

        //Process asteroids
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            RigidbodySegment rigidbody = asteroid.getSegment(RigidbodySegment.class);
            CircleColliderSegment collider = asteroid.getSegment(CircleColliderSegment.class);

            Asteroid a = (Asteroid) asteroid;
            if (!a.isActive()) {
                if (gameData.getTime() > a.getSpawnDelay() + a.getSpawnTimestamp()) {
                    a.setActive(true);

                    RenderingSegment rs = a.getSegment(RenderingSegment.class);
                    rs.setColor(250, 250, 250);
                }
                continue;
            }

            rigidbody.process(gameData, asteroid);
            rigidbody.setRotation(rigidbody.getRotation() + rigidbody.getRotationSpeed() * gameData.getDeltaSec());

            if (rigidbody.getPosition().x - collider.getRadius() < 0) {
                rigidbody.getPosition().x = 0 + collider.getRadius();
                rigidbody.getVelocity().x *= -1;
            }
            if (rigidbody.getPosition().x + collider.getRadius() > gameData.getDisplaySize().x) {
                rigidbody.getPosition().x = gameData.getDisplaySize().x - collider.getRadius();
                rigidbody.getVelocity().x *= -1;
            }
            if (rigidbody.getPosition().y - collider.getRadius() < 0) {
                rigidbody.getPosition().y = 0 + collider.getRadius();
                rigidbody.getVelocity().y *= -1;
            }
            if (rigidbody.getPosition().y + collider.getRadius() > gameData.getDisplaySize().y) {
                rigidbody.getPosition().y = gameData.getDisplaySize().y - collider.getRadius();
                rigidbody.getVelocity().y *= -1;
            }
        }
    }
}
