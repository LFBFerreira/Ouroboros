package luisf.ouroboros.visualizer.suits.suit01;


import luisf.ouroboros.common.Handy;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.hmi.OscStringBuilder;
import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.Illuminati;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class CityscapeSuit extends SuitBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private float classMargin = 20;

    private List<ClassSlice> slices = new LinkedList<>();

    private int numberSlicesToDraw = 1;
    private int floorColor = 0xFFFFFFFF;

    private Boolean lightsOn = true;

    private OscStringBuilder oscBuilder = new OscStringBuilder();

    private Illuminati illuminati;

    private PShape floor;
    private final int floorWidth = 500;
    private final int floorDepth = 1100;

    // ================================================================

    /**
     * @param models
     * @param graphics
     * @param parent
     */
    public CityscapeSuit(List<ClassModel> models, PGraphics graphics, PApplet parent) {
        super(models, graphics, parent);

        illuminati = new Illuminati(graphics, parent);
    }

    // ================================================================

    // Public

    public void initialize() {
        loadProperties();

        // create and initialize a ClassSlice for each class model
        models.forEach(m -> {
            ClassSlice slice = new ClassSlice(m, parent);
            slice.initialize();
            slices.add(slice);
        });

        numberSlicesToDraw = slices.size();

        floor = createFloor();
    }

    private void loadProperties() {
        classMargin = props.getInt("visualizer.suit01.classMargin");
        floorColor = props.getInt("visualizer.floorColor");
    }

    // ================================================================

    // DrawableInterface Interface

    @Override
    public void draw(PGraphics g) {
        graphics.beginDraw();

        graphics.pushMatrix();

        illuminati.turnLights(lightsOn, true);

        drawFloor();

        // debug sphere
//        graphics.pushStyle();
//        graphics.noStroke();
//        graphics.sphere(100);
//        graphics.popStyle();

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

            case 'l':
                lightsOn = !lightsOn;
                log.info(Handy.f("Lights are %s", lightsOn ? "On" : "Off"));
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

    private void drawFloor() {
        graphics.pushMatrix();

        // lower the floor so that the surface matches y = 0
        graphics.translate(0, 5, 0);

        graphics.shape(floor);

        graphics.popMatrix();
    }

    private PShape createFloor() {
        PShape floor = Shaper.createBox(floorWidth, 4, floorDepth, parent);
        floor.setFill(floorColor);
        floor.setStroke(200);
        return floor;
    }

    private void drawSlices(List<ClassSlice> slicesList, int slicesToDraw) {
        if (slicesToDraw > slicesList.size() || slicesToDraw < 1) {
            slicesToDraw = slicesList.size();
            log.info("The amount of slices to be drawn is incorrect");
        }

        int i = 0;
        // class depth is the same for all instances
        float zOffset = slices.get(0).getClassDepth() + classMargin;

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
