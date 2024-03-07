import dk.sdu.mmmi.cbse.common.player.Player;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

module SplitPackageExample {
    requires Common;
    exports dk.sdu.mmmi.cbse.common.player;
    provides IGamePluginService with Player;
}