package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.analyzer.models.MethodModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClassSlice {
    List<MethodLine> methodLines = new ArrayList<>();

    private float lineThickness;
    private float sliceThickness;

    public float sliceWidth = 0;
    public float sliceHeight = 0;

    private ClassModel classModel;

    public ClassSlice(ClassModel classModel, float sliceThickness, float lineThickness, float lineMargin) {
        this.sliceThickness = sliceThickness;
        this.lineThickness = lineThickness;
        this.classModel = classModel;

        int highest = 0;
        for (MethodModel model: classModel.getMethods()) {
            methodLines.add(new MethodLine(model, lineThickness));

            if (model.metrics.size() > highest)
            {
                highest =model.metrics.size();
                sliceHeight = highest;
            }
        }

        // find longest method
//        Optional<MethodModel> longestMethod = classModel.getMethods().stream().max((method1, method2) ->
//                Float.compare(method1.metrics.size(), method2.metrics.size()));

        sliceWidth = classModel.getMethods().size() * (lineThickness + lineMargin * 2) + lineThickness;
    }
}
