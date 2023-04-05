package physx.util;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;


import java.awt.Point;

public class InputHandler {

    ObjectHandler objH;
    Point lastMousePos = new Point(0, 0);

    public InputHandler(ObjectHandler objH) {
        this.objH = objH;
    }


    public void handleScroll(ScrollEvent event) {
        if (event.getDeltaY() > 0) {
            objH.zoomLevel--;
        } else {
            objH.zoomLevel++;
        }
        objH.zoomLevel = Math.max(objH.zoomLevel, 1);
    }

    public void handleMouse(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            objH.offsetX += (event.getX() - lastMousePos.x)* objH.zoomLevel;
            objH.offsetY += (event.getY() - lastMousePos.y) * objH.zoomLevel;
        }
        lastMousePos.x = (int) event.getX();
        lastMousePos.y = (int) event.getY();
    }
}
