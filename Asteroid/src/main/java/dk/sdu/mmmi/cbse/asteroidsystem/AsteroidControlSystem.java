package dk.sdu.mmmi.cbse.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.Random;

public class AsteroidControlSystem implements IEntityProcessingService {

    private final int maxAsteroids = 3;
    @Override
    public void process(GameData gameData, World world) {
        //!!!IMPORTANT!!! Test spawning of asteroids!
        if (world.getEntities(Asteroid.class).size() < maxAsteroids) {
            world.addEntity(createAsteroid(gameData, 60 + Math.random() * 80, 40f));
        }

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            asteroid.getPosition().add(asteroid.getVelocity().multiplied(asteroid.getSpeed() * gameData.getDeltaSec()));
            asteroid.setRotation(asteroid.getRotation() + asteroid.getRotationSpeed() * gameData.getDeltaSec());

            if (((Asteroid) asteroid).onScreen) {
                if (asteroid.getPosition().x - asteroid.getRadius() < 0) {
                    asteroid.getPosition().x = 0 + asteroid.getRadius();
                    asteroid.getVelocity().x *= -1;
                }
                if (asteroid.getPosition().x + asteroid.getRadius() > gameData.getDisplaySize().x) {
                    asteroid.getPosition().x = gameData.getDisplaySize().x - asteroid.getRadius();
                    asteroid.getVelocity().x *= -1;
                }
                if (asteroid.getPosition().y - asteroid.getRadius() < 0) {
                    asteroid.getPosition().y = 0 + asteroid.getRadius();
                    asteroid.getVelocity().y *= -1;
                }
                if (asteroid.getPosition().y + asteroid.getRadius() > gameData.getDisplaySize().y) {
                    asteroid.getPosition().y = gameData.getDisplaySize().y - asteroid.getRadius();
                    asteroid.getVelocity().y *= -1;
                }
            }

            if (asteroid.getPosition().x - asteroid.getRadius() - 10 > 0 && asteroid.getPosition().x + asteroid.getRadius() + 10 < gameData.getDisplaySize().x)
                if (asteroid.getPosition().y - asteroid.getRadius() - 10 > 0 && asteroid.getPosition().y + asteroid.getRadius() + 10 < gameData.getDisplaySize().y)
                    ((Asteroid) asteroid).onScreen = true;
        }
    }

    public Entity createAsteroid(GameData gameData, double speed, double radius) {
        Entity asteroid = new Asteroid();
        Random rand = new Random();

        //region Asteroid polygon
        //Random asteroid vertex count
        int vertexCount = 8 + rand.nextInt(4);
        double[] vertexArray = new double[vertexCount * 2];

        //Generating circles, and offsetting vertices to create rock shapes
        double theta = Math.toRadians(360f / vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            double randomOffset = radius + rand.nextDouble(-radius * 0.2f, radius * 0.2f);
            vertexArray[i*2] = Math.cos(theta * (i+1)) * randomOffset;
            vertexArray[i*2+1] = Math.sin(theta * (i+1)) * randomOffset;
        }
        asteroid.setPolygonArray(vertexArray);
        //endregion

        //region Asteroid attributes
        asteroid.setColor(250, 250, 250);
        asteroid.setSpeed(speed);
        asteroid.setRotation(Math.random() * 360f);
        asteroid.setRadius(radius);
        //endregion

        //region Asteroid rotation
        int rotationalDirection = rand.nextInt(2) == 0 ? -1 : 1;
        asteroid.setRotationSpeed(rotationalDirection * 5 * rand.nextDouble(1, 5));
        //endregion

        //region Asteroid initial position & velocity calculations
        Vector screenCenter = gameData.getDisplaySize().multiplied(0.5f);
        //Subtracting 0.5f to allow for negative numbers
        Vector spawnDirection = new Vector(rand.nextDouble(-1, 1), rand.nextDouble(-1, 1)).normalized();
        //Multiplying by 0.75 to ensure the asteroids spawn off-screen. Uses Math.max to ensure this always works on non-square display sizes.
        asteroid.setPosition(screenCenter.added(spawnDirection.multiplied(Math.max(gameData.getDisplaySize().x * 0.75f, gameData.getDisplaySize().y * 0.75f))));

        //Multiplying by 0.4 to get the half extends of the screen, as well as to limit how close an asteroid can travel to the edge
        Vector pointOnScreen = screenCenter.added(new Vector(rand.nextDouble(-1, 1), rand.nextDouble(-1, 1)).normalized().multiplied(Math.min(gameData.getDisplaySize().x * 0.35f, gameData.getDisplaySize().y * 0.35f)));
        asteroid.setVelocity(pointOnScreen.subtracted(asteroid.getPosition()).normalized());
        //endregion

        return asteroid;
    }
}
