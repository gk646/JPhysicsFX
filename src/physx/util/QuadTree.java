package physx.util;


import physx.objects.MassObject;

import java.util.ArrayList;
import java.util.List;


class Rectangle {
    int x, y, width, height;

    Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(float x, float y) {
        return !(x < this.x || x > this.x + width || y < this.y || y > this.y + height);
    }

    public boolean intersects(Rectangle other) {
        return this.x < other.x + other.width && this.x + this.width > other.x &&
                this.y < other.y + other.height && this.y + this.height > other.y;
    }
}


public class QuadTree {
    private final static int MAX_DEPTH = 6;
    QuadTree[] subTrees;
    ArrayList<MassObject> objects = new ArrayList<>();
    int capacity, depth;
    Rectangle bounds;

    public QuadTree(int x, int y, int width, int height, int capacity, int depth) {
        this.capacity = capacity;
        this.depth = depth;
        this.bounds = new Rectangle(x, y, width, height);
        subTrees = null;
    }

    public List<MassObject> search(int x, int y, int width, int height) {
        Rectangle searchArea = new Rectangle(x, y, width, height);
        ArrayList<MassObject> found = new ArrayList<>();
        if (!bounds.intersects(searchArea)) {
            return found;
        }

        if (subTrees == null) {
            for (MassObject obj : objects) {
                if (searchArea.contains(obj.posX, obj.posY)) {
                    found.add(obj);
                }
            }
            return found;
        }

        for (QuadTree child : subTrees) {
            found.addAll(child.search(searchArea.x, searchArea.y, searchArea.width, searchArea.height));
        }

        return found;
    }

    public boolean insert(MassObject obj) {
        if (!bounds.contains(obj.posX, obj.posY)) {
            return false;
        }

        if (objects.size() < capacity || depth == MAX_DEPTH) {
            objects.add(obj);
            return true;
        }
        if (subTrees == null) {
            subdivide();
        }
        for (QuadTree child : subTrees) {
            if (child.insert(obj)) {
                return true;
            }
        }
        return false;
    }


    private void subdivide() {
        int childWidth = bounds.width / 2;
        int childHeight = bounds.height / 2;
        subTrees = new QuadTree[4];
        subTrees[0] = new QuadTree(bounds.x, bounds.y, childWidth, childHeight, capacity, depth + 1);
        subTrees[1] = new QuadTree(bounds.x + childWidth, bounds.y, childWidth, childHeight, capacity, depth + 1);
        subTrees[2] = new QuadTree(bounds.x, bounds.y + childHeight, childWidth, childHeight, capacity, depth + 1);
        subTrees[3] = new QuadTree(bounds.x + childWidth, bounds.y + childHeight, childWidth, childHeight, capacity, depth + 1);
        for (MassObject obj : objects) {
            for (QuadTree child : subTrees) {
                if (child.insert(obj)) {
                    break;
                }
            }
        }
        objects.clear();
    }
}
