package luisf.ouroboros.visualizer;

import luisf.interfaces.InputEvent;
import luisf.interfaces.InputListennerInterface;
import luisf.interfaces.OscP5Manager;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.common.colortools.ColorTools;

import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.scene.CameraControlAgent;
import luisf.ouroboros.visualizer.scene.MyScene;
import luisf.ouroboros.visualizer.suits.SuitBase;
import luisf.ouroboros.visualizer.suits.suit01.CityscapeMultiSuit;
import luisf.ouroboros.visualizer.suits.suit01.FlippingMultiSuit;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.io.File;
import java.util.List;
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

    private int windowHeight = 768;
    private int windowWidth = 1024;

    private final String renderer = P3D;

    private int[] backgroundColors = new int[]{0xFFD5D8DC, 0xFFACB5C6};

    private OscP5Manager hmi;

    private CameraControlAgent oscControl;

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

        String[] args = new String[]{"--display=" + screenIndex, visualizer.getClass().getSimpleName()};

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
    }

    private void loadProperties() {
        backgroundColors = props.getIntArray("visualizer.backgroundColors");
        windowWidth = props.getInt("visualizer.window.width");
        windowHeight = props.getInt("visualizer.window.height");
    }

    // ================================================================

    /**
     * Settings
     */
    public void settings() {
        size(windowWidth, windowHeight, renderer);
        smooth(8);
    }

    /**
     * Setup
     */
    public void setup() {
        frameRate(60);

        suitGraphics = createGraphics(windowWidth, windowHeight, renderer);
        backgroundGraphics = createGraphics(windowWidth, windowHeight, renderer);

//        suit = new CityscapeSuit(models,this);
        suit = new CityscapeMultiSuit(projects, this);
//        suit = new FlippingMultiSuit(projects, this);

        suit.initialize();

        myScene = new MyScene(this, suitGraphics, graphicsFolder, suit);
        myScene.initialize();

        oscControl = new CameraControlAgent(myScene);

        hmi = new OscP5Manager(props.getInt("hmi.oscPort"), this);
        hmi.registerListeners(new InputListennerInterface[]{oscListenner, myScene, suit, oscControl});

        createBackground();
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
        image(backgroundGraphics, 0, 0);

        myScene.beginDraw();
        myScene.clearScene();
        // suit is drawn after endDraw
        myScene.endDraw();

        // copy scene output to main graphics buffer
        myScene.display();

        // save frame if record mode is on
        myScene.addFrameToVideo();
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

        }
    };

    // ================================================================

    // Helpers

    private void setWindowTitle() {
        surface.setTitle(String.format("Ouroboros (%d fps) %s",
                (int) frameRate,
                myScene.isCapturing() ? "[Recording]" : ""));
    }

    private void createBackground() {
        backgroundGraphics.beginDraw();

        ColorTools.fillGradient(backgroundColors,
                new PVector(backgroundGraphics.width * 0.1f, 0),
                new PVector(backgroundGraphics.width * 0.8f, backgroundGraphics.height),
                backgroundGraphics,
                false);

        backgroundGraphics.endDraw();
    }

}
