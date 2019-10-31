package luisf.ouroboros.visualizer.suits.suit01;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class LandscapeSuit extends SuitBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private List<CityscapeSuit> cities = new LinkedList<>();
    private List<ProjectData> projects = new LinkedList<>();

    private int horizontalRes = 0;
    private int verticalRes = 0;


    // ================================================================

    /**
     * Constructor
     *
     * @param projects
     * @param parent
     */
    public LandscapeSuit(List<ProjectData> projects, PApplet parent) {
        super(null, parent);
        this.projects = projects;
    }

    // ================================================================

    // Public

    public void initialize() {
        loadProperties();

        for (ProjectData project : projects) {
            CityscapeSuit city = new CityscapeSuit(project.classModels, parent);
            city.initialize();
            cities.add(city);
        }
    }

    private void loadProperties() {
        this.horizontalRes = props.getInt("visualizer.suit02.horizontalResolution");
        this.verticalRes = props.getInt("visualizer.suit02.verticalResolution");
    }

    // ================================================================

    // DrawableInterface Interface

    private final int samplesPerRow = 3;

    @Override
    public void draw(PGraphics g) {

        g.beginDraw();
        g.pushMatrix();

        Handy.drawAxes(g, false, 200);

        int horizontalOffset = 800;
        int verticallOffset = 800;

        //g.scale(0.2f);

        int i = 0;
        for (CityscapeSuit city : cities) {
            g.translate(horizontalOffset, 0);

            if (i % samplesPerRow == 0) {
                g.translate(-horizontalOffset * samplesPerRow, verticallOffset);
            }

            city.draw(g);

            i++;
        }

        g.popMatrix();
        g.endDraw();
    }

    @Override
    public void keyPressed(int key, int keyCode) {
        switch (key) {
            case 'l':
                for (CityscapeSuit city : cities) {
                    city.switchLights();
                }
                break;

        }
    }


    // ================================================================

    // InputListennerInterface

    @Override
    public void reactToInput(InputEvent input) {

    }
}

// ================================================================
