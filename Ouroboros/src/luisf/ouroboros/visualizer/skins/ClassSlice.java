package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.analyzer.models.MethodModel;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.ArrayList;
import java.util.List;

public class ClassSlice implements DrawableInterface {
    List<MethodLine> methodLines = new ArrayList<>();

    public float lineThickness = 0;
    public float lineMargin = 0;
    public float lineIncrement = 0;

    public float sliceThickness = 0;
    public float sliceWidth = 0;
    public float sliceHeight = 0;

    public float floorOffset = 3;

    private ClassModel classModel;

    private PShape classSlice;
    private PApplet parent;

    // ================================================================

    public ClassSlice(ClassModel classModel, float sliceThickness, float lineThickness, float lineMargin, float lineIncrement, PApplet parent) {
        this.parent = parent;

        this.classModel = classModel;

        this.sliceThickness = sliceThickness;

        this.lineThickness = lineThickness;
        this.lineMargin = lineMargin;
        this.lineIncrement = lineIncrement;

        int highest = 0;
        for (MethodModel model : classModel.getMethods()) {
            methodLines.add(new MethodLine(model, lineIncrement, lineThickness, parent));

            if (model.metrics.size() > highest) {
                highest = model.metrics.size();
                sliceHeight = highest * lineIncrement;
            }
        }

        // find longest method
//        Optional<MethodModel> longestMethod = classModel.getMethods().stream().max((method1, method2) ->
//                Float.compare(method1.metrics.size(), method2.metrics.size()));

        sliceWidth = classModel.getMethods().size() * (lineThickness + lineMargin * 2);

        classSlice = initializeClassShape(sliceWidth, sliceHeight, sliceThickness);
    }

    // ================================================================

    // DrawableInterface

    @Override
    public void draw(PGraphics g) {
        // draw class slice
//        g.shape(classSlice);

        // draw class slice
        g.pushStyle();
        loadClassSliceStyle(g);
        g.box(sliceWidth, sliceHeight + floorOffset * 2, sliceThickness);
        g.popStyle();

        // one time offset, so the "methods" are drawn from the corner and not the center of the box
        g.translate(-sliceWidth / 2 + lineMargin + lineThickness / 2, 0, 0);

        int i = 0;
        float xOffset = (lineThickness + lineMargin * 2);

        for (MethodLine line : methodLines) {
            g.pushMatrix();

            g.translate((i * xOffset), -line.getHeight() / 2 + sliceHeight / 2, 0);
            //Handy.drawAxes(g, false, 20);

            line.draw(g);

            g.popMatrix();

            i++;
        }
    }

    // ================================================================

    // Helpers

    private PShape initializeClassShape(float sliceWidth, float sliceHeight, float sliceThickness) {
        PShape shape = parent.createShape(parent.BOX, sliceWidth, sliceHeight + floorOffset * 2, sliceThickness);
        shape.setFill(0x00FFFFFF);
        shape.setStroke(0xFF000000);
        shape.setStrokeWeight(0.5f);
        return shape;
    }

    private void loadClassSliceStyle(PGraphics g) {
        g.stroke(0);
        g.strokeWeight(0.5f);
        g.fill(200, 100);
        g.noFill();
    }
}
