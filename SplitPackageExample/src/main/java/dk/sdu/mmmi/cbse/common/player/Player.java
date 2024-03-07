package dk.sdu.mmmi.cbse.common.player;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

/**
 * This module, package and class are all an example of split packages and how to resolve them.
 * This split package has been resolved through the use of module layers (see Main).
 * - MBT
 */
public class Player implements IGamePluginService {
    @Override
    public void start(GameData gameData, World world) {
        System.out.println("This is the split package speaking!");
    }

    @Override
    public void stop(GameData gameData, World world) {

    }
}
