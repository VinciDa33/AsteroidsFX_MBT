package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Plugin service interface for initializing and stopping services.
 * Functionally works as a contract, specifying what other modules can do.
 * - MBT
 */
public interface IGamePluginService {
    /**
     * @param gameData
     * @param world
     */
    void start(GameData gameData, World world);

    /**
     * @param gameData
     * @param world
     */
    void stop(GameData gameData, World world);
}
