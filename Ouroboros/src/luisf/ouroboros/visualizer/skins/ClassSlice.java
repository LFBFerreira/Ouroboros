package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.analyzer.models.MethodModel;
import luisf.ouroboros.common.Handy;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    // ================================================================

    public ClassSlice(ClassModel classModel, float sliceThickness, float lineThickness, float lineMargin, float lineIncrement) {
        this.classModel = classModel;

        this.sliceThickness = sliceThickness;

        this.lineThickness = lineThickness;
        this.lineMargin = lineMargin;
        this.lineIncrement = lineIncrement;

        int highest = 0;
        for (MethodModel model : classModel.getMethods()) {
            methodLines.add(new MethodLine(model, lineIncrement, lineThickness));

            if (model.metrics.size() > highest) {
                highest = model.metrics.size();
                sliceHeight = highest * lineIncrement;
            }
        }

        // find longest method
//        Optional<MethodModel> longestMethod = classModel.getMethods().stream().max((method1, method2) ->
//                Float.compare(method1.metrics.size(), method2.metrics.size()));

        sliceWidth = classModel.getMethods().size() * (lineThickness + lineMargin * 2);
    }

    // ================================================================

    // DrawableInterface

    @Override
    public void draw(PGraphics g) {
        loadClassSliceStyle(g);

        // class box
        g.box(sliceWidth, sliceHeight + floorOffset * 2, sliceThickness);
        g.popStyle();

        // one time offset, so the lines are drawn from the corner and not the center of the box
        g.translate(-sliceWidth / 2 + lineMargin + lineThickness / 2, 0, 0);

        int i = 0;
        float xOffset = (lineThickness + lineMargin * 2);

        for (MethodLine line : methodLines) {
            g.pushMatrix();

            g.translate((i * xOffset), - line.getHeight() / 2 + sliceHeight / 2, 0);
            //Handy.drawAxes(g, false, 20);
            line.draw(g);

            g.popMatrix();

            i++;
        }
    }

    // ================================================================

    // Helpers

    private void loadClassSliceStyle(PGraphics g) {
        g.pushStyle();
        g.stroke(0);
        g.strokeWeight(0.5f);
        g.fill(200, 100);
        g.noFill();
    }
}
