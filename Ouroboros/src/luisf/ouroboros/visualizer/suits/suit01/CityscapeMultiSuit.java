package luisf.ouroboros.visualizer.suits.suit01;

import luisf.interfaces.InputEvent;
import luisf.ouroboros.common.Handy;
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

    private int multiCityHorizontalBorderSize;        // overriden by app.properties
    private int multiCityVerticalBorderSize;        // overriden by app.properties
    private int maxCitiesWidth = 0;

    private PVector initialCameraPosition = new PVector(0, 0, 3000);

    private int samplesPerRow;                  // overriden by app.properties
    private int matrixNumberRows = 0;
    private float worldRotationSpeed = 0;       // overriden by app.properties
    private float currentWorldRotation = 0;
    private float displayInclination;           // overriden by app.properties

    private float defaultCityRotationSpeed = 0.01f;

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

            maxCitiesWidth += city.getCityWidth() + multiCityHorizontalBorderSize;
        }

        matrixNumberRows = (int) Math.ceil(projects.size() / samplesPerRow);
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

    // linear draw
//    @Override
//    public void draw(PGraphics g) {
//        g.beginDraw();
//        g.pushMatrix();
//
//        //Handy.drawAxes(g, false, 200);
//
//        // initial offset to center the landscape
//        g.translate(-maxCitiesWidth / 2, 0);
//
//        int cityIndex = 0;
//        for (CityscapeSingleSuit city : cities) {
//            // draw city and iterate
//            city.draw(g);
//            cityIndex++;
//
//            // move the anchor to the next position
//            g.translate(city.getCityWidth() + multiCityBorderSize, 0);
//        }
//
//        g.popMatrix();
//        g.endDraw();
//    }

    // matrix draw
    @Override
    public void draw(PGraphics g) {
        updateCityRotation();

        g.beginDraw();
        g.pushMatrix();

        //Handy.drawAxes(g, false, 200);

        // initial offset to center the landscape
        g.translate(-(samplesPerRow / 2) * multiCityHorizontalBorderSize, -(matrixNumberRows / 2) * multiCityVerticalBorderSize);

        int cityIndex = 0;
        for (CityscapeSingleSuit city : cities) {
            if (cityIndex % samplesPerRow == 0 && cityIndex > 0) {
                g.translate(-(samplesPerRow) * multiCityHorizontalBorderSize, multiCityVerticalBorderSize);
            }
            else
            {
                //g.translate(0, 50, 0);
            }

            // draw in a tilted and rotated canvas
            g.pushMatrix();
            g.rotateX(-displayInclination);
            g.rotateY(currentWorldRotation);
            city.draw(g);
            g.popMatrix();

            // move the anchor to the next position
            g.translate(multiCityHorizontalBorderSize, 0);

            cityIndex++;
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
//        log.info(Handy.f());
        if (input.isPage("1") && input.isName("multipush1") && input.isReleased()) {
            stopCityRotation();
        } else if (input.isPage("1") && input.isName("multipush1") && input.isReleased()) {
            resumeCityRotation();
        } else if (input.isName("multifader1") && input.isGroup("2")) {
            worldRotationSpeed = defaultCityRotationSpeed * input.getAsFloat(0, 2);
            log.info("world speed " + worldRotationSpeed);
        }
    }


    // ================================================================

    // Helpers

    private void updateCityRotation() {
        currentWorldRotation += worldRotationSpeed;
    }

    private float lastCityRotationSpeed = 0;
    private void stopCityRotation()
    {
        log.info("Stopping city rotation");
        lastCityRotationSpeed = lastCityRotationSpeed;
        worldRotationSpeed = 0;
    }

    private void resumeCityRotation()
    {
        log.info("Resuming city rotation");
        worldRotationSpeed = lastCityRotationSpeed;
    }


    private void loadProperties() {
        multiCityHorizontalBorderSize = props.getInt("visualizer.suit01.multiCityHorizontalBorderSize");
        multiCityVerticalBorderSize = props.getInt("visualizer.suit01.multiCityVerticalBorderSize");
        samplesPerRow = props.getInt("visualizer.suit01.multiCitySamplesPerRow");
        worldRotationSpeed = props.getFloat("visualizer.suit01.worldRotationSpeed");
        displayInclination = props.getFloat("visualizer.suit01.multiCityDisplayAngle");
    }
}