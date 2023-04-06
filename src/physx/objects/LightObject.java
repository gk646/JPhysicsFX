package physx.objects;

import physx.ParticleBasedHandler;


public class LightObject extends MassObject {

    public LightObject(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        mass = ParticleBasedHandler.random.nextInt(100_000_00);
        size = (int) (mass / 100_000);

    }


    public void move() {
        posX += vector.x;
        posY += vector.y;
    }

    public void moveMax() {
        posX += vector.x;
        posY += vector.y;
        posX = Math.max(Math.min(1280, posX), 0);
        posY = Math.max(Math.min(960, posY), 0);
    }
}
