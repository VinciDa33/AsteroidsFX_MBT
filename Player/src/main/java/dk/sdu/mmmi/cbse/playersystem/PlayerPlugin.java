package dk.sdu.mmmi.cbse.playersystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.player.Player;
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
        playerShip.setPolygonCoordinates(
                7, 0,
                -8, -6,
                -5, 0,
                -8, 6
        );

        playerShip.setColor(140, 220, 240);

        playerShip.setRadius(6f);

        playerShip.setSpeed(140);
        playerShip.setVelocity(new Vector(0, -1));
        playerShip.setRotationSpeed(250);

        playerShip.setFireCooldown(0.5f);

        playerShip.setPosition(gameData.getDisplaySize().divided(2d));
        return playerShip;
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Remove entities
        for (Entity player : world.getEntities(Player.class)) {
            player.setDeletionFlag(true);
        }
    }

}
