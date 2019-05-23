package luisf.ouroboros.visualizer.scene;

import processing.core.PApplet;
import processing.core.PGraphics;
import remixlab.proscene.Scene;

import java.util.logging.Logger;

public class CameraMan extends Scene {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    // ================================================================

    public CameraMan(PApplet p, PGraphics canvas) {
        super(p, canvas);
    }

    public CameraMan(PApplet p) {
        this(p, p.g);
    }

    private int platformSize = 600;
    // ================================================================

    // Public

    public void init()
    {
        setRadius(700);

        setGridVisualHint(true);
        setAxesVisualHint(true);

        //eyeFrame().setDamping(0);

        camera().setUpVector(0, 1, 0);
        camera().setPosition(0, -10, 100);
        camera().lookAt(0, 0, 0);

        showAll();
    }

    /**
     * Draw on the scene's pgraphics
     * Called after Scene.endDraw()
     */
    @Override
    public void proscenium() {
        pg().lights();

        pg().ambientLight(150, 150, 150);

        // floor
//        pg().pushMatrix();
//
//        translate(0, -4, 0);
//
//        pg().pushStyle();
//        pg().fill(150);
//        pg().noStroke();
//        pg().box(platformSize, 3, platformSize);
//        pg().popStyle();
//
//        pg().popMatrix();

//        beginScreenDrawing();
//        pg().stroke(255);
//        pg().line(60, 10, 60, 110);
//        pg().line(10, 60, 110, 60);
//        endScreenDrawing();
    }


    // ================================================================

    // Helpers

}
