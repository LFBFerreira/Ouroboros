package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class SlicedSkin extends SkinBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final float sliceMargin = 20;
    private final float sliceThickness = 20;
    private final float lineThickness = 10;
    private final float lineMargin = 5;

    private List<ClassSlice> slices = new LinkedList<>();

    private int rotationX = 0;
    private float rotationY = 0;

    // ================================================================

    /**
     * @param models
     * @param graphics
     * @param parent
     */
    public SlicedSkin(List<ClassModel> models, PGraphics graphics, PApplet parent) {
        super(models, graphics, parent);
    }

    // ================================================================

    // Public

    public void initialize() {
        models.forEach(m ->
        {
            slices.add(new ClassSlice(m, sliceThickness, lineThickness, lineMargin));
        });
    }

    // ================================================================

    // DrawableInterface Interface

    @Override
    public PGraphics draw() {
        graphics.lights();

        graphics.beginDraw();

        graphics.clear();

        int i = 0;
        int zOffset = 0;
        for (ClassSlice slice : slices) {
            graphics.pushMatrix();

            graphics.translate(0, -slice.sliceHeight/2, i * (sliceThickness + sliceMargin));

            loadClassSliceStyle();
            graphics.box(slice.sliceWidth, slice.sliceHeight, sliceThickness);
            graphics.popStyle();

            graphics.popMatrix();
            i++;
        }

        slices.forEach(s ->
        {

        });


        graphics.endDraw();
        return graphics;
    }

    // ================================================================

    // Helpers

    private void loadClassSliceStyle()
    {
        graphics.pushStyle();

        graphics.stroke(0);
        graphics.strokeWeight(0.5f);
        graphics.fill(200, 100);
    }
}
