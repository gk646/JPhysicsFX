package physx.objects;

import physx.ObjectHandler;


public class LightObject extends MassObject {

    public LightObject(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        mass = ObjectHandler.random.nextFloat(100_000_00);
        size = (int) (mass / 100_000);
    }


    public void move() {
        posX += vector.x;
        posY += vector.y;
    }
}
