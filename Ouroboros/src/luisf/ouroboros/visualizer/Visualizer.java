package luisf.ouroboros.visualizer;

import luisf.interfaces.InputEvent;
import luisf.interfaces.InputListennerInterface;
import luisf.interfaces.OscP5Manager;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.common.colortools.ColorTools;

import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.scene.CameraControlAgent;
import luisf.ouroboros.visualizer.scene.MyScene;
import luisf.ouroboros.visualizer.suits.SuitBase;
import luisf.ouroboros.visualizer.suits.suit01.CityscapeMultiSuit;
import luisf.ouroboros.visualizer.suits.suit01.FlippingMultiSuit;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

import java.io.File;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;

public class Visualizer extends PApplet {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private boolean parseSingleProject = false;

    public List<ProjectData> projects;
    public File graphicsFolder;

    private SuitBase suit;
    private PGraphics suitGraphics;
    private PGraphics backgroundGraphics;
    private MyScene myScene;

    private int windowHeight;               // overriden by app.properties
    private int windowWidth;                // overriden by app.properties
    private final String renderer = P3D;

    private int[] backgroundColors;         // overriden by app.properties
    private boolean backgroundChanged = false;
    private OscP5Manager hmi;

//    private CameraControlAgent oscControl;

    private int titleFontColor;             // overriden by app.properties
    private int titleFontSize;              // overriden by app.properties
    private PFont defaultFont;

    // ================================================================

    /**
     * Static Launcher
     * available arguments at: https://github.com/processing/processing/blob/master/core/src/processing/core/PApplet.java
     *
     * @param graphicsFolder
     * @param projects
     */
    public static void launchGenerator(File graphicsFolder, List<ProjectData> projects, boolean parseSingleProject) {
        PropertyManager props = PropertyManager.getInstance();

        Visualizer visualizer = new Visualizer(graphicsFolder, projects, parseSingleProject);

        int screenIndex = props.getInt("visualizer.displayScreen");
        log.info("Displaying on Screen " + screenIndex);

        String[] args = new String[]{
                "--display=" + screenIndex,
                "--location=100,50",
//                "--sketch-path=" + sketchPath,
                visualizer.getClass().getSimpleName()};

        runSketch(args, visualizer);
    }

    // ================================================================

    /**
     * Constructor
     *
     * @param graphicsFolder
     * @param projects
     */
    public Visualizer(File graphicsFolder, List<ProjectData> projects, boolean parseSingleProject) {
        this.projects = projects;
        this.parseSingleProject = parseSingleProject;
        this.graphicsFolder = graphicsFolder;

        loadProperties();

        // trigger first draw
        backgroundChanged = true;
    }

    private void loadProperties() {
        backgroundColors = props.getIntArray("visualizer.backgroundColors");
        windowWidth = props.getInt("visualizer.window.width");
        windowHeight = props.getInt("visualizer.window.height");
        titleFontColor = props.getInt("visualizer.titleFontColor");
        titleFontSize = props.getInt("visualizer.titleFontSize");
    }

    // ================================================================

    /**
     * Settings
     */
    public void settings() {
        size(windowWidth, windowHeight, renderer);
        smooth(4);
    }

    /**
     * Setup
     */
    public void setup() {
        frameRate(30);

        suitGraphics = createGraphics(windowWidth, windowHeight, renderer);
        backgroundGraphics = createGraphics(windowWidth, windowHeight, renderer);

//        suit = new CityscapeSuit(models,this);
        suit = new CityscapeMultiSuit(projects, this);
//        suit = new FlippingMultiSuit(projects, this);

        suit.initialize();

        myScene = new MyScene(this, suitGraphics, graphicsFolder, suit);
        myScene.initialize();

        hmi = new OscP5Manager(props.getInt("hmi.oscPort"), this);
        hmi.registerListeners(new InputListennerInterface[]{oscListenner, myScene, suit});

        myScene.setCameraPosition(suit.getInitialCameraPosition());

        defaultFont = createFont("Roboto", titleFontSize);
    }

    /**
     * Dispose
     */
    public void dispose() {
        log.info(String.format("Terminating %s", Visualizer.class.getSimpleName()));

        hmi.dispose();
        myScene.dispose();
    }

    // ================================================================

    /**
     * Draw
     */
    public void draw() {
        setWindowTitle();

        // Background
        //backgroundGraphics(100);
        createBackground(backgroundGraphics, backgroundColors);
        image(backgroundGraphics, 0, 0);

        myScene.beginDraw();
        myScene.clearScene();
        // suit is drawn after endDraw
        //myScene.drawTitle(suit.getTitle());

        myScene.endDraw();

        // copy scene output to main graphics buffer
        myScene.display();

        //drawStatistics(g, suit.getCurrentProject());

        // save frame if record mode is on
        myScene.addFrameToVideo();
    }

