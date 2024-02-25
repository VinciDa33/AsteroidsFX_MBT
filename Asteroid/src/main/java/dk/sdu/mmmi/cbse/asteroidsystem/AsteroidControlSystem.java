package dk.sdu.mmmi.cbse.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class AsteroidControlSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {
        //!!! Test spawning of asteroids !!!
        if (world.getEntities(Asteroid.class).size() < 5) {
            world.addEntity(createAsteroid(gameData, 40 + Math.random() * 80, 18f));
        }

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            asteroid.getPosition().add(asteroid.getVelocity().multiplied(asteroid.getSpeed() * gameData.getDeltaSec()));
            asteroid.setRotation(asteroid.getRotation() + asteroid.getRotationSpeed() * gameData.getDeltaSec());
        }
    }

    public Entity createAsteroid(GameData gameData, double speed, double size) {
        Entity asteroid = new Asteroid();

        //Random asteroid vertex count
        int vertexCount = 8 + (int)Math.floor(Math.random() * 4);
        double[] vertexArray = new double[vertexCount * 2];

        //Generating circles, and offsetting vertices to create rock shapes
        double theta = Math.toRadians(360f / vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            double randomOffset = size + Math.random() * 12f;
            vertexArray[i*2] = Math.cos(theta * (i+1)) * randomOffset;
            vertexArray[i*2+1] = Math.sin(theta * (i+1)) * randomOffset;
        }
        asteroid.setPolygonArray(vertexArray);


        asteroid.setColor(250, 250, 250);

        asteroid.setSpeed(speed);
        asteroid.setRotationSpeed(20 + (Math.random() > 0.5f ? -1 : 1) * 30);

        Vector screenCenter = gameData.getDisplaySize().multiplied(0.5f);

        //Subtracting 0.5f to allow for negative numbers
        Vector spawnDirection = new Vector(Math.random()-0.5f, Math.random()-0.5f).normalized();
        //Multiplying by 0.5 to get the half extends of the screen, and adding 50 to get a short distance away from the screen edge
        asteroid.setPosition(screenCenter.added(spawnDirection.multiplied(Math.max(gameData.getDisplaySize().x * 0.5f + 50, gameData.getDisplaySize().y * 0.5f + 50))));

        //Multiplying by 0.4 to get the half extends of the screen, as well as to limit how close an asteroid can travel to the edge
        Vector pointOnScreen = screenCenter.added(new Vector(Math.random()-0.5f, Math.random()-0.5f).normalized().multiplied(Math.min(gameData.getDisplaySize().x * 0.4f, gameData.getDisplaySize().y * 0.4f)));
        asteroid.setVelocity(pointOnScreen.subtracted(asteroid.getPosition()).normalized());

        asteroid.setRotation(Math.random() * 360f);
        return asteroid;
    }
}
