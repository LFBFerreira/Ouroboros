package luisf.ouroboros.visualizer.suits.suit01;

import luisf.interfaces.InputEvent;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.*;
import java.util.logging.Logger;

public class FlippingMultiSuit extends SuitBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private List<CityscapeSingleSuit> cities = new LinkedList<>();
    private List<ProjectData> projects = new LinkedList<>();

    private int currentProjectIndex = 0;
    private int nextProjectIndex = -1;
    private float platformAngle = 0;
    private float rotationSpeed = 0;
    private float rotationAcceleration = 0;
    private float currentWorldRotation = 0;
    private float cityRotationPeakSpeed = 0;
    private float worldRotationSpeed = 0;

    // controls if the rotation is on (!=0), and the side(left <0, right>0)
    private int rotationTrigger = 0;
    private int centerOffset = 0;

    private float cityRotationPeakSpeedDefault = 0.005f;
    private float worldRotationSpeedDefault = 0.006f;
    private long targetDelayBetweenSwtich = 3000L;

    // ================================================================

    /**
     * Constructor
     *
     * @param projects
     * @param parent
     */
    public FlippingMultiSuit(List<ProjectData> projects, PApplet parent) {
        super(null, parent);
        this.projects = projects;
    }

    // ================================================================

    // Public

    public void initialize() {
        loadProperties();

        // order projects by date
        projects.sort(new Comparator<ProjectData>() {
            @Override
            public int compare(ProjectData o1, ProjectData o2) {
                return o1.commitDate.compareTo(o2.commitDate);
            }
        });

        // initialize cities
        for (ProjectData project : projects) {
            CityscapeSingleSuit city = new CityscapeSingleSuit(project.classModels, parent);
            city.initialize();
            cities.add(city);
        }

        centerOffset = cities.get(0).getFloorHeight();

        worldRotationSpeed = worldRotationSpeedDefault;
        cityRotationPeakSpeed = cityRotationPeakSpeedDefault;
    }

    private void loadProperties() {
        cityRotationPeakSpeedDefault = props.getFloat("visualizer.suit01.cityRotationPeakSpeed");
        worldRotationSpeedDefault = props.getFloat("visualizer.suit01.worldRotationSpeed");
        targetDelayBetweenSwtich = props.getLong("visualizer.suit01.delayBetweenSwtich");
    }

    // ================================================================

    // DrawableInterface Interface

    @Override
    public void draw(PGraphics g) {
        g.beginDraw();
        g.pushMatrix();

        //Handy.drawAxes(g, false, 200);

        // update world
        updateCitiesRotation();
        updateWorldRotation();

        // scale everything
        g.scale(2.2f);

        // constant world rotation
        g.rotateY(currentWorldRotation);

        // draw current project
        g.pushMatrix();
        // citie rotation
        g.rotateZ(platformAngle);
        // push the floor outside of the center so it doesn't overlap with the other citie's floor
        g.translate(0, -centerOffset);
        // scale the city to its real size
        g.scale(parent.map(parent.abs(platformAngle), PConstants.PI, 0, 0, 1));

        cities.get(currentProjectIndex).draw(g);
        g.popMatrix();

        // draw next project if visible
        if (nextProjectIndex >= 0) {
            g.pushMatrix();
            // citie rotation
            g.rotateZ(PConstants.PI - platformAngle * -1);
            // push the floor outside of the center so it doesn't overlap with the other citie's floor
            g.translate(0, -centerOffset);
            // scale the city to its real size
            g.scale(parent.map(parent.abs(platformAngle), 0, PConstants.PI, 0, 1));

            cities.get(nextProjectIndex).draw(g);
            g.popMatrix();
        }

        g.popMatrix();
        g.endDraw();
    }

    @Override
    public void keyPressed(int key, int keyCode) {
        switch (key) {
            case '+':
                nextCity();
                break;
            case '-':
                previousCity();
                break;
            case '0':
                iterateThroughCitites();
                break;
            case 'l':
                for (CityscapeSingleSuit city : cities) {
                    city.switchLights();
                }
                break;
            case '.':
                stopIterateThroughCities();
                break;
        }
    }


    // ================================================================

    // InputListennerInterface

    @Override
    public void newEvent(InputEvent input) {
        log.info("Input: " + input.toString());

        if (input.isPage("2") && input.isName("push01") && input.isReleased()) {
            previousCity();
        } else if (input.isPage("2") && input.isName("push02") && input.isReleased()) {
            nextCity();
        } else if (input.isPage("2") && input.isName("fader01")) {
            cityRotationPeakSpeed = cityRotationPeakSpeedDefault * input.getAsFloat(0, 2);
            log.info("speed " + cityRotationPeakSpeed);
        } else if (input.isPage("2") && input.isName("fader02")) {
            worldRotationSpeed = worldRotationSpeedDefault * input.getAsFloat(0, 2);
            log.info("speed " + worldRotationSpeed);
        }
    }

    // ================================================================

    // Helpers

    private void nextCity() {
        log.info("Next city");

        if (currentProjectIndex + 1 < cities.size()) {
            nextProjectIndex = currentProjectIndex + 1;
            startRotatingLeft();
        } else {
            nextProjectIndex = 0;
            startRotatingLeft();
        }
    }

    private void previousCity() {
        log.info("Previous city");

        if (currentProjectIndex - 1 >= 0) {
            nextProjectIndex = currentProjectIndex - 1;
            startRotatingRight();
        } else {
            nextProjectIndex = cities.size() - 1;
            startRotatingRight();
        }
    }

    private TimerTask iterateThroughCitiesTask;

    private void iterateThroughCitites() {
        log.info("Start auto iteration");

        iterateThroughCitiesTask = new TimerTask() {
            public void run() {
                nextCity();
            }
        };
        Timer timer = new Timer("Timer");

        timer.scheduleAtFixedRate(iterateThroughCitiesTask, 0, targetDelayBetweenSwtich);
    }

    private void stopIterateThroughCities() {
        log.info("Stop auto iteration");
        iterateThroughCitiesTask.cancel();
    }

//    private void delayedNextCity() {
//        TimerTask task = new TimerTask() {
//            public void run() {
//                nextCity();
//            }
//        };
//        Timer timer = new Timer("Timer");
//
//        long delay = 1000L;
//        timer.schedule(task, delay);
//    }

    private void startRotatingLeft() {
        rotationTrigger = -1;
    }

    private void startRotatingRight() {
        rotationTrigger = 1;
    }

    private void updateCitiesRotation() {
        // if rotation is active, update the angle
        if (rotationTrigger != 0) {
            // update acceleration
            rotationAcceleration = parent.map(parent.cos(platformAngle), 0, PConstants.TWO_PI, 0, 1)
                    * cityRotationPeakSpeed;

            // invert acceleration if trigger points left
            if (rotationTrigger < 0) {
                rotationAcceleration *= -1;
            }

            // update speed
            rotationSpeed += rotationAcceleration;

            // update angle
            platformAngle += rotationSpeed;
        }

        citieRotationStopCondition();
    }

    private void citieRotationStopCondition() {
        if (platformAngle > parent.PI || platformAngle < -parent.PI) {
            stopRotation();
        }
    }

    private void stopRotation() {
        log.info("Stopping rotation");

        // reset rotation variables
        rotationAcceleration = 0;
        rotationSpeed = 0;
        platformAngle = 0;
        rotationTrigger = 0;

        // reset citie's indexes
        currentProjectIndex = nextProjectIndex;
        nextProjectIndex = -1;
    }

    private void updateWorldRotation() {
        currentWorldRotation += worldRotationSpeed;
    }
}
