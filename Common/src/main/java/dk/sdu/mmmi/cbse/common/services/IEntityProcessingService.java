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
     * @param gameData
     * @param world
     *
     * Pre-condition: gameData cannot be null, world cannot be null
     * Post-condition: The class implementing this interface has run its implementation of the process method
     */
    void process(GameData gameData, World world);
}
