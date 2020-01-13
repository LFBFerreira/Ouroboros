package luisf.ouroboros.visualizer.suits.suit01;

import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.models.MethodModel;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.DrawableInterface;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.ArrayList;
import java.util.List;

public class ClassSlice implements DrawableInterface {
    private List<MethodLine> methodLines = new ArrayList<>();

    private final PropertyManager props = PropertyManager.getInstance();

    private float methodDepth = 0;
    private float methodMargin = 0;
    private float methodHeightIncrement = 0;

    private float classDepth = 0;
    private float sliceWidth = 0;
    private float sliceHeight = 0;

    private int classLineColor;


    private final float floorOffset = 3;
    private final float zeroWidth = 10;


    private ClassModel classModel;

    private PShape classSlice;
    private PApplet parent;

    // ================================================================

    public ClassSlice(ClassModel classModel, PApplet parent) {
        this.parent = parent;
        this.classModel = classModel;
    }

    public void initialize() {
        loadProperties();

        // create and initialize MethodLine for each method
        classModel.getMethods().forEach(m -> {
            MethodLine line = new MethodLine(m, parent);
            line.initialize();
            methodLines.add(line);
        });

        // find highest method
        int highest = 0;
        for (MethodModel model : classModel.getMethods()) {
            if (model.metrics.size() > highest) {
                highest = model.metrics.size();
                sliceHeight = highest * methodHeightIncrement;
            }
        }

        sliceWidth = classModel.getMethods().size() * (methodDepth + methodMargin * 2);

        classSlice = Shaper.createBox(sliceWidth > 0 ? sliceWidth : zeroWidth, sliceHeight + floorOffset * 2, classDepth, parent);
        //classSlice.setFill(classFillColor);
        //classSlice.setStroke(classLineColor);
    }


    public float getHeight() {
        return sliceHeight;
    }

    public float getWidth() {
        return sliceWidth;
    }


    // ================================================================

    // DrawableInterface

    @Override
    public void draw(PGraphics graphics) {

        // draw class slice
        //graphics.shape(classSlice);

        // one time offset, so the "methods" are drawn from the corner and not the center of the box
        graphics.translate(-sliceWidth / 2 + methodMargin + methodDepth / 2, 0, 0);

        int i = 0;
        float xOffset = (methodDepth + methodMargin * 2);

        for (MethodLine line : methodLines) {
            graphics.pushMatrix();

            graphics.translate((i * xOffset), -line.getHeight() / 2 + sliceHeight / 2, 0);
            //Handy.drawAxes(g, false, 20);

            line.draw(graphics);

            graphics.popMatrix();

            i++;
        }
    }

    // ================================================================

    // Helpers

    private void loadProperties() {
        classLineColor = props.getInt("visualizer.suit01.classLineColor");
        methodDepth = props.getInt("visualizer.suit01.methodThickness");
        methodMargin = props.getInt("visualizer.suit01.methodMargin");
        classDepth = props.getInt("visualizer.suit01.classThickness");
        methodHeightIncrement = props.getInt("visualizer.suit01.methodLineIncrement");
    }
}
