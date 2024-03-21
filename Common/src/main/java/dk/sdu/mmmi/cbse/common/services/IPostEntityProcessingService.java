package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Processing service interface for entity processing services.
 * Functionally works as a contract, specifying what other modules can do.
 * Enables running process every frame.
 *
 * All implementations of this interface will run after the standard EntityProcessing has occurred.
 * Use this to process game steps that need to run after all normal movement and actions
 * have been processed.
 * - MBT
 */
public interface IPostEntityProcessingService {
    /**
     * @param gameData
     * @param world
     */
    void process(GameData gameData, World world);
}
