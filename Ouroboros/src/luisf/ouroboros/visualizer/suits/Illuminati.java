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
    private float angleStep = PConstants.PI * 0.003f;
    private float currentAngle = 0;
    private float rotationRadius = 800;
    private float indicatorRadius = 15;
    private float height = 200;

    // ================================================================

    public Illuminati(PApplet parent) {
        super(parent, null);
    }

    // ================================================================

    // Public

    public void turnLights(Boolean state, boolean showIndicator, PGraphics g) {
        if (state == true) {

//            g.lightFalloff(1, 0, 0);
//            g.lightSpecular(20, 20, 20);
//            g.ambientLight(200, 200, 200);
            //g.directionalLight(128, 128, 128, 0, 0, -1);
            g.lights();

            float xCoord = center.x + rotationRadius * parent.cos(currentAngle);
            float yCoord = center.y + rotationRadius * parent.sin(currentAngle);
            currentAngle += angleStep;

//            graphics.spotLight(200, 50, 50,
//                    xCoord, -height, yCoord,
//                    0, 0, 0,
//                    PConstants.PI, 32);

            g.pointLight(200, 50, 50,
                    xCoord, -height, yCoord);

            if (showIndicator) {
                // draw direction line
//                graphics.pushStyle();
//                graphics.strokeWeight(0.5f);
//                graphics.stroke(50, 50, 255);
//                graphics.line(xCoord, -height, yCoord, 0, 0, 0);
//                graphics.popStyle();

                // draw sphere
                g.pushMatrix();
                g.rotateX(PConstants.HALF_PI);
                g.translate(xCoord, yCoord, height);
                g.pushStyle();
                g.fill(200, 50, 50);
                g.noStroke();
                g.sphere(indicatorRadius);
                g.popStyle();
                g.popMatrix();
            }
        } else {
            g.noLights();
        }
    }
}
