package physx.objects;

import physx.util.Vector;

abstract public class MassObject {

    public Vector vector = new Vector();
    public float posX, posY, mass;
    public int size;


    public void move() {

    }

    @Override
    public String toString() {
        return "(X:" + posX + " / Y: " + posY + ")";
    }
}
