package dk.sdu.mmmi.cbse.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.CircleColliderSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.OnScreenSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RenderingSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RigidbodySegment;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class AsteroidFactory {
    public Asteroid createAsteroid(GameData gameData, World world, int asteroidSize) {
        Asteroid asteroid = new Asteroid();
        asteroid.setTag(EntityTag.ASTEROID);

        asteroid.setSpawnDelay(2d);
        asteroid.setSpawnTimestamp(gameData.getTime());
        asteroid.setAsteroidSize(asteroidSize);

        Random rand = new Random();
        double radius = AsteroidConfig.getRadius(asteroid.getAsteroidSize());
        double speed = AsteroidConfig.getSpeed(asteroid.getAsteroidSize());

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
        renderer.setColor(80, 80, 80);
        asteroid.addSegment(renderer);

        //Rigidbody
        RigidbodySegment rigidbody = new RigidbodySegment(asteroid);
        rigidbody.setRotation(rand.nextDouble(360d));
        int rotationalDirection = rand.nextInt(2) == 0 ? -1 : 1;
        rigidbody.setRotationSpeed(rotationalDirection * 8 * rand.nextDouble(2, 6));

        Vector screenCenter = gameData.getDisplaySize().divided(2d);
        Vector spawnDirection = new Vector(rand.nextDouble(-1, 1), rand.nextDouble(-1, 1)).normalized();
        double multiplier = Math.max(gameData.getDisplaySize().x * 0.4d, gameData.getDisplaySize().y * 0.4d);
        rigidbody.setPosition(screenCenter.added(spawnDirection.multiplied(multiplier)));

        rigidbody.setVelocity(spawnDirection.multiplied(speed));
        asteroid.addSegment(rigidbody);


        //OnScreen
        OnScreenSegment onScreen = new OnScreenSegment(gameData, asteroid);
        asteroid.addSegment(onScreen);

        //Collision
        CircleColliderSegment collider = new CircleColliderSegment();
        collider.setRadius(smallestRadius);

        collider.addCollisionEvent(EntityTag.PLAYER, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                if (!asteroid.isActive())
                    return;

                world.removeEntity(other);
            }
        });
        collider.addCollisionEvent(EntityTag.BULLET, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                if (!asteroid.isActive())
                    return;

                //Smaller asteroids award more points
                int amount = (4 - asteroid.getAsteroidSize()) * 100;

                //Handle score
                String url = String.format("http://localhost:6060/update?amount=%d", amount);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                try {
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }

                AsteroidSplitter.instance().splitAsteroid(gameData, asteroid);
            }
        });
        collider.addCollisionEvent(EntityTag.ASTEROID, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                RigidbodySegment otherRigidbody = other.getSegment(RigidbodySegment.class);
                rigidbody.setVelocity(rigidbody.getPosition().subtracted(otherRigidbody.getPosition()).normalized().multiplied(rigidbody.getVelocity().magnitude()));
                rigidbody.process(gameData, asteroid);
            }
        });

        asteroid.addSegment(collider);

        return asteroid;
    }
}
