package luisf.ouroboros.visualizer.suits.suit01;

import luisf.ouroboros.models.MethodModel;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.DrawableInterface;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.logging.Logger;

public class MethodLine implements DrawableInterface {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private PApplet parent;

    private final PropertyManager props = PropertyManager.getInstance();

    private float methodWidth = 0;
    private float methodHeightIncrement = 2;
    private MethodModel method;
    private int methodStrokeColor;
    private int methodFillColor;


    private PShape methodBox;

    // ================================================================

    public MethodLine(MethodModel method, PApplet parent) {
        this.parent = parent;
        this.methodWidth = methodWidth;
        this.methodHeightIncrement = methodHeightIncrement;
        this.method = method;
    }

    public void initialize() {
        loadProperties();

        methodBox = Shaper.createBox(methodWidth, getHeight(), methodWidth, parent);
        methodBox.setFill(methodFillColor);
        methodBox.setStroke(methodStrokeColor);
        methodBox.noStroke();
    }

    private void loadProperties() {
        methodHeightIncrement = props.getInt("visualizer.suit01.methodLineIncrement");
        methodFillColor = props.getInt("visualizer.suit01.classFillColor");
        methodStrokeColor = props.getInt("visualizer.suit01.classLineColor");
    }

    // ================================================================

    // Public

    public float getHeight() {
        return method.metrics.size() * methodHeightIncrement;
    }

    // ================================================================

    // DrawableInterface

    @Override
    public void draw(PGraphics graphics) {
        graphics.shape(methodBox);
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
