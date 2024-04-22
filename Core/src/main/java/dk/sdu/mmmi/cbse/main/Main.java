package dk.sdu.mmmi.cbse.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main extends Application {
    public static void main(String[] args) {
        //Launch JavaFX with this Application class
        launch(Main.class);
    }

    @Override
    public void start(Stage window) throws Exception {

        //Setup spring configuration using the ModuleConfig class
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ModuleConfig.class);

        //Print the name of all Spring beans
        for (String beanName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }

        //Use Spring to obtain a Game object and start the game
        Game game = ctx.getBean(Game.class);
        game.start(window);
    }
}
