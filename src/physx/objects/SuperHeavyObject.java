package physx.objects;

import physx.ParticleBasedHandler;

public class SuperHeavyObject extends MassObject {


    public SuperHeavyObject(float posX, float posY, long mass) {
        this.posX = posX;
        this.posY = posY;
        this.mass = mass;
        size = (int) (mass / 100_000_0000000000000L);
    }

    public void move() {

    }
}

