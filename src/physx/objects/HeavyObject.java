package physx.objects;

import physx.ParticleBasedHandler;


public class HeavyObject extends MassObject {


    public HeavyObject(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        mass = ParticleBasedHandler.random.nextLong(100_000_00, 100_000_000);
        size = (int) (mass / 100_000);
    }



    public void move() {
        posX += vector.x;
        posY += vector.y;
    }
}
