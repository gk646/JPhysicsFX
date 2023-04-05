package physx.util;

public class Vector {

    public float x, y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector() {

    }

    public void add(Vector vec) {
        x += vec.x;
        y += vec.y;
    }

    public void subtract(Vector vec) {
        x -= vec.x;
        y -= vec.y;
    }

    @Override
    public String toString() {
        return "(X:" + x + " / Y: " + y + ")";
    }

    public void set(int i, int i1) {
        x = i;
        y = i1;
    }
}
