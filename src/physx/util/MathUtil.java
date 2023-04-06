package physx.util;

import physx.objects.MassObject;

public class MathUtil {
    public static final double G = 6.67430 * Math.pow(10, -11);

    public static double getSqDist(float x1, float x2, float y1, float y2) {
        return Math.max(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2), 5);
    }

    public static Vector getGravityForceVector(MassObject obj, MassObject obj2) {
        double F;
        double dist = getSqDist(obj.posX, obj2.posX, obj.posY, obj2.posY);
        double sqrtDist = Math.sqrt(dist);
        F = G * (obj.mass * obj2.mass) / dist;
        return new Vector((float) ((obj2.posX - obj.posX) / sqrtDist * F), (float) ((obj2.posY - obj.posY) / sqrtDist * F));
    }


    private float sqrt(float x) {
        double xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xhalf * x * x);
        return 1 / x;
    }

    private Vector multiplyVector(Vector vec, Vector vec2) {
        return new Vector(vec.x * vec2.x, vec.y * vec2.y);
    }
}
