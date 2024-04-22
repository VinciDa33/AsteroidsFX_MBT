package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import static java.util.stream.Collectors.toList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.ServiceLoader;

@Configuration
public class ModuleConfig {
    public ModuleConfig() {
    }

    //Bean for obtaining a Game object, with dependencies injected
    @Bean(name="GameBean")
    public Game game(){
        return new Game(gamePluginServices(), entityProcessingServices(), postEntityProcessingServices());
    }

    //Bean for getting a collection of all entity processing services
    @Bean(name="EntityProcessingBean")
    public List<IEntityProcessingService> entityProcessingServices(){
        return ServiceLoader.load(IEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    //Bean for getting a collection of all game plugin services
    @Bean(name="GamePluginBean")
    public List<IGamePluginService> gamePluginServices() {
        return ServiceLoader.load(IGamePluginService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    //Bean for getting a collection of all post entity processing services
    @Bean(name="PostEntityProcessingBean")
    public List<IPostEntityProcessingService> postEntityProcessingServices() {
        return ServiceLoader.load(IPostEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}