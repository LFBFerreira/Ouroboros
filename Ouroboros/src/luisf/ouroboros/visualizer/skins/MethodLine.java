package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.MethodModel;
import processing.core.PGraphics;

import java.util.logging.Logger;

public class MethodLine implements DrawableInterface{

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private float thickness = 0;
    private float incrementSize = 0;
    private MethodModel method;

    // ================================================================

    public MethodLine(MethodModel method, float incrementSize, float thickness) {
        this.thickness = thickness;
        this.incrementSize = incrementSize;
        this.method = method;
    }

    // ================================================================

    // Public

    public float getHeight()
    {
        return method.metrics.size() * incrementSize;
    }

    // ================================================================

    // DrawableInterface

    @Override
    public void draw(PGraphics g) {
        //g.pushMatrix();

        g.box(thickness, getHeight(), thickness);

        //g.popMatrix();
    }

    // ================================================================

    // Helpers
}
