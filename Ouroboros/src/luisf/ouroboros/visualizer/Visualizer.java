package luisf.ouroboros.visualizer;

import luisf.ouroboros.hmi.HumanMachineInput;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.hmi.InputListennerInterface;
import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.colortools.ColorTools;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.scene.CameraMan;
import luisf.ouroboros.visualizer.suits.SuitBase;
import luisf.ouroboros.visualizer.suits.suit01.CityscapeSuit;
import oscP5.OscMessage;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;

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

    // ================================================================

    /**
     * Static Launcher
     *
     * @param graphicsFolder
     * @param models
     */
    public static void launchGenerator(File graphicsFolder, List<ClassModel> models) {
        Visualizer visualizer = new Visualizer(graphicsFolder, models);

        runSketch(new String[]{""}, visualizer);
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
        windowWidth = props.getInt("visualizer.windowWidth");
        windowHeight = props.getInt("visualizer.windowHeight");
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

        cameraMan = new CameraMan(this, (PGraphics3D) suitGraphics, graphicsFolder);
        cameraMan.initialize();

        suit = new CityscapeSuit(models, suitGraphics, this);
        suit.initialize();

        hmi = new HumanMachineInput(props.getInt("hmi.oscPort"), this);
        hmi.registerListeners(new InputListennerInterface[]{this, cameraMan, suit});

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

        suitGraphics.clear();

        // draw suit
        suit.draw(suitGraphics);

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
                (int)frameRate,
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





    private void oscEvent(OscMessage theOscMessage) {
        String addr = theOscMessage.addrPattern();

        /* print the address pattern and the typetag of the received OscMessage */
        log.info("Viz event addrpattern: " + theOscMessage.addrPattern() + " typetag: " + theOscMessage.typetag());

//        if (addr.equals("/3/fader1")) {
//            log.info("FADER 1");
//        } else if (addr.equals("/1/fader2")) {
//            log.info("FADER 2");
//        } else if (addr.equals("/1/fader3")) {
//            log.info("CROSSFADE");
//        } else if (addr.equals("/1/rotary1")) {
//            log.info("ROTARY INPUT 1");
//        } else if (addr.equals("/3/xy")) {
//        }
    }
}
