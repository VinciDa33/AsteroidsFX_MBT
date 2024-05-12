package dk.sdu.mmmi.cbse.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.EntityTag;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.entitysegments.RigidbodySegment;

public class AsteroidSplitter {
    private static AsteroidSplitter singleton;
    private World world;

    private AsteroidSplitter() {

    }

    public static AsteroidSplitter instance() {
        if (singleton == null)
            singleton = new AsteroidSplitter();
        return singleton;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void splitAsteroid(GameData gameData, Asteroid asteroid) {
        if (world == null)
            return;

        world.removeEntity(asteroid);

        //Do not split if asteroid is already the smallest size
        if (asteroid.getAsteroidSize() <= 1)
            return;

        RigidbodySegment rigidbody = asteroid.getSegment(RigidbodySegment.class);
        Vector newDirection1 = rigidbody.getVelocity().rotated(35d).normalized();
        Vector newDirection2 = rigidbody.getVelocity().rotated(-35d).normalized();

        world.addEntity(createAsteroidFragment(gameData, asteroid, newDirection1));
        world.addEntity(createAsteroidFragment(gameData, asteroid, newDirection2));
    }

    private Asteroid createAsteroidFragment(GameData gameData, Asteroid asteroid, Vector newDirection) {
        RigidbodySegment rigidbody = asteroid.getSegment(RigidbodySegment.class);

        Asteroid asteroidFrag = new AsteroidFactory().createAsteroid(gameData, world, asteroid.getAsteroidSize() - 1);
        asteroidFrag.setSpawnDelay(0d);
        asteroidFrag.setSpawnTimestamp(gameData.getTime());

        double fragmentSpeed = AsteroidConfig.getSpeed(asteroidFrag.getAsteroidSize());
        double fragmentRadius = AsteroidConfig.getRadius(asteroidFrag.getAsteroidSize());

        RigidbodySegment fragmentRigidbody = asteroidFrag.getSegment(RigidbodySegment.class);
        fragmentRigidbody.setVelocity(newDirection.multiplied(fragmentSpeed));
        fragmentRigidbody.setPosition(rigidbody.getPosition().added(newDirection.multiplied(fragmentRadius * 2)));

        return asteroidFrag;
    }
}
