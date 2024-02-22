package dk.sdu.mmmi.cbse.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class AsteroidControlSystem implements IEntityProcessingService {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
        }
    }

    public Entity createBullet(GameData gameData, double speed) {
        Entity asteroid = new Asteroid();
        asteroid.setPolygonCoordinates(-2,-2,-2,2,2,2,2,-2);

        asteroid.setSpeed(speed);

        asteroid.setX(0);
        asteroid.setY(0);
        asteroid.setRotation(Math.random() * 360f);
        return asteroid;
    }
}
