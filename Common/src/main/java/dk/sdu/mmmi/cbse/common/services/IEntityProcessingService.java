package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Processing service interface for entity processing services.
 * Functionally works as a contract, specifying what other modules can do.
 * Enables using the process method, which will run every frame.
 */
public interface IEntityProcessingService {
    /**
     * @param gameData, cannot be null
     * @param world, cannot be null
     */
    void process(GameData gameData, World world);
}
