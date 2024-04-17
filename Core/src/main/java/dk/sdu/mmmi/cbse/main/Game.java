package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.CircleColliderSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RenderingSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.TransformSegment;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.net.http.HttpClient;
import java.util.stream.Collectors;

public class Game {
    private final boolean showColliders = false;
    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Map<Entity, Circle> colliders = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();

    private final List<IGamePluginService> gamePluginServices;
    private final List<IEntityProcessingService> entityProcessingServices;
    private final List<IPostEntityProcessingService> postEntityProcessingServices;

    private final Text scoreText = new Text("Score: 0");
    private final Text timeText = new Text("Time:" + 0);

    private final double fetchScoreCooldown = 0.2d;
    private double fetchScoreTimer = 0d;

    public Game(List<IGamePluginService> gamePluginServices, List<IEntityProcessingService> entityProcessingServiceList, List<IPostEntityProcessingService> postEntityProcessingServices) {
        this.gamePluginServices = gamePluginServices;
        this.entityProcessingServices = entityProcessingServiceList;
        this.postEntityProcessingServices = postEntityProcessingServices;
    }

    public void start(Stage window) throws Exception {
        gameWindow.setPrefSize(gameData.getDisplaySize().x, gameData.getDisplaySize().y);
        gameWindow.setStyle("-fx-background-color: #1e1e1e"); //Set background color

        scoreText.setX(20);
        scoreText.setY(40);
        scoreText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 24));
        scoreText.setFill(new Color(1f, 1f, 1f, 1f));
        gameWindow.getChildren().add(scoreText);

        timeText.setX(gameData.getDisplaySize().x - 180);
        timeText.setY(40);
        timeText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 24));
        timeText.setFill(new Color(1f, 1f, 1f, 1f));
        gameWindow.getChildren().add(timeText);

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
        for (IGamePluginService iGamePlugin : gamePluginServices) {
            iGamePlugin.start(gameData, world);
        }

        //Reset the score on game relaunch
        String url = String.format("http://localhost:6060/reset");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
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

        // Update
        for (IEntityProcessingService entityProcessorService : entityProcessingServices) {
            entityProcessorService.process(gameData, world);
        }
        for (IPostEntityProcessingService postEntityProcessorService : postEntityProcessingServices) {
            postEntityProcessorService.process(gameData, world);
        }

        garbageCollect();

        //Stop updating the score and time if no player is present
        if (world.getEntitiesWithTag(EntityTag.PLAYER).isEmpty())
            return;

        //Limit how often an HTTP request is send
        if (fetchScoreTimer < fetchScoreCooldown) {
            fetchScoreTimer += gameData.getDeltaSec();
            return;
        }

        fetchScoreTimer -= fetchScoreCooldown;

        String url = String.format("http://localhost:6060/get");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        int scoreValue = -1;

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            scoreValue = Integer.parseInt(responseBody);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

        scoreText.setText("Score: " + (scoreValue == -1 ? "Score not found!" : scoreValue));
        timeText.setText("Time: " + ((int)(gameData.getTime() * 10f)) / 10f);
    }

    /**
     * Entity must contain a RenderingSegment and a TransformSegment for the entity
     * polygon to be rendered.
     */
    private void draw() {
        for (Entity entity : world.getEntities()) {
            if (!entity.hasSegment(RenderingSegment.class))
                continue;
            if (!entity.hasSegment(TransformSegment.class))
                continue;

            RenderingSegment render = entity.getSegment(RenderingSegment.class);
            TransformSegment transform = entity.getSegment(TransformSegment.class);

            Polygon polygon = polygons.get(entity);
            if (polygon == null) {
                polygon = new Polygon(render.getPolygonCoordinates());
                polygons.put(entity, polygon);
                gameWindow.getChildren().add(polygon);
            }

            polygon.setTranslateX(transform.getPosition().x);
            polygon.setTranslateY(transform.getPosition().y);
            polygon.setRotate(transform.getRotation());

            int[] color = render.getColor();
            polygon.setFill(javafx.scene.paint.Color.rgb(color[0], color[1], color[2]));

            if (showColliders)
                drawColliders(entity);
        }
    }

    public void garbageCollect() {
        List<Entity> toDelete = new ArrayList<>();

        for (Entity key : polygons.keySet()) {
            if (world.getEntity(key.getID()) == null) {
                toDelete.add(key);
            }
        }

        for (Entity e : toDelete) {
            gameWindow.getChildren().remove(polygons.get(e));
            gameWindow.getChildren().remove(colliders.get(e));
            polygons.remove(e);
            colliders.remove(e);
        }
    }

    /**
     * @param entity
     * Entity must contain a CircleColliderSegment in addition to the needs of the standard rendering in
     * order for its collider to be rendered.
     * The check for a TransformSegment is done in the standard draw method.
     */
    private void drawColliders(Entity entity) {
        if (!entity.hasSegment(CircleColliderSegment.class))
            return;

        CircleColliderSegment collider = entity.getSegment(CircleColliderSegment.class);
        TransformSegment transform = entity.getSegment(TransformSegment.class);

        Circle colliderGizmo = colliders.get(entity);
        if (colliderGizmo == null) {
            colliderGizmo = new Circle(0, 0, collider.getRadius());
            colliderGizmo.setFill(javafx.scene.paint.Color.rgb(130, 225, 245, 0.5d));
            colliders.put(entity, colliderGizmo);
            gameWindow.getChildren().add(colliderGizmo);
        }

        colliderGizmo.setTranslateX(transform.getPosition().x);
        colliderGizmo.setTranslateY(transform.getPosition().y);
    }
}
