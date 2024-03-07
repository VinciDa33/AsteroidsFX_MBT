package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import java.lang.module.ModuleReference;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    private final boolean showColliders = false;
    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Map<Entity, Circle> colliders = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();
    private static ModuleLayer pluginLayer;


    public static void main(String[] args) {
        setupPluginModuleLayer();

        launch(Main.class);
    }

    @Override
    public void start(Stage window) throws Exception {
        Text text = new Text(10, 20, "Destroyed asteroids: 0");
        gameWindow.setPrefSize(gameData.getDisplaySize().x, gameData.getDisplaySize().y);
        gameWindow.setStyle("-fx-background-color: #000000"); //Set background color
        gameWindow.getChildren().add(text);

        Scene scene = new Scene(gameWindow);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) {
                gameData.getKeys().setKey(GameKeys.LEFT, true);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                gameData.getKeys().setKey(GameKeys.RIGHT, true);
            }
            if (event.getCode().equals(KeyCode.UP)) {
                gameData.getKeys().setKey(GameKeys.UP, true);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, true);
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) {
                gameData.getKeys().setKey(GameKeys.LEFT, false);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                gameData.getKeys().setKey(GameKeys.RIGHT, false);
            }
            if (event.getCode().equals(KeyCode.UP)) {
                gameData.getKeys().setKey(GameKeys.UP, false);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, false);
            }

        });

        // Lookup all Game Plugins using ServiceLoader
        for (IGamePluginService iGamePlugin : getPluginServices()) {
            iGamePlugin.start(gameData, world);
        }
        for (Entity entity : world.getEntities()) {
            Polygon polygon = new Polygon(entity.getPolygonCoordinates());
            polygons.put(entity, polygon);
            gameWindow.getChildren().add(polygon);
        }

        render();

        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();

    }

    private void render() {
        new AnimationTimer() {
            private long then = System.nanoTime();
            @Override
            public void handle(long now) {
                long deltaTime = (now - then) / 1000000;
                then = now;
                update(deltaTime);
                draw();
                gameData.getKeys().update();
            }

        }.start();
    }

    private void update(long delta) {

        //Update delta time
        gameData.setDelta(delta);

        //Deletion of flagged entities
        for (Entity entity : world.getEntities()) {
            if (entity.getDeletionFlag()) {
                gameWindow.getChildren().remove(polygons.get(entity));
                gameWindow.getChildren().remove(colliders.get(entity));
                polygons.remove(entity);
                colliders.remove(entity);
                world.removeEntity(entity);
            }
        }

        // Update
        for (IEntityProcessingService entityProcessorService : getEntityProcessingServices()) {
            entityProcessorService.process(gameData, world);
        }
        for (IPostEntityProcessingService postEntityProcessorService : getPostEntityProcessingServices()) {
            postEntityProcessorService.process(gameData, world);
        }
    }

    private void draw() {
        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity);
            if (polygon == null) {
                polygon = new Polygon(entity.getPolygonCoordinates());
                polygons.put(entity, polygon);
                gameWindow.getChildren().add(polygon);
            }
            polygon.setTranslateX(entity.getPosition().x);
            polygon.setTranslateY(entity.getPosition().y);
            polygon.setRotate(entity.getRotation());

            int[] color = entity.getColor();
            polygon.setFill(javafx.scene.paint.Color.rgb(color[0], color[1], color[2]));

            if (showColliders)
                drawColliders(entity);
        }
    }

    private void drawColliders(Entity entity) {
        Circle collider = colliders.get(entity);
        if (collider == null) {
            collider = new Circle(0, 0, entity.getRadius());
            collider.setFill(javafx.scene.paint.Color.rgb(130, 225, 245, 0.5d));
            colliders.put(entity, collider);
            gameWindow.getChildren().add(collider);
        }

        collider.setTranslateX(entity.getPosition().x);
        collider.setTranslateY(entity.getPosition().y);
    }

    private Collection<? extends IGamePluginService> getPluginServices() {
        return ServiceLoader.load(pluginLayer, IGamePluginService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    private Collection<? extends IEntityProcessingService> getEntityProcessingServices() {
        return ServiceLoader.load(pluginLayer, IEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    private Collection<? extends IPostEntityProcessingService> getPostEntityProcessingServices() {
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
        Configuration pluginsConfiguration = ModuleLayer
                .boot()
                .configuration()
                .resolve(pluginsFinder, ModuleFinder.of(), plugins);

        // Create module layer
        pluginLayer = ModuleLayer
                .boot()
                .defineModulesWithOneLoader(pluginsConfiguration, ClassLoader.getSystemClassLoader());
    }
}
