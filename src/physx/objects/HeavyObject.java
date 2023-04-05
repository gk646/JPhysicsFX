package physx.objects;

import physx.ObjectHandler;
import physx.util.Vector;


public class HeavyObject extends MassObject {


    public HeavyObject(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        mass = ObjectHandler.random.nextFloat(100_000_0000,100_000_00000L);
        size = (int) (mass / 100_00000);
    }


    public void move() {
        posX += vector.x;
        posY += vector.y;
    }


}
