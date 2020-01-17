package luisf.ouroboros.visualizer.suits;

import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.GraphicsResource;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.logging.Logger;


/**
 * Rotating lamp
 */
public class Illuminati extends GraphicsResource {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
    private final PropertyManager props = PropertyManager.getInstance();

    private PVector center = new PVector();
    private float angleStep = PConstants.PI * 0.003f;
    private float linearStep = 0.03f;
    private float currentAngle = 0;
    private float rotationRadius = 800;
    private float indicatorRadius = 15;
    private float height = 200;

    private int pointLight01Color = 0xFFFFFFFF;
    // ================================================================

    public Illuminati(PApplet parent) {
        super(parent, null);
    }

    // ================================================================

    // Public

    public void addLight(Boolean state, boolean showIndicator, PGraphics g, int travelDistance) {
        if (state == true) {
//            g.ambientLight(128, 128, 128);
//            g.directionalLight(128, 128, 128, 0, 0, -1);
//            g.lightFalloff(1, 0, 0);
//            g.lightSpecular(102, 102, 102);

            g.lights();
            g.ambientLight(60,60,60);

            // circular motion
//            float xCoord = center.x + rotationRadius * parent.cos(currentAngle);
//            float yCoord = center.y + rotationRadius * parent.sin(currentAngle);
//            currentAngle += angleStep;

            // straight motion
            float xCoord = center.x;
            float yCoord = center.y + travelDistance * parent.cos(currentAngle);
//            currentAngle += angleStep;
            currentAngle += linearStep;

            g.pointLight(parent.red(pointLight01Color), parent.green(pointLight01Color), parent.blue(pointLight01Color),
                    xCoord, -height, yCoord);

            if (showIndicator) { drawFixture(g, xCoord, yCoord); }
        } else {
            g.noLights();
        }
    }

    public void setPointLight01Color(int color){
        pointLight01Color = color;
    };


    // ================================================================


    // Helpers

    private void drawFixture(PGraphics g, float xCoord, float yCoord) {
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
}
