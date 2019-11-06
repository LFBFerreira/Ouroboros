package luisf.ouroboros.visualizer.suits.suit01;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
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
    private final float defaultRotationSpeed = 0.08f;
    private float currentWorldRotation = 0;

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
    }

    private void loadProperties() {
    }

    // ================================================================

    // DrawableInterface Interface

    private final int samplesPerRow = 9;

    @Override
    public void draw(PGraphics g) {

        g.beginDraw();
        g.pushMatrix();

        //Handy.drawAxes(g, false, 200);

        // update rotation
        updateRotation();

        g.scale(2.0f);
        currentWorldRotation += 0.005;
        g.rotateY(currentWorldRotation);

        // draw current project
        g.pushMatrix();
        g.rotateZ(platformAngle);
        g.scale(parent.map(parent.abs(platformAngle), PConstants.PI, 0, 0, 1));

        cities.get(currentProjectIndex).draw(g);
        g.popMatrix();

        // draw next project if visible
        if (nextProjectIndex >= 0) {
            g.pushMatrix();
            g.rotateZ(PConstants.PI - platformAngle * -1);
            g.scale(parent.map(parent.abs(platformAngle), 0, PConstants.PI, 0, 1));
            //g.translate(0, -40);

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
                if (currentProjectIndex + 1 < cities.size()) {
                    nextProjectIndex = currentProjectIndex + 1;
                    startRotatingLeft();
                }
                break;
            case '-':
                if (currentProjectIndex - 1 >= 0) {
                    nextProjectIndex = currentProjectIndex - 1;
                    startRotatingRight();
                }
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

    private void startRotatingLeft() {
        rotationSpeed = defaultRotationSpeed;
    }

    private void startRotatingRight() {
        rotationSpeed = -defaultRotationSpeed;
    }

    private void updateRotation() {
        platformAngle += rotationSpeed;

        if (platformAngle > parent.PI || platformAngle < -parent.PI) {
            stopRotation();
        }
    }

    private void stopRotation() {
        //log.info("Stopping rotation");
        rotationSpeed = 0;
        platformAngle = 0;

        currentProjectIndex = nextProjectIndex;
        nextProjectIndex = -1;
    }
}
