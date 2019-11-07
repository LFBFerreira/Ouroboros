package luisf.ouroboros.visualizer.suits.suit01;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.hmi.InputEvent;
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
    private float currentWorldRotation = 0;

    // controls if the rotation is on (!=0), and the side(left <0, right>0)
    private int rotationTrigger = 0;

    private int centerOffset = 0;
    private final float targetCitySwtichRotationSpeed = 0.008f;
    private final float targetCityRotationPeakSpeed = 0.4f;
    private final float defaultWorldRotationSpeed = 0.006f;
    private final long targetDelayBetweenSwtich = 2500L;

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
    }

    private void loadProperties() {
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
                iterateThroughCitiesTask.cancel();
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

    private void nextCity() {
        if (currentProjectIndex + 1 < cities.size()) {
            nextProjectIndex = currentProjectIndex + 1;
            startRotatingLeft();
        }
        else
        {
            nextProjectIndex = 0;
            startRotatingLeft();
        }
    }

    private void previousCity() {
        if (currentProjectIndex - 1 >= 0) {
            nextProjectIndex = currentProjectIndex - 1;
            startRotatingRight();
        }
        else
        {
            nextProjectIndex = cities.size();
            startRotatingLeft();
        }
    }

    TimerTask iterateThroughCitiesTask;
    private void iterateThroughCitites() {
        iterateThroughCitiesTask = new TimerTask() {
            public void run() {
                nextCity();
            }
        };
        Timer timer = new Timer("Timer");

        timer.scheduleAtFixedRate(iterateThroughCitiesTask, 0, targetDelayBetweenSwtich);
    }

    private void delayedNextCity() {
        TimerTask task = new TimerTask() {
            public void run() {
                nextCity();
            }
        };
        Timer timer = new Timer("Timer");

        long delay = 1000L;
        timer.schedule(task, delay);
    }

    private void startRotatingLeft() {
        rotationTrigger = -1;
    }

    private void startRotatingRight() {
        rotationTrigger = 1;
    }

    private void updateCitiesRotation() {

        // if rotation is active, update the angle
        if (rotationTrigger != 0) {

            rotationSpeed = parent.map(parent.sin(platformAngle), 0, PConstants.TWO_PI, 0, 1)
                    * targetCityRotationPeakSpeed + targetCitySwtichRotationSpeed;

            if (rotationTrigger < 0) {
                platformAngle += rotationSpeed;
            } else {
                platformAngle -= rotationSpeed;
            }
        }

        citieRotationStopCondition();
    }

    private void citieRotationStopCondition() {
        if (platformAngle > parent.PI || platformAngle < -parent.PI) {
            stopRotation();
        }
    }

    private void stopRotation() {

        // reset rotation variables
        platformAngle = 0;
        rotationTrigger = 0;

        // reset citie's indexes
        currentProjectIndex = nextProjectIndex;
        nextProjectIndex = -1;

        // TODO remove later!
        //nextCity();
    }

    private void updateWorldRotation() {
        currentWorldRotation += defaultWorldRotationSpeed;
    }

}
