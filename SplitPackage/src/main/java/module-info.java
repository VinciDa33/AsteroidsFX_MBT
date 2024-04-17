import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

module SplitPackage {
    requires Common;
    exports dk.sdu.mmmi.cbse.playersystem;
    provides IGamePluginService with dk.sdu.mmmi.cbse.playersystem.Player;
}