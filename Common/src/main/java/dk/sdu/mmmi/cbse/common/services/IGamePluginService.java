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
     * @param gameData, cannot be null
     * @param world, cannot be null
     */
    void start(GameData gameData, World world);

    /**
     * @param gameData, cannot be null
     * @param world, cannot be null
     */
    void stop(GameData gameData, World world);
}
