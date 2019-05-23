package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.MethodModel;
import processing.core.PGraphics;

import java.util.logging.Logger;

public class MethodLine implements DrawableInterface{

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private float size = 0;
    private MethodModel method;

    // ================================================================

    public MethodLine(MethodModel method, float size) {
        this.size = size;
        this.method = method;
    }


    // ================================================================

    // DrawableInterface

    @Override
    public void draw(PGraphics g) {
        g.pushMatrix();

        float height = method.metrics.size() * size;

        g.translate(size/2, height/2, 0 );

        g.box(size, height, size);

        g.popMatrix();
    }

    // ================================================================

    // Helpers
}
