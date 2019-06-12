package luisf.ouroboros.visualizer.suits.suit01;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.analyzer.models.MethodModel;
import luisf.ouroboros.visualizer.DrawableInterface;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.ArrayList;
import java.util.List;

public class ClassSlice implements DrawableInterface {
    private List<MethodLine> methodLines = new ArrayList<>();

    private float methodDepth = 0;
    private float methodMargin = 0;
    private float methodHeightIncrement = 0;

    private float classDepth = 0;
    private float sliceWidth = 0;
    private float sliceHeight = 0;

    private int classFillColor;
    private int classLineColor;

    private float floorOffset = 3;


    private ClassModel classModel;

    private PShape classSlice;
    private PApplet parent;

    // ================================================================

    public ClassSlice(ClassModel classModel,
                      float classDepth,
                      float methodWidth,
                      float methodMargin,
                      float methodHeightIncrement,
                      int classFillColor,
                      int classLineColor,
                      PApplet parent) {
        this.parent = parent;

        this.classModel = classModel;

        this.classDepth = classDepth;
        this.classFillColor = classFillColor;
        this.classLineColor = classLineColor;

        this.methodDepth = methodWidth;
        this.methodMargin = methodMargin;
        this.methodHeightIncrement = methodHeightIncrement;

        int highest = 0;
        for (MethodModel model : classModel.getMethods()) {
            methodLines.add(new MethodLine(model, methodHeightIncrement, methodWidth, parent));

            if (model.metrics.size() > highest) {
                highest = model.metrics.size();
                sliceHeight = highest * methodHeightIncrement;
            }
        }

        // find longest method
//        Optional<MethodModel> longestMethod = classModel.getMethods().stream().max((method1, method2) ->
//                Float.compare(method1.metrics.size(), method2.metrics.size()));

        sliceWidth = classModel.getMethods().size() * (methodWidth + methodMargin * 2);

        classSlice = createClassShape(sliceWidth, sliceHeight, classDepth);
    }

    // ================================================================

    // DrawableInterface

    @Override
    public void draw(PGraphics graphics) {

        // draw class slice
        graphics.pushStyle();
        loadClassSliceStyle(graphics);
        graphics.box(sliceWidth, sliceHeight + floorOffset * 2, classDepth);
        graphics.popStyle();

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


    // Public

    public float getSliceHeight() {
        return sliceHeight;
    }

    // ================================================================

    // Helpers

    private PShape createClassShape(float sliceWidth, float sliceHeight, float sliceThickness) {
        PShape shape = parent.createShape(parent.BOX, sliceWidth, sliceHeight + floorOffset * 2, sliceThickness);
        shape.setFill(classFillColor);
        shape.setStroke(classLineColor);
        shape.setStrokeWeight(0.5f);
        return shape;
    }

    private void loadClassSliceStyle(PGraphics g) {
        g.stroke(classLineColor);
        g.strokeWeight(0.5f);
        g.fill(classFillColor);
        g.noFill();
    }
}
