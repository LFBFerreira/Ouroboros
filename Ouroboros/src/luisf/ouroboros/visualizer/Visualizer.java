package luisf.ouroboros.visualizer;

import luisf.ouroboros.common.colortools.ColorTools;
import luisf.ouroboros.hmi.HumanMachineInput;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.hmi.InputListennerInterface;
import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.scene.CameraControlAgent;
import luisf.ouroboros.visualizer.scene.CameraMan;
import luisf.ouroboros.visualizer.suits.SuitBase;
import luisf.ouroboros.visualizer.suits.suit01.CityscapeSuit;
import oscP5.OscMessage;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PGraphics3D;
import remixlab.proscene.MouseAgent;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Visualizer extends PApplet implements InputListennerInterface {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    public List<ClassModel> models = new LinkedList<>();
    public File graphicsFolder;

    private SuitBase suit;
    private PGraphics suitGraphics;
    private PGraphics backgroundGraphics;
    private CameraMan cameraMan;

    private int windowHeight = 768;
    private int windowWidth = 1024;

    private final String renderer = P3D;


    private int[] backgroundColors = new int[]{0xFFD5D8DC, 0xFFACB5C6};

    private HumanMachineInput hmi;

    private CameraControlAgent oscControl ;

    // ================================================================

    /**
     * Static Launcher
     * available arguments at: https://github.com/processing/processing/blob/master/core/src/processing/core/PApplet.java
     *
     * @param graphicsFolder
     * @param models
     */
    public static void launchGenerator(File graphicsFolder, List<ClassModel> models) {
        PropertyManager props = PropertyManager.getInstance();

        Visualizer visualizer = new Visualizer(graphicsFolder, models);

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
     * @param models
     */
    public Visualizer(File graphicsFolder, List<ClassModel> models) {
        this.models = models;
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
//    hint()
    }

    /**
     * Setup
     */
    public void setup() {
        frameRate(60);

        suitGraphics = createGraphics(windowWidth, windowHeight, renderer);
        backgroundGraphics = createGraphics(windowWidth, windowHeight, renderer);

        suit = new CityscapeSuit(models, suitGraphics, this);
        suit.initialize();

        cameraMan = new CameraMan(this, suitGraphics, graphicsFolder, suit);
        cameraMan.initialize();

        oscControl = new CameraControlAgent(cameraMan);


        hmi = new HumanMachineInput(props.getInt("hmi.oscPort"), this);
        hmi.registerListeners(new InputListennerInterface[]{this, cameraMan, suit, oscControl});

        createBackground();
    }

    /**
     * Dispose
     */
    public void dispose() {
        log.info(String.format("Terminating %s", Visualizer.class.getSimpleName()));

        cameraMan.dispose();
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

        cameraMan.beginDraw();
        //cameraMan.changeLook();

        suitGraphics.clear();

        suitGraphics.beginDraw();
//        suitGraphics.translate(500, 0, 0);

        // draw suit
        suit.draw(suitGraphics);

        suitGraphics.endDraw();

        cameraMan.endDraw();

        cameraMan.display();

        cameraMan.addFrameToVideo(g);
    }

    // ================================================================

    /**
     * Key pressed
     */

    public void keyPressed() {

        // forward the event
        suit.keyPressed(key, keyCode);
        cameraMan.keyPressed(key, keyCode);
    }

    // ================================================================

    // InputListennerInterface

    @Override
    public void reactToInput(InputEvent input) {

    }

    // ================================================================

    // Helpers

    private void setWindowTitle() {
        surface.setTitle(String.format("Ouroboros (%d fps) %s",
                (int) frameRate,
                cameraMan.isCapturing() ? "[Recording]" : ""));
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
