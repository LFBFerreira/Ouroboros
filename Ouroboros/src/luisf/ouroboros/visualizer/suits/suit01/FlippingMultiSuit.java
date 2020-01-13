package luisf.ouroboros.visualizer.suits.suit01;

import luisf.interfaces.InputEvent;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.*;

import java.time.format.DateTimeFormatter;
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

    private int fontColor = 0x00000000;    // overriden by app.properties
    private int fontSize = 40;     // overriden by app.properties
    private PFont defaultFont;
    private PVector textAnchor;
    private String fontName = "Roboto Bold";

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
            CityscapeSingleSuit city = new CityscapeSingleSuit(project, parent);
            city.initialize();
            cities.add(city);
        }

        centerOffset = cities.get(0).getFloorHeight();

        worldRotationSpeed = worldRotationSpeedDefault;
        cityRotationPeakSpeed = cityRotationPeakSpeedDefault;

        defaultFont = parent.createFont(fontName, fontSize);
    }

    private void loadProperties() {
        cityRotationPeakSpeedDefault = props.getFloat("visualizer.suit01.cityRotationPeakSpeed");
        worldRotationSpeedDefault = props.getFloat("visualizer.suit01.worldRotationSpeed");
        targetDelayBetweenSwtich = props.getLong("visualizer.suit01.delayBetweenSwtich");
        fontColor = props.getInt("visualizer.suit01.fontColor");
        fontSize = props.getInt("visualizer.suit01.fontSize");
    }

    @Override
    public ProjectData getCurrentProject() {
        return projects.get(currentProjectIndex);
    }

    // ================================================================

    // DrawableInterface Interface

    @Override
    public void draw(PGraphics g) {
        g.beginDraw();

        // draw text statistics
        drawStatistics(g, projects.get(currentProjectIndex));

        //Handy.drawAxes(g, false);

        // update world
        updateCitiesRotation();
        updateWorldRotation();

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
                cities.forEach(c -> c.switchLights());
                break;
            case 'k':
                cities.forEach(c -> c.hideLights());
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
        } else if (input.isName("multifader1") && input.isGroup("1")) {
            cityRotationPeakSpeed = cityRotationPeakSpeedDefault * input.getAsFloat(0, 2);
            log.info("city speed " + cityRotationPeakSpeed);
        } else if (input.isName("multifader1") && input.isGroup("2")) {
            worldRotationSpeed = worldRotationSpeedDefault * input.getAsFloat(0, 2);
            log.info("world speed " + worldRotationSpeed);
        }
    }

    // ================================================================

    // Helpers

    private final int verticalShift = 40;
    private final int textBoxWidth = 1400;
    private final int textBoxHeight = 800;
    private final float smallFontMultiplier = 0.8f;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-mm-yyyy \thh:mm:ss");
    private final PVector textTranslationCoordinates = new PVector(-1000, -70);

    /**
     * @param g
     * @param project
     */
    private void drawStatistics(PGraphics g, ProjectData project) {
        textAnchor = new PVector(g.width * 0.2f, g.height * 0.3f);

        g.pushMatrix();
        g.pushStyle();

        // translate text origin
        g.translate(textTranslationCoordinates.x, textTranslationCoordinates.y);

        g.textFont(defaultFont);
        //g.textLeading(10);
        g.textAlign(parent.LEFT, parent.TOP);
        g.fill(fontColor);

        // project ID
        g.textSize(Math.round(fontSize * smallFontMultiplier));
        g.text("ID", textAnchor.x, textAnchor.y, textBoxWidth, textBoxHeight);
        textAnchor.y += verticalShift;

        g.textSize(fontSize);
        g.text(Handy.f("%s", project.id), textAnchor.x, textAnchor.y, textBoxWidth, textBoxHeight);
        textAnchor.y += verticalShift * 1.8f;

        // project date
        g.textSize(Math.round(fontSize * smallFontMultiplier));
        g.text("Date", textAnchor.x, textAnchor.y, textBoxWidth, textBoxHeight);
        textAnchor.y += verticalShift;

        g.textSize(fontSize);
        g.text(Handy.f("%s", project.commitDate.format(formatter)), textAnchor.x, textAnchor.y, textBoxWidth, textBoxHeight);
        textAnchor.y += verticalShift * 1.8f;

        // class sizes
        g.textSize(Math.round(fontSize * smallFontMultiplier));
        g.text("# Methods", textAnchor.x, textAnchor.y, textBoxWidth, textBoxHeight);
        textAnchor.y += verticalShift;

        g.textSize(fontSize);
        StringJoiner methodsSize = new StringJoiner(" ");
        project.classModels.forEach(m -> methodsSize.add(String.format("%02d", m.getMethods().size())));
        g.text(Handy.f("%s", methodsSize.toString()), textAnchor.x, textAnchor.y, textBoxWidth, textBoxHeight);
        textAnchor.y += verticalShift;

//        g.text(Handy.f("%d", project.classModels.size()), anchor.x, anchor.y, textBoxWidth, textBoxHeight);
//        anchor.y += verticalShift;

        g.popStyle();
        g.popMatrix();
    }

    private void nextCity() {
        log.info("Next city");

        if (currentProjectIndex + 1 < cities.size()) {
            nextProjectIndex = currentProjectIndex + 1;
            startRotatingLeft();
        } else {
            nextProjectIndex = 0;
            startRotatingLeft();
        }

        log.info(Handy.f("City: %s %s", projects.get(nextProjectIndex).getCommitDateFormated(),
                projects.get(nextProjectIndex).id));
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

        log.info(Handy.f("City: %s %s", projects.get(nextProjectIndex).getCommitDateFormated(),
                projects.get(nextProjectIndex).id));
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
