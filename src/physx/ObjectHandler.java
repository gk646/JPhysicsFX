package physx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import physx.objects.HeavyObject;
import physx.objects.LightObject;
import physx.objects.MassObject;
import physx.util.InputHandler;
import physx.util.Vector;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ObjectHandler {
    public static final double G = 6.67430 * Math.pow(10, -11);
    GraphicsContext gc;
    public InputHandler inputH = new InputHandler(this);
    MassObject[] particles;

    Vector[] gravityForces;
    Vector moveVec = new Vector();
    BigInteger perfTest = BigInteger.ZERO;
    public static final SecureRandom random = new SecureRandom();
    int perfCounter;
    int count;
    int threads;
    public int offsetX;
    public int offsetY;
    public int zoomLevel = 15;
    boolean debug;
    public static int SCREEN_X = 1280, SCREEN_Y = 960, HALF_X = 640, HALF_Y = 480;


    public ObjectHandler(GraphicsContext gc, int countObjects, int sizeX, int sizeY, int threads, boolean debug) {
        this.gc = gc;
        this.debug = debug;
        SCREEN_X = sizeX;
        SCREEN_Y = sizeY;
        HALF_X = sizeX / 2;
        HALF_Y = sizeY / 2;
        count = countObjects;
        this.threads = threads;
        gravityForces = new Vector[countObjects];
        createParticles(countObjects);
        startThreads();
    }

    private void renderFrame() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_X, SCREEN_Y);
        gc.setFill(Color.RED);
        for (MassObject obj : particles) {
            gc.fillRect(HALF_X + (obj.posX + offsetX) / zoomLevel, HALF_Y + (obj.posY + offsetY) / zoomLevel, obj.size / zoomLevel, obj.size / zoomLevel);
        }
        drawUi(gc);
    }

    private void drawUi(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        int y = 25;
        gc.fillText("Threads: " + threads, 25, 25);
        gc.fillText("Particles: " + count, 25, y * 2);
        gc.fillText("PositionX: " + offsetX, 25, y * 3);
        gc.fillText("PositionY: " + offsetY, 25, y * 4);
        gc.fillText("Zoom: " + zoomLevel, 25, y * 5);
    }

    private void gravity(int start, int end) {
        Vector force;
        for (int i = start; i < end - 1; i++) {
            for (int j = start + 1; j < end; j++) {
                if (i != j) {
                    force = getGravityForceVector(particles[i], particles[j]);
                    particles[i].vector.add(force);
                    particles[j].vector.subtract(force);
                }
            }
            particles[i].move();
        }
        particles[end - 1].move();
    }

    private Vector getGravityForceVector(MassObject obj, MassObject obj2) {
        double F;
        double dist = getSqDist(obj.posX, obj2.posX, obj.posY, obj2.posY);
        double sqrtDist = Math.sqrt(dist);
        F = G * (obj.mass * obj2.mass) / dist;
        return new Vector((float) ((obj2.posX - obj.posX) / sqrtDist * F), (float) ((obj2.posY - obj.posY) / sqrtDist * F));
    }


    private void startThreads() {
        { //START RENDER THREAD
            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.008), ae -> renderFrame());
            timeline.getKeyFrames().add(kf);
            timeline.play();
        }
        int span = count / threads;
        float timing = 60;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        for (int i = 0; i < threads; i++) {
            if (i == 0 && debug) {
                createStatisticThread(i, span, timing);
                continue;
            }
            createThread(i, span, timing,barrier);
        }
    }


    private void createParticles(int amount) {
        particles = new MassObject[amount];
        for (int i = 0; i < amount; i++) {
            if (i % 500 == 0) {
                particles[i] = new HeavyObject(-25000 + random.nextFloat(50000), -25000 + random.nextFloat(50000));
                continue;
            }
            particles[i] = new LightObject(-25000 + random.nextFloat(50000), -25000 + random.nextFloat(50000));
        }
        for (int i = 0; i < gravityForces.length; i++) {
            gravityForces[i] = new Vector();
        }
    }


    private double getSqDist(float x1, float x2, float y1, float y2) {
        return Math.max(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2), 15);
    }

    private float sqrt(float x) {
        double xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xhalf * x * x);
        return 1 / x;
    }

    private Vector multiplyVector(Vector vec, Vector vec2) {
        return new Vector(vec.x * vec2.x, vec.y * vec2.y);
    }

    public static String formatWithUnderscores(long number) {
        String numStr = Long.toString(number);
        int length = numStr.length();
        StringBuilder formatted = new StringBuilder();
        int count = 0;

        for (int i = length - 1; i >= 0; i--) {
            formatted.append(numStr.charAt(i));
            count++;
            if (count % 3 == 0 && i > 0) {
                formatted.append('_');
            }
        }
        return formatted.reverse().toString();
    }

    private void createThread(int i, int span, float timing, CyclicBarrier barrier) {
        Thread physicThread2 = new Thread(() -> {
            int start = i * span;
            int end = start + span;
            long interval = (long) (1_000.0f / timing);
            while (true) {
                long startTime = System.currentTimeMillis();
                gravity(start, end);

                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = interval - elapsedTime;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        physicThread2.start();
    }

    private void createStatisticThread(int i, int span, float timing) {
        Thread physicThread1 = new Thread(() -> {
            int start = i * span;
            int end = start + span;
            long lastTime1 = System.currentTimeMillis();
            float logicCounter = 1_000.0f / timing;
            float difference = 0;
            long firstTimeGate1;
            while (true) {
                firstTimeGate1 = System.currentTimeMillis();
                difference += (firstTimeGate1 - lastTime1) / logicCounter;
                lastTime1 = firstTimeGate1;
                if (difference >= 1) {
                    long time = System.nanoTime();
                    gravity(start, end);
                    long diff = System.nanoTime() - time;
                    perfTest = perfTest.add(BigInteger.valueOf(diff));
                    perfCounter++;
                    if (perfCounter % 150 == 0) {
                        System.out.println(formatWithUnderscores(perfTest.divide(BigInteger.valueOf(perfCounter)).longValue()));
                        perfTest= BigInteger.ZERO;
                        perfCounter = 0;
                    }
                    difference = 0;
                }
            }
        });
        physicThread1.start();
    }
}
