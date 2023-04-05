package physx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import physx.objects.HeavyObject;
import physx.objects.LightObject;
import physx.objects.MassObject;
import physx.util.ObjectHandler;
import physx.util.Vector;

import static physx.util.MathUtil.getGravityForceVector;

public class GridBasedHandler extends ObjectHandler {

    public GridBasedHandler(GraphicsContext gc, int countObjects, int sizeX, int sizeY, int threads, boolean debug) {
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


    @Override
    protected void gravity(int start, int end) {
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

    @Override
    protected void renderFrame() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_X, SCREEN_Y);
        gc.setFill(Color.RED);
        for (MassObject obj : particles) {
            gc.fillRect(HALF_X + (obj.posX + offsetX) / zoomLevel, HALF_Y + (obj.posY + offsetY) / zoomLevel, obj.size / zoomLevel, obj.size / zoomLevel);
        }
        drawUi(gc);
    }


    @Override
    protected void createParticles(int amount) {
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


    private void drawUi(GraphicsContext gc) {
        super.drawUi();
        gc.fillText("GridBased: ", 25, 35);
    }
}
