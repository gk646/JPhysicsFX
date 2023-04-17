package physx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import physx.objects.LightObject;
import physx.objects.MassObject;
import physx.util.ObjectHandler;
import physx.util.Vector;

import static physx.util.MathUtil.getGravityForceVector;

public class ParticleBasedHandler extends ObjectHandler {

    long[][] massValues = new long[SCREEN_X][SCREEN_Y];

    public ParticleBasedHandler(GraphicsContext gc, int countObjects, int sizeX, int sizeY, int threads, boolean debug) {
        this.gc = gc;
        this.debug = debug;
        SCREEN_X = sizeX;
        SCREEN_Y = sizeY;
        HALF_X = sizeX / 2;
        HALF_Y = sizeY / 2;
        count = countObjects;
        this.threadCount = threads;
        gravityForces = new Vector[countObjects];
        createParticles(countObjects);
        startThreads();
    }

    @Override
    protected void gravity(int start, int end) {
        Vector force;
        int len = particles.length;
        for (int i = start; i < end; i++) {
            for (int j = 0; j < len; j++) {
                if (i != j) {
                    force = getGravityForceVector(particles[i], particles[j]);
                    particles[i].vector.add(force);
                    particles[j].vector.subtract(force);
                }
            }
            particles[i].move();
        }
    }


    @Override
    protected void renderFrame() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_X, SCREEN_Y);
        gc.setFill(Color.RED);
        for (MassObject obj : particles) {
            gc.fillRect(HALF_X + (obj.posX + offsetX - obj.size / 2) / zoomLevel, HALF_Y + (obj.posY + offsetY - obj.size / 2) / zoomLevel, obj.size / zoomLevel, obj.size / zoomLevel);
        }
        drawUi(gc);
    }


    protected void createParticles(int amount) {
        int startX = 10000;
        particles = new MassObject[amount];
        for (int i = 0; i < amount; i++) {
            particles[i] = new LightObject(-startX + random.nextFloat(startX * 2), -startX + random.nextFloat(startX * 2));
        }
        //particles[0] = new SuperHeavyObject(500, 500, 100_000_00000L);
        for (int i = 0; i < gravityForces.length; i++) {
            gravityForces[i] = new Vector();
        }
    }

    private void drawUi(GraphicsContext gc) {
        super.drawUi();
        gc.fillText("ParticleBased: ", 25, 35);
    }
}