    private final int verticalShift = 20;
    private final int textBoxWidth = 600;
    private final int textBoxHeight = 400;

    public void drawStatistics(PGraphics g, ProjectData project) {
        g.pushStyle();

        g.textFont(defaultFont);
        g.textLeading(10);
        g.textAlign(LEFT, TOP);
        g.fill(titleFontColor);
        g.textSize(titleFontSize);

        PVector anchor = new PVector(g.width * 0.2f, g.height * 0.1f);
        g.text(Handy.f("%s", project.id), anchor.x, anchor.y, textBoxWidth, textBoxHeight);
        anchor.y += verticalShift * 2;
        g.text(Handy.f("%s", project.commitDate), anchor.x, anchor.y, textBoxWidth, textBoxHeight);
        anchor.y += verticalShift;
        g.text(Handy.f("%d", project.classModels.size()), anchor.x, anchor.y, textBoxWidth, textBoxHeight);
        anchor.y += verticalShift;
        StringJoiner methodsSize = new StringJoiner(" ");
        project.classModels.forEach(m -> methodsSize.add(String.format("%02d", m.getMethods().size())));
        g.text(Handy.f("%s", methodsSize.toString()), anchor.x, anchor.y, textBoxWidth, textBoxHeight);
        anchor.y += verticalShift;

        g.popStyle();
    }
    // ================================================================

    /**
     * Key pressed
     */

    public void keyPressed() {
        // forward the event
        suit.keyPressed(key, keyCode);
        myScene.keyPressed(key, keyCode);
    }

    // ================================================================

    // InputListennerInterface
    private InputListennerInterface oscListenner = new InputListennerInterface() {
        @Override
        public void newEvent(InputEvent input) {
            /*
            if (input.isName("multifader1") && input.isGroup("1")) {
                backgroundColors[0] = ColorTools.composeclr(
                        input.getAsFloat(0, 1),
                        green(backgroundColors[0]) / 255,
                        blue(backgroundColors[0]) / 255,
                        1);
                backgroundChanged = true;
            } else if (input.isName("multifader1") && input.isGroup("2")) {
                backgroundColors[0] = ColorTools.composeclr(
                        red(backgroundColors[0]) / 255,
                        input.getAsFloat(0, 1),
                        blue(backgroundColors[0]) / 255,
                        1);
                backgroundChanged = true;
            } else if (input.isName("multifader1") && input.isGroup("3")) {
                backgroundColors[0] = ColorTools.composeclr(
                        red(backgroundColors[0]) / 255,
                        green(backgroundColors[0]) / 255,
                        input.getAsFloat(0, 1),
                        1);
                backgroundChanged = true;
            } else if (input.isName("multifader1") && input.isGroup("4")) {
                backgroundColors[1] = ColorTools.composeclr(
                        input.getAsFloat(0, 1),
                        green(backgroundColors[1]) / 255,
                        blue(backgroundColors[1]) / 255,
                        1);
                backgroundChanged = true;
            } else if (input.isName("multifader1") && input.isGroup("5")) {
                backgroundColors[1] = ColorTools.composeclr(
                        red(backgroundColors[1]) / 255,
                        input.getAsFloat(0, 1),
                        blue(backgroundColors[1]) / 255,
                        1);
                backgroundChanged = true;
            } else if (input.isName("multifader1") && input.isGroup("6")) {
                backgroundColors[1] = ColorTools.composeclr(
                        red(backgroundColors[1]) / 255,
                        green(backgroundColors[1]) / 255,
                        input.getAsFloat(0, 1),
                        1);
                backgroundChanged = true;
            }
            */
        }
    };

    // ================================================================

    // Helpers

    private void setWindowTitle() {
        surface.setTitle(String.format("Ouroboros (%d fps) %s",
                (int) frameRate,
                myScene.isCapturing() ? "[Recording]" : ""));
    }

    private void createBackground(PGraphics background, int[] backgroundColors) {
        if (!backgroundChanged) {
            return;
        }

        background.beginDraw();

        background.clear();

        ColorTools.fillGradient(backgroundColors,
                new PVector(background.width * 0.1f, 0),
                new PVector(background.width * 0.8f, background.height),
                background,
                false);

        background.endDraw();

        backgroundChanged = false;
    }
}
