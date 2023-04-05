package physx.util;


import physx.objects.LightObject;


class Point {
    int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Rectangle {
    int x, y, width, height;

    Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}


public class QuadTree {

    QuadTree[] subTrees = new QuadTree[4];
    int capacity;
    Rectangle boundary;

    QuadTree(Rectangle boundary, int capacity) {
        this.capacity = capacity;
        this.boundary = boundary;
    }

    public void insert(LightObject obj) {

    }


    private void subdivide() {

    }
}
