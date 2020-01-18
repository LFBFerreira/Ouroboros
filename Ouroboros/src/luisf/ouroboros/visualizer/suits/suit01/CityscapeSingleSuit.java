package luisf.ouroboros.visualizer.suits.suit01;


import luisf.interfaces.InputEvent;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.Illuminati;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class CityscapeSingleSuit extends SuitBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private float classMargin = 20;
    private ProjectData project;
    private List<ClassSlice> slices = new LinkedList<>();
    private int numberSlicesToDraw = 1;
    private int floorColor = 0xFFFFFFFF;        // overriden by app.properties
    private int pointLightColor = 0xFFFFFFFF;   // overriden by app.properties
    private float classSliceDepth;

    private Boolean lightsOn = true;
    private Boolean hideLights = true;
    private Illuminati illuminati;
    private PShape floor;
    private int floorHeight = 4;

    PVector initialCameraPosition = new PVector(0, 0, 1500);

    // ================================================================

    /**
     * Constructor
     *
     * @param project
     * @param parent
     */
    public CityscapeSingleSuit(ProjectData project , PApplet parent) {
        super(null, parent);
        this.project = project;
        illuminati = new Illuminati(parent);
    }

    // ================================================================

    // Public

    public void initialize() {
        loadProperties();

        // create and initialize a ClassSlice for each class model
        project.classModels.forEach(m -> {
            ClassSlice slice = new ClassSlice(m, parent);
            slice.initialize();
            slices.add(slice);
        });

        numberSlicesToDraw = slices.size();

        float totalClassWidth = props.getInt("visualizer.suit01.methodThickness") +
                props.getInt("visualizer.suit01.methodMargin") +
                props.getInt("visualizer.suit01.classThickness");


        floor = createFloor();

        illuminati.setPointLight01Color(pointLightColor);
    }

    public void switchLights() {
        lightsOn = !lightsOn;
        log.info(Handy.f("Lights are %s", lightsOn ? "On" : "Off"));
    }

    public void hideLights() {
        hideLights = !hideLights;
        log.info(Handy.f("Lights are %s", hideLights ? "Hidden" : "Visible"));
    }

    public float getFloorHeight() {
        return floor.getHeight();
    }

    public float getCityWidth() {
        return floor.getWidth();
    }

    @Override
    public ProjectData getCurrentProject() {
        return project;
    }

    @Override
    public PVector getInitialCameraPosition() {
        return initialCameraPosition;
    }

    // ================================================================

    // DrawableInterface Interface

    @Override
    public void draw(PGraphics g) {
        //Handy.drawAxes(g, false, 100);

        g.pushMatrix();

        int travelDistance = Math.round(2.5f * slices.size() * classSliceDepth);
        illuminati.addLight(lightsOn, !hideLights, g, travelDistance);

        drawFloor(g);

        // pushes everything back, so the model is centered in the world
        g.translate(0, 0, ((numberSlicesToDraw / -2f)) * (classSliceDepth + classMargin));

        drawClassSlices(slices, numberSlicesToDraw, g);

        g.popMatrix();
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
                switchLights();
                break;

            case 'k':
                hideLights();
                break;

        }
    }

    // ================================================================

    // InputListennerInterface

    @Override
    public void newEvent(InputEvent input) {

    }

    // ================================================================

    // Helpers

    private void loadProperties() {
        pointLightColor= props.getInt("visualizer.suit01.pointLightColor");
        classMargin = props.getInt("visualizer.suit01.classMargin");
        floorColor = props.getInt("visualizer.suit01.floorColor");
        classSliceDepth = props.getInt("visualizer.suit01.classThickness");
    }

    private void drawFloor(PGraphics g) {
        g.pushMatrix();

        // lower the floor so that the surface matches y = 0
        g.translate(0, floorHeight - 2, -classMargin);
        g.shape(floor);

        g.popMatrix();
    }

    private PShape createFloor() {
        float maxWidth = 0;

        for (ClassSlice slice : slices) {
            if (maxWidth < slice.getWidth()) {
                maxWidth = slice.getWidth();
            }
        }

        PShape floor = Shaper.createBox(maxWidth, floorHeight,
                (1 + slices.size()) * (classSliceDepth + classMargin),
                parent);

        floor.setFill(floorColor);
        floor.setStroke(0x0);
        floor.setStrokeWeight(1);
        floor.setSpecular(0xFF787878);

        return floor;
    }

    private void drawClassSlices(List<ClassSlice> slicesList, int slicesToDraw, PGraphics g) {
        if (slicesToDraw > slicesList.size() || slicesToDraw < 1) {
            slicesToDraw = slicesList.size();
            log.info("The amount of slices to be drawn is incorrect");
        }

        int i = 0;
        // class depth is the same for all instances
        float zOffset = classSliceDepth + classMargin;

        for (ClassSlice slice : slicesList) {
            g.pushMatrix();

            g.translate(0, -slice.getHeight() / 2, i * zOffset);

            //Handy.drawAxes(g, false, 20);
            slice.draw(g);

            g.popMatrix();
            i++;

            // stop the cycle if the right amount of slices has been drawn
            if (i == slicesToDraw) {
                break;
            }
        }
    }
}
