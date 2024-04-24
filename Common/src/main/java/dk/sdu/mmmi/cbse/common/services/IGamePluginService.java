package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Plugin service interface for initializing and stopping services.
 * Functionally works as a contract, specifying what other modules can do.
 * Enables using the start and stop methods.
 */
public interface IGamePluginService {
    /**
     * @param gameData
     * @param world
     *
     * Pre-condition: gameData cannot be null, world cannot be null
     * Post-condition: The class implementing this interface has run its implementation of the start method
     */
    void start(GameData gameData, World world);

    /**
     * @param gameData
     * @param world
     *
     * Pre-condition: gameData cannot be null, world cannot be null
     * Post-condition: The class implementing this interface has run its implementation of the stop method
     */
    void stop(GameData gameData, World world);
}
