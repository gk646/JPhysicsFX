package physx.objects;

import physx.util.Vector;

abstract public class MassObject {

    public Vector vector = new Vector();
    public float posX, posY;
    public long mass;
    public int size;


    public void move() {

    }

    public void move(Vector vector) {

    }
    public void moveMax() {

    }

    @Override
    public String toString() {
        return "(X:" + posX + " / Y: " + posY + ")";
    }
}
