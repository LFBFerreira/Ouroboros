package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.MethodModel;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.logging.Logger;

public class MethodLine implements DrawableInterface{

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    PApplet parent;

    private float thickness = 0;
    private float incrementSize = 0;
    private MethodModel method;

    private PShape methodBox;

    // ================================================================

    public MethodLine(MethodModel method, float incrementSize, float thickness, PApplet parent) {
        this.parent = parent;
        this.thickness = thickness;
        this.incrementSize = incrementSize;
        this.method = method;


        methodBox = initializeMethodShape(thickness, getHeight(), thickness);
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
        g.shape(methodBox);
//        g.box(thickness, getHeight(), thickness);
    }

    // ================================================================

    // Helpers

    private PShape initializeMethodShape(float sliceWidth, float sliceHeight, float sliceThickness) {
        PShape shape = parent.createShape(parent.BOX, sliceWidth, sliceHeight, sliceThickness);
        shape.setFill(0xFFFFFFFF);
        shape.setStroke(0xFF000000);
        shape.setStrokeWeight(0.5f);
        return shape;
    }
}
