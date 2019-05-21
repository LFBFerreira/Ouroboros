package luisf.ouroboros.visualizer;

import com.sun.prism.GraphicsResource;
import javafx.scene.Camera;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import remixlab.proscene.*;

import java.util.logging.Logger;

public class CameraMan extends GraphicsResources {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private PVector eyePosition = new PVector();
    private PVector sceneCenter = new PVector();
    private PVector orientation = new PVector();

    private Boolean trackingMouse = false;
    private PVector lastMousePosition = new PVector();

    private PVector defaultRotationSpeed = new PVector();

    // ================================================================

    public CameraMan(PGraphics graphics, PApplet parent) {
        super(parent, graphics);

        // default camera coordinates
        eyePosition = new PVector(0, graphics.height / 2, (graphics.height / 2) / parent.tan(parent.PI / 6));
        sceneCenter = new PVector(graphics.width / 2, graphics.height / 2, 0);
        orientation = new PVector(0, 1, 0);

        defaultRotationSpeed = new PVector(0.9f, 0, 0);
    }

    // ================================================================

    public void updateCamera(boolean mousePressed, int mouseX, int mouseY) {
        if (!mousePressed) {
            lastMousePosition = new PVector(mouseX, mouseY);
            eyePosition.x = eyePosition.x + defaultRotationSpeed.x;
        }
        else
        {
            float mouseOffsetX = mouseX > lastMousePosition.x ? mouseX - lastMousePosition.x : lastMousePosition.x - mouseX;
            eyePosition.x = lastMousePosition.x + mouseOffsetX;
            //log.info("Mouse " + mouseX);
        }

        graphics.beginDraw();

        graphics.camera(eyePosition.x, eyePosition.y, eyePosition.z,
                sceneCenter.x, sceneCenter.y, sceneCenter.z,
                orientation.x, orientation.y, orientation.z);

        graphics.endDraw();
    }
}
