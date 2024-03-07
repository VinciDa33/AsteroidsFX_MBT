package dk.sdu.mmmi.cbse.enemysystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.player.Player;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.enemysystem.Enemy;

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
        enemyShip.setPolygonCoordinates(
                11, 0,
                -8, -7,
                -6, 0,
                -8, 7
        );

        enemyShip.setColor(240, 100, 80);

        enemyShip.setRadius(10f);

        enemyShip.setSpeed(100);
        enemyShip.setRotationSpeed(2);

        enemyShip.setVelocity(new Vector(0, 1));
        enemyShip.setFireCooldown(1f);

        enemyShip.setPosition(gameData.getDisplaySize().divided(3d));
        return enemyShip;
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Remove entities
        for (Entity enemy : world.getEntities(Enemy.class)) {
            enemy.setDeletionFlag(true);
        }
    }
}
