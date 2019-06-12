package luisf.ouroboros.visualizer.suits.suit01;

import luisf.ouroboros.analyzer.models.MethodModel;
import luisf.ouroboros.visualizer.DrawableInterface;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.logging.Logger;

public class MethodLine implements DrawableInterface {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private PApplet parent;

    private float methodWidth = 0;
    private float methodHeightIncrement = 0;
    private MethodModel method;

    private PShape methodBox;

    // ================================================================

    public MethodLine(MethodModel method, float methodHeightIncrement, float methodWidth, PApplet parent) {
        this.parent = parent;
        this.methodWidth = methodWidth;
        this.methodHeightIncrement = methodHeightIncrement;
        this.method = method;

        methodBox = initializeMethodShape(methodWidth, getHeight(), methodWidth);
    }

    // ================================================================

    // Public

    public float getHeight() {
        return method.metrics.size() * methodHeightIncrement;
    }

    // ================================================================

    // DrawableInterface

    @Override
    public void draw(PGraphics g) {
        g.shape(methodBox);
//        g.box(methodWidth, getHeight(), methodWidth);
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
