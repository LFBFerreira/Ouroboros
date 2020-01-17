package luisf.ouroboros.visualizer.suits.suit01;

import luisf.interfaces.InputEvent;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class CityscapeMultiSuit extends SuitBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private List<CityscapeSingleSuit> cities = new LinkedList<>();
    private List<ProjectData> projects = new LinkedList<>();

    private int multiCityBorderSize = 0;        // overriden by app.properties
    private int maxCitiesWidth = 0;

    PVector initialCameraPosition = new PVector(0,0,0);

    // ================================================================

    /**
     * Constructor
     *
     * @param projects
     * @param parent
     */
    public CityscapeMultiSuit(List<ProjectData> projects, PApplet parent) {
        super(null, parent);
        this.projects = projects;
    }

    // ================================================================

    // Public
    @Override
    public void initialize() {
        loadProperties();

        // order projects by date
        projects.sort(new Comparator<ProjectData>() {
            @Override
            public int compare(ProjectData o1, ProjectData o2) {
                return o1.commitDate.compareTo(o2.commitDate);
            }
        });

        for (ProjectData project : projects) {
            CityscapeSingleSuit city = new CityscapeSingleSuit(project, parent);
            city.initialize();
            cities.add(city);

            maxCitiesWidth += city.getCityWidth() + multiCityBorderSize;
        }
    }

    @Override
    public ProjectData getCurrentProject() {
        return null; //projects.get(currentProjectIndex);
    }

    @Override
    public PVector getInitialCameraPosition() {
        return initialCameraPosition;
    }

    // ================================================================

    // DrawableInterface Interface

    private final int samplesPerRow = 9;

    @Override
    public void draw(PGraphics g) {
        g.beginDraw();
        g.pushMatrix();

        //Handy.drawAxes(g, false, 200);

        // initial offset to center the landscape
        g.translate(-maxCitiesWidth / 2, 0);

        int cityIndex = 0;
        for (CityscapeSingleSuit city : cities) {
            // draw city and iterate
            city.draw(g);
            cityIndex++;

            // move the anchor to the next position
            g.translate(city.getCityWidth() + multiCityBorderSize, 0);
        }

        g.popMatrix();
        g.endDraw();
    }

    @Override
    public void keyPressed(int key, int keyCode) {
        switch (key) {
            case 'l':
                for (CityscapeSingleSuit city : cities) {
                    city.switchLights();
                }
                break;

        }

        cities.forEach(c -> c.keyPressed(key, keyCode));
    }

    // ================================================================

    // InputListennerInterface

    @Override
    public void newEvent(InputEvent input) {

    }


    // ================================================================

    // Helpers

    private void loadProperties() {
        multiCityBorderSize = props.getInt("visualizer.suit01.multiCityBorderSize");
    }
}