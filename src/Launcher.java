import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import physx.GridBasedHandler;
import physx.ParticleBasedHandler;
import physx.objects.MassObject;
import physx.util.ObjectHandler;

import java.util.Map;

import static physx.util.ObjectHandler.SCREEN_X;
import static physx.util.ObjectHandler.SCREEN_Y;


enum PhysicsStyle {
    GRID_BASED, PARTICLE_BASED
}

public class Launcher extends Application {
    int screenSizeX = 1280, screenSizeY = 960, amountObjects = 5000, threads = 4;
    boolean debug;
    PhysicsStyle style = PhysicsStyle.GRID_BASED;
    ObjectHandler objectHandler;

    public static void main(String[] args) {
        System.setProperty("prism.forceGPU", "true");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("PhysicsFX Demo");
        System.out.println("""
                Thanks for trying out PhysicsFX!
                You can drag the screen using LeftMouseButton and zoom in/out using Scroll.
                """);
        Parameters parameters = getParameters();
        setupScreen(parameters);
        stage.setMaxWidth(screenSizeX);
        stage.setMaxHeight(screenSizeY);
        if (style == PhysicsStyle.PARTICLE_BASED) {
            Canvas canvas = new Canvas(screenSizeX, screenSizeY);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            Group root = new Group();
            Scene scene = new Scene(root);
            scene.setCursor(Cursor.HAND);
            root.getChildren().add(canvas);
            stage.setScene(scene);
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, screenSizeX, screenSizeY);
            objectHandler = new ParticleBasedHandler(gc, amountObjects, screenSizeX, screenSizeY, threads, debug);
            stage.show();
            stage.setOnCloseRequest(e -> System.exit(1));
            scene.setOnScroll(event -> objectHandler.inputH.handleScroll(event));
            scene.setOnMouseDragged(event -> objectHandler.inputH.handleMouse(event));
            scene.setOnMouseMoved(event -> objectHandler.inputH.handleMouse(event));
        } else if (style == PhysicsStyle.GRID_BASED) {
            objectHandler = new GridBasedHandler(amountObjects, screenSizeX, screenSizeY, threads, debug);
            Group particlesGroup = new Group();
            ((GridBasedHandler) objectHandler).groupBox = new Box[amountObjects];
            for (int i = 0; i < amountObjects; i++) {
                MassObject obj = objectHandler.particles[i];
                Box particle = new Box(1, 1, 0.001); // Very small z-dimension
                particle.setTranslateX(obj.posX);
                particle.setTranslateY(obj.posY);
                particle.setTranslateZ(0);
                particle.setMaterial(new PhongMaterial(Color.RED));
                ((GridBasedHandler) objectHandler).groupBox[i] = particle;

                particlesGroup.getChildren().add(particle);
            }

            SubScene subScene = new SubScene(particlesGroup, SCREEN_X, SCREEN_Y, false, null);
            ParallelCamera parallelCamera = new ParallelCamera();

            parallelCamera.setTranslateZ(-1);

            subScene.setCamera(parallelCamera);

            Group root = new Group(subScene);
            Scene scene = new Scene(root, SCREEN_X, SCREEN_Y);
            stage.setScene(scene);
            stage.show();

            objectHandler.startThreads();
        }
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
