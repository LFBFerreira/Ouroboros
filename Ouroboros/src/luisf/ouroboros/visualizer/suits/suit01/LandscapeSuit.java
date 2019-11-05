package luisf.ouroboros.visualizer.suits.suit01;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class LandscapeSuit extends SuitBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private List<CityscapeSuit> cities = new LinkedList<>();
    private List<ProjectData> projects = new LinkedList<>();

    private int horizontalOffset = 0;
    private int verticalOffset = 0;


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
        this.horizontalOffset = props.getInt("visualizer.suit02.horizontalOffset");
        this.verticalOffset = props.getInt("visualizer.suit02.verticalOffset");
    }

    // ================================================================

    // DrawableInterface Interface

    private final int samplesPerRow = 3;

    @Override
    public void draw(PGraphics g) {

        g.beginDraw();
        g.pushMatrix();

        Handy.drawAxes(g, false, 200);

        //g.translate(horizontalRes, 0);
        //g.scale(0.2f);

        g.translate(-horizontalOffset * (samplesPerRow/2),
                -verticalOffset * (samplesPerRow/2));


        int cityIndex = 0;
        for (CityscapeSuit city : cities) {

            city.draw(g);

            cityIndex++;

            // move the anchor to the next position in Y
            g.translate(horizontalOffset, 0);

            // move the anchor to the next position in Y, every samplesPerRow
            if (cityIndex % samplesPerRow == 0) {
                g.translate(-horizontalOffset * samplesPerRow, verticalOffset);
            }
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
