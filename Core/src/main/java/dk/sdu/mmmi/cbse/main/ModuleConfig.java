package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import static java.util.stream.Collectors.toList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

@Configuration
public class ModuleConfig {
    private static ModuleLayer pluginLayer;
    public ModuleConfig() {
        setupPluginModuleLayer();
    }

    @Bean(name="GameBean")
    public Game game(){
        return new Game(gamePluginServices(), entityProcessingServices(), postEntityProcessingServices());
    }

    @Bean(name="EntityProcessingBean")
    public List<IEntityProcessingService> entityProcessingServices(){
        return ServiceLoader.load(pluginLayer, IEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    @Bean(name="GamePluginBean")
    public List<IGamePluginService> gamePluginServices() {
        return ServiceLoader.load(pluginLayer, IGamePluginService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    @Bean(name="PostEntityProcessingBean")
    public List<IPostEntityProcessingService> postEntityProcessingServices() {
        return ServiceLoader.load(pluginLayer, IPostEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    private static void setupPluginModuleLayer() {
        Path pluginsDir = Paths.get("plugins"); // Directory with plugins JARs

        // Search for plugins in the plugins directory
        ModuleFinder pluginsFinder = ModuleFinder.of(pluginsDir);

        // Create list of all found plugin modules
        List<String> plugins = pluginsFinder
                .findAll()
                .stream()
                .map(ModuleReference::descriptor)
                .map(ModuleDescriptor::name)
                .collect(Collectors.toList());

        // Create configuration
        java.lang.module.Configuration pluginsConfiguration = ModuleLayer
                .boot()
                .configuration()
                .resolve(pluginsFinder, ModuleFinder.of(), plugins);

        // Create module layer
        pluginLayer = ModuleLayer
                .boot()
                .defineModulesWithOneLoader(pluginsConfiguration, ClassLoader.getSystemClassLoader());
    }
}