package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.common.Handy;
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

    private int numberSlicesToDraw = 1;


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

        numberSlicesToDraw = slices.size();
    }

    // ================================================================

    // DrawableInterface Interface

    @Override
    public void draw(PGraphics g) {
        graphics.lights();

        graphics.beginDraw();

        graphics.clear();

//        drawSlices(slices);
        drawSlices(slices, numberSlicesToDraw);

        graphics.endDraw();
    }

    @Override
    public void keyPressed(int key) {
        switch (key) {
            case '+':
                if (numberSlicesToDraw < slices.size()) {
                    numberSlicesToDraw++;
                }
                log.info(Handy.f("Slices to draw: %d / %d", numberSlicesToDraw, slices.size()));
                break;
            case '-':
                if (numberSlicesToDraw > 1) {
                    numberSlicesToDraw--;
                }
                log.info(Handy.f("Slices to draw: %d / %d", numberSlicesToDraw, slices.size()));
                break;
        }
    }


    // ================================================================

    // Helpers

    private void drawSlices(List<ClassSlice> slicesList) {
        drawSlices(slicesList, slicesList.size());
    }

    private void drawSlices(List<ClassSlice> slicesList, int slicesToDraw) {
        if (slicesToDraw > slicesList.size() || slicesToDraw < 1) {
            slicesToDraw = slicesList.size();
            log.info("The amount of slices to be drawn is incorrect");
        }

        int i = 0;
        float zOffset = sliceThickness + sliceMargin;

        for (ClassSlice slice : slicesList) {
            graphics.pushMatrix();

            graphics.translate(0, -slice.sliceHeight / 2, i * zOffset);

            slice.draw(graphics);

            graphics.popMatrix();
            i++;

            // stop the cycle if the right amount of slices has been drawn
            if (i == slicesToDraw) {
                break;
            }
        }
    }
}
