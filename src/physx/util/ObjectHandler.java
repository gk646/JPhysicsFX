package physx.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import physx.objects.MassObject;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

abstract public class ObjectHandler {

    protected GraphicsContext gc;
    public InputHandler inputH = new InputHandler(this);
    public MassObject[] particles;

    protected Vector[] gravityForces;

    protected BigInteger perfTest = BigInteger.ZERO;
    public static final SecureRandom random = new SecureRandom();
    protected int perfCounter;
    protected int count;
    protected int threads;
    public int offsetX;
    public int offsetY;
    public int zoomLevel = 15;
    protected boolean debug;
    public static int SCREEN_X = 1280, SCREEN_Y = 960, HALF_X = 640, HALF_Y = 480;


    protected void drawUi() {
        gc.setFill(Color.WHITE);
        int y = 75;
        int dist = 22;
        gc.fillText("Threads: " + threads, 25, y);
        gc.fillText("Particles: " + count, 25, y + dist);
        gc.fillText("PositionX: " + offsetX, 25, y + dist * 2);
        gc.fillText("PositionY: " + offsetY, 25, y + dist * 3);
        gc.fillText("Zoom: " + zoomLevel, 25, y + dist * 4);
    }

    protected void renderFrame() {

    }

    public void startThreads() {
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
            //createThread(i, span, timing, barrier);
        }
        createThreads(threads, span, timing);
    }

    protected void gravity(int start, int end) {

    }

    protected void createThread(int i, int span, float timing, CyclicBarrier barrier) {
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
                if (difference > 1) {
                    gravity(start, end);
                    difference = 0;
                }
            }
        });
        physicThread1.start();
    }

    protected void createStatisticThread(int i, int span, float timing) {
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
                        perfTest = BigInteger.ZERO;
                        perfCounter = 0;
                    }
                    difference = 0;
                }
            }
        });
        physicThread1.start();
    }

    protected String formatWithUnderscores(long number) {
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

    protected void createParticles(int amount) {

    }


    private static float hueToRgb(float p, float q, float t) {
        if (t < 0.0f) t += 1.0f;
        if (t > 1.0f) t -= 1.0f;
        if (t < 1.0f / 6.0f) return p + (q - p) * 6.0f * t;
        if (t < 1.0f / 2.0f) return q;
        if (t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
        return p;
    }

    public static Color valueToColor(float value, float minValue, float maxValue) {
        float normalizedValue = (value - minValue) / (maxValue - minValue);
        float h = (1.0f - normalizedValue) * 240.0f / 360.0f; // Hue (0 to 240)
        float s = 1.0f; // Saturation (1.0)
        float l = 0.5f; // Lightness (0.5)

        float q = l < 0.5f ? l * (1.0f + s) : l + s - l * s;
        float p = 2 * l - q;

        float r = hueToRgb(p, q, h + 1.0f / 3.0f);
        float g = hueToRgb(p, q, h);
        float b = hueToRgb(p, q, h - 1.0f / 3.0f);

        return Color.color(r, g, b);
    }

    protected void createThreads(int numThreads, int span, float timing) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CyclicBarrier barrier = new CyclicBarrier(numThreads);

        for (int i = 0; i < numThreads; i++) {
            int start = i * span;
            int end = start + span;

            Runnable task = () -> {
                while (true) {
                    gravity(start, end);

                    try {
                        barrier.await();
                        long interval = (long) (1_000.0f / timing);
                        TimeUnit.MILLISECONDS.sleep(interval);
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            };
            executor.submit(task);
        }
    }
}