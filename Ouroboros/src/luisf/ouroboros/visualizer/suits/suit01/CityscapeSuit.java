package luisf.ouroboros.visualizer.suits.suit01;


import luisf.ouroboros.common.Handy;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class CityscapeSuit extends SuitBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private float classMargin = 20;
    private float classThickness = 20;
    private float methodThickness = 8;
    private float methodMargin = 4;
    private float methodLineIncrement = 2;
    private int classFillColor;
    private int classLineColor;

    private List<ClassSlice> slices = new LinkedList<>();

    private int numberSlicesToDraw = 1;


    // ================================================================

    /**
     * @param models
     * @param graphics
     * @param parent
     */
    public CityscapeSuit(List<ClassModel> models, PGraphics graphics, PApplet parent) {
        super(models, graphics, parent);
    }

    // ================================================================

    // Public

    public void initialize() {
        loadProperties();

        models.forEach(m -> slices.add(new ClassSlice(
                m,
                classThickness,
                methodThickness,
                methodMargin,
                methodLineIncrement,
                classFillColor,
                classLineColor,
                parent)));

        numberSlicesToDraw = slices.size();
    }

    private void loadProperties() {
        classMargin = props.getInt("visualizer.suit01.classMargin");
        classThickness = props.getInt("visualizer.suit01.classThickness");
        methodThickness = props.getInt("visualizer.suit01.methodThickness");
        methodMargin = props.getInt("visualizer.suit01.methodMargin");
        methodLineIncrement = props.getInt("visualizer.suit01.methodLineIncrement");
        classFillColor = props.getInt("visualizer.suit01.classFillColor");
        classLineColor = props.getInt("visualizer.suit01.classLineColor");
    }

    // ================================================================

    // DrawableInterface Interface

    @Override
    public void draw(PGraphics g) {
        graphics.beginDraw();

        graphics.pushMatrix();

        // pushes everything back, so the model is centered in the world
        graphics.translate(0, 0, numberSlicesToDraw / -2f * classMargin * 2);

        drawSlices(slices, numberSlicesToDraw);

        //Handy.drawAxes(graphics, false, 60);

        graphics.popMatrix();

        graphics.endDraw();
    }

    @Override
    public void keyPressed(int key, int keyCode) {
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

    // InputListennerInterface

    @Override
    public void reactToInput(InputEvent input) {

    }

    // ================================================================

    // Helpers

    private void drawSlices(List<ClassSlice> slicesList, int slicesToDraw) {
        if (slicesToDraw > slicesList.size() || slicesToDraw < 1) {
            slicesToDraw = slicesList.size();
            log.info("The amount of slices to be drawn is incorrect");
        }

        int i = 0;
        float zOffset = classThickness + classMargin;

        for (ClassSlice slice : slicesList) {
            graphics.pushMatrix();

            graphics.translate(0, -slice.getSliceHeight() / 2, i * zOffset);

            //Handy.drawAxes(graphics, false, 20);
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
