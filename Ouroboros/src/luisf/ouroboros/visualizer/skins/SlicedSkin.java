package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class SlicedSkin extends SkinBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final float sliceMargin = 20;
    private final float sliceThickness = 20;
    private final float lineThickness = 4;
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
    public void draw(PGraphics g) {
        graphics.lights();

        graphics.beginDraw();

        graphics.clear();

        int i = 0;
        float zOffset = sliceThickness + sliceMargin;

        for (ClassSlice slice : slices) {
            graphics.pushMatrix();

            graphics.translate(0, -slice.sliceHeight/2, i * zOffset);

            slice.draw(graphics);

            graphics.popMatrix();
            i++;

            // TODO remove later
            break;
        }

        graphics.endDraw();
    }

    // ================================================================

    // Helpers

}
