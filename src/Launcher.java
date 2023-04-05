import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import physx.GridBasedHandler;
import physx.ParticleBasedHandler;
import physx.util.ObjectHandler;

import java.util.Map;


enum PhysicsStyle {
    GRID_BASED, PARTICLE_BASED
}

public class Launcher extends Application {
    int screenSizeX = 1280, screenSizeY = 960, amountObjects = 5000, threads = 4;
    boolean debug;
    PhysicsStyle style = PhysicsStyle.GRID_BASED;
    ObjectHandler objectHandler;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("""
                Thanks for trying out PhysicsFX!
                You can drag the screen using LeftMouseButton and zoom in/out using Scroll.
                """);
        Parameters parameters = getParameters();
        setupScreen(parameters);
        stage.setMaxWidth(screenSizeX);
        stage.setMaxHeight(screenSizeY);
        Canvas canvas = new Canvas(screenSizeX, screenSizeY);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setTitle("PhysicsFX Demo");
        stage.setScene(scene);
        scene.setCursor(Cursor.HAND);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenSizeX, screenSizeY);
        stage.show();
        if (style == PhysicsStyle.PARTICLE_BASED) {
            objectHandler = new ParticleBasedHandler(gc, amountObjects, screenSizeX, screenSizeY, threads, debug);
        } else if (style == PhysicsStyle.GRID_BASED) {
            objectHandler = new GridBasedHandler(gc, amountObjects, screenSizeX, screenSizeY, threads, debug);
        }
        stage.setOnCloseRequest(e -> System.exit(1));
        scene.setOnScroll(event -> objectHandler.inputH.handleScroll(event));
        scene.setOnMouseDragged(event -> objectHandler.inputH.handleMouse(event));
        scene.setOnMouseMoved(event -> objectHandler.inputH.handleMouse(event));
    }


    private void setupScreen(Parameters params) {
        Map<String, String> map = params.getNamed();
        if (map.get("x") != null && Integer.parseInt(map.get("x")) >= 640) {
            screenSizeX = Integer.parseInt(map.get("x"));
        }
        if (map.get("y") != null && Integer.parseInt(map.get("y")) >= 480) {
            screenSizeY = Integer.parseInt(map.get("y"));
        }
        if (map.get("n") != null && Integer.parseInt(map.get("n")) > 0) {
            amountObjects = Integer.parseInt(map.get("n"));
        }
        if (map.get("T") != null && Integer.parseInt(map.get("T")) > 0) {
            threads = Integer.parseInt(map.get("T"));
        }
        if (map.get("M") != null) {
            if (map.get("M").equals("debug")) {
                debug = true;
            }
        }
        if (map.get("style") != null) {
            if (map.get("style").equals("particle")) {
                style = PhysicsStyle.PARTICLE_BASED;
            }
        }
    }
}
