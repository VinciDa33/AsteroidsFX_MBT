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

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.net.http.HttpClient;
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

    /**
     * Constructor with enforced constructor injection
     * @param gamePluginServices
     * @param entityProcessingServiceList
     * @param postEntityProcessingServices
     */
    public Game(List<IGamePluginService> gamePluginServices, List<IEntityProcessingService> entityProcessingServiceList, List<IPostEntityProcessingService> postEntityProcessingServices) {
        this.gamePluginServices = gamePluginServices;
        this.entityProcessingServices = entityProcessingServiceList;
        this.postEntityProcessingServices = postEntityProcessingServices;
    }


    /**
     * Game start method. Should be run only once, when the game is started
     * @param window
     * @throws Exception
     */
    public void start(Stage window) throws Exception {
        //Set window size and background color
        gameWindow.setPrefSize(gameData.getDisplaySize().x, gameData.getDisplaySize().y);
        gameWindow.setStyle("-fx-background-color: #1e1e1e"); //Set background color

        //Setup text element for displaying the score
        scoreText.setX(20);
        scoreText.setY(40);
        scoreText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 24));
        scoreText.setFill(new Color(1f, 1f, 1f, 1f));
        gameWindow.getChildren().add(scoreText);

        //Setup text element for displaying the game time
        timeText.setX(gameData.getDisplaySize().x - 180);
        timeText.setY(40);
        timeText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 24));
        timeText.setFill(new Color(1f, 1f, 1f, 1f));
        gameWindow.getChildren().add(timeText);

        //Setup keyboard input listeners for relevant keys
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

        //Loop over all IGamePluginService's and execute their start methods
        for (IGamePluginService iGamePlugin : gamePluginServices) {
            iGamePlugin.start(gameData, world);
        }

        //Reset the score on game relaunch (Relevant since the score is stored in a separate Spring Boot application)
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

        //Start the render method
        render();

        //Enable the application window using the scene containing the gameWindow
        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();

    }

    /**
     * Starts the main loop for the application
     */
    private void render() {
        //Create and start animation timer as main game loop
        new AnimationTimer() {
            private long then = System.nanoTime();
            @Override
            public void handle(long now) {

                //Calculate the time between this and the previous frame (In milliseconds)
                long deltaTime = (now - then) / 1000000;
                then = now;

                //Run the update and draw methods
                update(deltaTime);
                draw();

                //Update the game keys
                gameData.getKeys().update();
            }

        }.start();
    }

    /**
     * Handles the general game code and running of different modules
     * @param delta
     */
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

        //Run the garbage collector to remove no longer needed polygons
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

        //Retrieve the score from the scoring microservice.
        String url = String.format("http://localhost:6060/get");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        //Default value in case the endpoint is not found
        int scoreValue = -1;

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            scoreValue = Integer.parseInt(responseBody);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

        //Update text labels
        scoreText.setText("Score: " + (scoreValue == -1 ? "Score not found!" : scoreValue));
        timeText.setText("Time: " + ((int)(gameData.getTime() * 10f)) / 10f);
    }

    /**
     * Handles the graphical logic of adding and removing polygons.
     * Entity must contain a RenderingSegment and a TransformSegment for the entity
     * polygon to be rendered.
     */
    private void draw() {
        //Run for every entity in the world object
        for (Entity entity : world.getEntities()) {
            //Check if needed segments are present
            if (!entity.hasSegment(RenderingSegment.class))
                continue;
            if (!entity.hasSegment(TransformSegment.class))
                continue;

            //Get the relevant entity segments
            RenderingSegment render = entity.getSegment(RenderingSegment.class);
            TransformSegment transform = entity.getSegment(TransformSegment.class);

            //Check if entity already has a polygon, if not create one and add it
            Polygon polygon = polygons.get(entity);
            if (polygon == null) {
                polygon = new Polygon(render.getPolygonCoordinates());
                polygons.put(entity, polygon);
                gameWindow.getChildren().add(polygon);
            }

            //Update polygon position and rotation
            polygon.setTranslateX(transform.getPosition().x);
            polygon.setTranslateY(transform.getPosition().y);
            polygon.setRotate(transform.getRotation());

            //Update polygon color
            int[] color = render.getColor();
            polygon.setFill(javafx.scene.paint.Color.rgb(color[0], color[1], color[2]));

            //If visible colliders are enabled, run the draw colliders method
            if (showColliders)
                drawColliders(entity);
        }
    }


    /**
     * Checks weather a polygon no longer maps to an entity within the world object.
     * If a polygon no longer has an entity, it is removed from all relevant maps.
     */
    public void garbageCollect() {
        //List of entities ready for deletion
        List<Entity> toDelete = new ArrayList<>();

        //Loop over all entity keys in the polygons keyset. If a key does not exist in the
        //world object, add it for deletion
        //(Objects are not deleted immediately to ensure no deletion happens during iteration)
        for (Entity key : polygons.keySet()) {
            if (world.getEntity(key.getID()) == null) {
                toDelete.add(key);
            }
        }

        //Delete all polygons listed
        for (Entity e : toDelete) {
            gameWindow.getChildren().remove(polygons.get(e));
            gameWindow.getChildren().remove(colliders.get(e));
            polygons.remove(e);
            colliders.remove(e);
        }
    }

    /**
     * Entity must contain a CircleColliderSegment in addition to the needs of the standard rendering in
     * order for its collider to be rendered.
     * The check for a TransformSegment is done in the standard draw method.
     * @param entity
     */
    private void drawColliders(Entity entity) {
        //Check if needed segment is present
        if (!entity.hasSegment(CircleColliderSegment.class))
            return;

        //Get relevant segments
        CircleColliderSegment collider = entity.getSegment(CircleColliderSegment.class);
        TransformSegment transform = entity.getSegment(TransformSegment.class);

        //Check if entity already has a collision circle, if not create one and add it
        Circle colliderGizmo = colliders.get(entity);
        if (colliderGizmo == null) {
            colliderGizmo = new Circle(0, 0, collider.getRadius());
            colliderGizmo.setFill(javafx.scene.paint.Color.rgb(130, 225, 245, 0.5d));
            colliders.put(entity, colliderGizmo);
            gameWindow.getChildren().add(colliderGizmo);
        }

        //Update collision circle position
        colliderGizmo.setTranslateX(transform.getPosition().x);
        colliderGizmo.setTranslateY(transform.getPosition().y);
    }
}
