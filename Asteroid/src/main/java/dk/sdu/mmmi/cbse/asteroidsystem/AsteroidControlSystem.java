package dk.sdu.mmmi.cbse.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.CircleColliderSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RenderingSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RigidbodySegment;
import dk.sdu.mmmi.cbse.common.entitysegments.TransformSegment;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.Random;

public class AsteroidControlSystem implements IEntityProcessingService {

    private static int maxAsteroids = 5;
    @Override
    public void process(GameData gameData, World world) {
        //!!!IMPORTANT!!! Test spawning of asteroids!
        if (world.getEntities(Asteroid.class).size() < maxAsteroids) {
            world.addEntity(createAsteroid(gameData, 60 + Math.random() * 80, 40f));
        }

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            RigidbodySegment rigidbody = asteroid.getSegment(RigidbodySegment.class);
            CircleColliderSegment collider = asteroid.getSegment(CircleColliderSegment.class);

            rigidbody.process(gameData, asteroid);
            rigidbody.setRotation(rigidbody.getRotation() + rigidbody.getRotationSpeed() * gameData.getDeltaSec());

            if (((Asteroid) asteroid).onScreen) {
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

            if (rigidbody.getPosition().x - collider.getRadius() - 10 > 0 && rigidbody.getPosition().x + collider.getRadius() + 10 < gameData.getDisplaySize().x)
                if (rigidbody.getPosition().y - collider.getRadius() - 10 > 0 && rigidbody.getPosition().y + collider.getRadius() + 10 < gameData.getDisplaySize().y)
                    ((Asteroid) asteroid).onScreen = true;
        }
    }

    public Entity createAsteroid(GameData gameData, double speed, double radius) {
        Entity asteroid = new Asteroid();
        asteroid.setTag(EntityTag.ASTEROID);

        Random rand = new Random();

        //Random asteroid vertex count
        int vertexCount = 8 + rand.nextInt(4);
        double[] vertexArray = new double[vertexCount * 2];

        //Generating circles, and offsetting vertices to create rock shapes
        double theta = Math.toRadians(360f / vertexCount);
        double smallestRadius = Integer.MAX_VALUE;
        for (int i = 0; i < vertexCount; i++) {
            double randomOffset = radius + rand.nextDouble(-radius * 0.1f, radius * 0.1f);
            smallestRadius = Math.min(randomOffset, smallestRadius);

            vertexArray[i*2] = Math.cos(theta * (i+1)) * randomOffset;
            vertexArray[i*2+1] = Math.sin(theta * (i+1)) * randomOffset;
        }

        //Rendering
        RenderingSegment renderer = new RenderingSegment();
        renderer.setPolygonArray(vertexArray);
        renderer.setColor(250, 250, 250);
        asteroid.addSegment(renderer);

        //Rigidbody
        RigidbodySegment rigidbody = new RigidbodySegment(asteroid);
        rigidbody.setRotation(rand.nextDouble(360d));
        int rotationalDirection = rand.nextInt(2) == 0 ? -1 : 1;
        rigidbody.setRotationSpeed(rotationalDirection * 8 * rand.nextDouble(2, 6));

        Vector screenCenter = gameData.getDisplaySize().multiplied(0.5f);
        //Subtracting 0.5f to allow for negative numbers
        Vector spawnDirection = new Vector(rand.nextDouble(-1, 1), rand.nextDouble(-1, 1)).normalized();
        //Multiplying by 0.75 to ensure the asteroids spawn off-screen. Uses Math.max to ensure this always works on non-square display sizes.
        rigidbody.setPosition(screenCenter.added(spawnDirection.multiplied(Math.max(gameData.getDisplaySize().x * 0.75f, gameData.getDisplaySize().y * 0.75f))));

        //Multiplying by 0.4 to get the half extends of the screen, as well as to limit how close an asteroid can travel to the edge
        Vector pointOnScreen = screenCenter.added(new Vector(rand.nextDouble(-1, 1), rand.nextDouble(-1, 1)).normalized().multiplied(Math.min(gameData.getDisplaySize().x * 0.35f, gameData.getDisplaySize().y * 0.35f)));
        rigidbody.setVelocity(pointOnScreen.subtracted(rigidbody.getPosition()).normalized().multiplied(speed));
        asteroid.addSegment(rigidbody);

        //Collision
        CircleColliderSegment collider = new CircleColliderSegment();
        collider.setRadius(smallestRadius);

        collider.addCollisionEvent(EntityTag.BULLET, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                asteroid.setDeletionFlag(true);
            }
        });
        collider.addCollisionEvent(EntityTag.ASTEROID, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                RigidbodySegment otherRigidbody = other.getSegment(RigidbodySegment.class);
                rigidbody.setPosition(rigidbody.getPosition().added(rigidbody.getVelocity().multiplied(-1 * gameData.getDeltaSec())));
                rigidbody.setVelocity(rigidbody.getPosition().subtracted(otherRigidbody.getPosition()).normalized().multiplied(rigidbody.getVelocity().magnitude()));
            }
        });

        asteroid.addSegment(collider);

        return asteroid;
    }
}
