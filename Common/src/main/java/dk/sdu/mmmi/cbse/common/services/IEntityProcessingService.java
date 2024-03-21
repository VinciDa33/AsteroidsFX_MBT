package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Processing service interface for entity processing services.
 * Functionally works as a contract, specifying what other modules can do.
 * Enables running process every frame.
 * - MBT
 */
public interface IEntityProcessingService {
    /**
     * @param gameData
     * @param world
     */
    void process(GameData gameData, World world);
}
