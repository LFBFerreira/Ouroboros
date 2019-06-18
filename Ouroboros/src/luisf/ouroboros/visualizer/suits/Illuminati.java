package luisf.ouroboros.visualizer.suits;

import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.GraphicsResource;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.logging.Logger;

public class Illuminati extends GraphicsResource {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
    private final PropertyManager props = PropertyManager.getInstance();

    private PVector center = new PVector();
    private float angleStep = PConstants.PI * 0.005f;
    private float currentAngle = 0;
    private float radius = 400;
    private float height = 300;

    // ================================================================

    public Illuminati(PGraphics graphics, PApplet parent) {
        super(parent, graphics);
    }

    // ================================================================

    // Public

    public void turnLights(Boolean state, boolean showIndicator) {
        if (state == true) {

            graphics.lightFalloff(1, 0, 0);
            graphics.lightSpecular(20, 20, 20);
            graphics.ambientLight(150, 150, 150);
            graphics.directionalLight(128, 128, 128, 0, 0, -1);

            float xCoord = center.x + radius * parent.cos(currentAngle);
            float yCoord = center.y + radius * parent.sin(currentAngle);
            currentAngle += angleStep;

//            graphics.spotLight(200, 50, 50,
//                    xCoord, -height, yCoord,
//                    0, 0, 0,
//                    PConstants.PI, 32);

            graphics.pointLight(200, 50, 50,
                    xCoord, -height, yCoord);

            if (showIndicator) {
                // draw direction line
                graphics.pushStyle();
                graphics.strokeWeight(2);
                graphics.stroke(0, 0, 255);
                graphics.line(xCoord, -height, yCoord, 0, 0, 0);
                graphics.popStyle();

                // draw sphere
                graphics.pushMatrix();
                graphics.rotateX(PConstants.HALF_PI);
                graphics.translate(xCoord, yCoord, height);
                graphics.pushStyle();
                graphics.fill(255);
                graphics.noStroke();
                graphics.sphere(30);
                graphics.popStyle();
                graphics.popMatrix();
            }
        } else {
            graphics.noLights();
        }
    }
}
