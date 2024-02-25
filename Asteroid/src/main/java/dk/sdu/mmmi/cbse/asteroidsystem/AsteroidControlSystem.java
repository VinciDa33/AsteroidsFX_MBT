package dk.sdu.mmmi.cbse.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class AsteroidControlSystem implements IEntityProcessingService {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
        }
    }

    public Entity createAsteroid(GameData gameData, double speed) {
        Entity asteroid = new Asteroid();
        asteroid.setPolygonCoordinates(-2,-2,-2,2,2,2,2,-2);

        asteroid.setSpeed(speed);

        asteroid.setPosition(new Vector(0, 0));
        asteroid.setRotation(Math.random() * 360f);
        return asteroid;
    }
}
