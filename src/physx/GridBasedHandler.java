package physx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.util.Duration;
import physx.objects.LightObject;
import physx.objects.MassObject;
import physx.util.ObjectHandler;
import physx.util.QuadTree;
import physx.util.Vector;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static physx.util.MathUtil.getGravityForceVectorForPixel;


public class GridBasedHandler extends ObjectHandler {
    public Box[] groupBox = new Box[count];
    QuadTree quadTree;
    static int SUBPIXEL_PER_PIXEL = 1;
    static int GRID_SIZE_X = 1280, GRID_SIZE_Y = 960, MAX_PARTICLES_PER_PIXEL = 100;
    int[][] massPerPixel = new int[GRID_SIZE_X][GRID_SIZE_Y];
    LightObject[][][] particles_per_pixel = new LightObject[GRID_SIZE_X][GRID_SIZE_Y][MAX_PARTICLES_PER_PIXEL];

    public GridBasedHandler(int countObjects, int sizeX, int sizeY, int threadCount, boolean debug, GraphicsContext gc) {
        this.debug = debug;
        this.gc = gc;
        quadTree = new QuadTree(0, 0, SCREEN_X, SCREEN_Y, 100, 0);
        SCREEN_X = sizeX;
        SCREEN_Y = sizeY;
        HALF_X = sizeX / 2;
        HALF_Y = sizeY / 2;
        count = countObjects;
        this.threadCount = threadCount;
        gravityForces = new Vector[countObjects];
        createParticles(countObjects);
    }


    @Override
    protected void gravity(int start, int end) {
        Vector force = new Vector();
        for (int y = 0; y < SCREEN_Y; y++) {
            for (int x = start; x < end; x++) {

                for (int i = Math.max(y - 25, 0); i < Math.min(SCREEN_Y, y + 25); i++) {
                    for (int j = Math.max(x - 25, 0); j < Math.min(SCREEN_X, x + 25); j++) {
                        force.add(getGravityForceVectorForPixel(x, y, j, i, massPerPixel[x][y], massPerPixel[j][i]));
                    }
                }

                for (MassObject obj : particles_per_pixel[x][y]) {
                    if (obj != null) {
                        obj.move(force);
                        obj = null;
                    } else {
                        break;
                    }
                }
                force.x = 0;
                force.y = 0;
            }
        }
    }


    private void sortParticlesToPixelGrid() {
        for (int y = 0; y < SCREEN_Y; y++) {
            for (int x = 0; x < SCREEN_X; x++) {
                massPerPixel[x][y] = 0;
            }
        }
        for (LightObject obj : particles_light) {
            int posX = (int) obj.posX;
            int posY = (int) obj.posY;
            if (posY > 0 && posY < GRID_SIZE_Y && posX > 0 && posX < GRID_SIZE_X) {
                massPerPixel[posX][posY] += obj.mass;
                for (int i = 0; i < MAX_PARTICLES_PER_PIXEL; i++) {
                    particles_per_pixel[posX][posY][i] = obj;
                }
            }
        }
    }


    @Override
    protected void renderFrame() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_X, SCREEN_Y);
        gc.setFill(Color.RED);
        for (MassObject obj : particles_light) {
            gc.fillRect(obj.posX, obj.posY, 1, 1);
        }
        drawUi(gc);
    }

    @Override
    protected void createParticles(int amount) {
        particles_light = new LightObject[amount];
        for (int i = 0; i < amount; i++) {
            particles_light[i] = new LightObject(random.nextFloat(SCREEN_X), random.nextFloat(SCREEN_Y));
        }
    }

    @Override
    protected void createThreads(int numThreads, int span, float timing) {
        final CyclicBarrier barrier = new CyclicBarrier(numThreads + 1);
        try {
            span = GRID_SIZE_X / numThreads;
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        sortParticlesToPixelGrid();
                        barrier.await();
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

            for (int i = 0; i < numThreads; i++) {
                int start = i * span;
                int end = start + span;
                Thread task = new Thread(() -> {
                    try {
                        while (true) {
                            gravity(start, end);
                            barrier.await();
                        }
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                });
                task.start();
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void startThreads() {
        {
            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.008), ae -> renderFrame());
            timeline.getKeyFrames().add(kf);
            timeline.play();
        }
        int span = count / threadCount;
        float timing = 60;
        if (debug) {
            createStatisticThread(timing);
        }
        createThreads(threadCount, span, timing);
    }

    private void drawUi(GraphicsContext gc) {
        super.drawUi();
        gc.fillText("GridBased: ", 25, 35);
    }
}
