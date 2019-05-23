package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.List;
import java.util.logging.Logger;

public class SlicedSkin extends SkinBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private int rotationX = 0;
    private float rotationY = 0;

    // ================================================================

    /**
     *
     * @param models
     * @param graphics
     * @param parent
     */
    public SlicedSkin(List<ClassModel> models, PGraphics graphics, PApplet parent) {
        super(models, graphics, parent);
    }

    // ================================================================

    // DrawableInterface Interface

    @Override
    public PGraphics draw() {
        graphics.lights();

        graphics.beginDraw();

        graphics.clear();
        graphics.pushMatrix();

//        graphics.noFill();
        graphics.fill(60);
        graphics.stroke(200);

//        graphics.translate(graphics.width/2, graphics.height/2, 0);
        rotationY +=0.01;
        graphics.rotateY(rotationY);

        graphics.box(60, 60, 20);

        graphics.popMatrix();

        graphics.endDraw();
        return graphics;
    }

    // ================================================================

    // Helpers

}
