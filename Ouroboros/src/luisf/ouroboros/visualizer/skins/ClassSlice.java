package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.analyzer.models.MethodModel;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClassSlice implements DrawableInterface {
    List<MethodLine> methodLines = new ArrayList<>();

    private float lineThickness;
    private float lineMargin;
    private float sliceThickness;

    public float sliceWidth = 0;
    public float sliceHeight = 0;

    private ClassModel classModel;

    // ================================================================

    public ClassSlice(ClassModel classModel, float sliceThickness, float lineThickness, float lineMargin) {
        this.classModel = classModel;
        this.sliceThickness = sliceThickness;
        this.lineThickness = lineThickness;
        this.lineMargin = lineMargin;

        int highest = 0;
        for (MethodModel model : classModel.getMethods()) {
            methodLines.add(new MethodLine(model, lineThickness));

            if (model.metrics.size() > highest) {
                highest = model.metrics.size();
                sliceHeight = highest;
            }
        }

        // find longest method
//        Optional<MethodModel> longestMethod = classModel.getMethods().stream().max((method1, method2) ->
//                Float.compare(method1.metrics.size(), method2.metrics.size()));

        sliceWidth = classModel.getMethods().size() * (lineThickness + lineMargin * 2) + lineThickness;
    }

    // ================================================================

    // DrawableInterface

    @Override
    public void draw(PGraphics g) {
        loadClassSliceStyle(g);
        g.box(sliceWidth, sliceHeight, sliceThickness);
        g.popStyle();

        g.translate(-lineMargin, 0 , 0);

        int i = 0;
        float xOffset = (lineThickness + lineMargin);

        for (MethodLine line : methodLines) {
            g.pushMatrix();

            g.translate((i * xOffset), 0, 0);
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
