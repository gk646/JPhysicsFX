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

    public static Vector getGravityForceVectorForPixel(int x, int y, int goalx, int goaly, int mass, int goalmass) {
        double F;
        int dx = goalx - x, dy = goaly - y;
        double dist = (dx * dx) + (dy * dy);
        double sqrtDist = Math.sqrt(dist);
        F = G * (mass * goalmass) / dist;
        return new Vector((float) ((dx) / sqrtDist * F), (float) ((dy) / sqrtDist * F));
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

    public static int heatMapColor(int value, int minValue, int maxValue) {
        float normalizedValue = (float) (value - minValue) / (maxValue - minValue);

        int r, g, b;

        if (normalizedValue < 0.5f) {
            r = 0;
            g = (int) (normalizedValue * 2.0f * 255);
            b = 255 - g;
        } else {
            r = (int) ((normalizedValue - 0.5f) * 2.0f * 255);
            g = 255 - r;
            b = 0;
        }

        int a = 255; // Alpha channel (fully opaque)
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
