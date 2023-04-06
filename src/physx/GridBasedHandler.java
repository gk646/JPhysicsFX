package physx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Box;
import physx.objects.LightObject;
import physx.objects.MassObject;
import physx.util.ObjectHandler;
import physx.util.QuadTree;
import physx.util.Vector;


public class GridBasedHandler extends ObjectHandler {
    public Box[] groupBox = new Box[count];
    QuadTree quadTree;
    int[][] containers = new int[SCREEN_X][SCREEN_Y];

    public GridBasedHandler(int countObjects, int sizeX, int sizeY, int threads, boolean debug) {
        this.debug = debug;
        quadTree = new QuadTree(0, 0, SCREEN_X, SCREEN_Y, 100, 0);
        SCREEN_X = sizeX;
        SCREEN_Y = sizeY;
        HALF_X = sizeX / 2;
        HALF_Y = sizeY / 2;
        count = countObjects;
        this.threads = threads;
        gravityForces = new Vector[countObjects];
        createParticles(countObjects);
    }


    @Override
    protected void gravity(int start, int end) {
        for (int y = 0; y < SCREEN_Y; y++) {
            for (int x = 0; x < SCREEN_X; x++) {

            }
        }


    }

    @Override
    protected void renderFrame() {
        Box particle;
        MassObject obj;
        for (int y = 0; y < SCREEN_Y; y++) {
            for (int x = 0; x < SCREEN_X; x++) {

            }
        }
        int len = groupBox.length;

        for (int i = 0; i < len; i++) {
            particle = groupBox[i];
            obj = particles[i];
            particle.setTranslateX(obj.posX++);
            particle.setTranslateY(obj.posY++);
        }
    }


    @Override
    protected void createParticles(int amount) {
        particles = new MassObject[amount];
        for (int i = 0; i < amount; i++) {
            particles[i] = new LightObject(random.nextFloat(SCREEN_X), random.nextFloat(SCREEN_Y));
        }
        for (int i = 0; i < SCREEN_X; i++) {
            for (int j = 0; j < SCREEN_Y; j++) {

            }
        }
    }


    private void drawUi(GraphicsContext gc) {
        super.drawUi();
        gc.fillText("GridBased: ", 25, 35);
    }
}
