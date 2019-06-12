package luisf.ouroboros.visualizer;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.colortools.ColorTools;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.scene.CameraMan;
import luisf.ouroboros.visualizer.suits.SuitBase;
import luisf.ouroboros.visualizer.suits.suit01.CityscapeSuit;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphics3D;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Visualizer extends PApplet {
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

    private PShape floor;
    private final int floorWidth = 500;
    private final int floorDepth = 1100;

    private int[] backgroundColors = new int[]{0xFFD5D8DC, 0xFFACB5C6};

    private Boolean lightsOn = true;

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

        floor = createFloor();

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

        lights();

        // Background
        //backgroundGraphics(100);
        image(backgroundGraphics, 0, 0);

        cameraMan.beginDraw();

        suitGraphics.clear();

        // draw floow
        drawFloor(suitGraphics);

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

        switch (key) {
            case 'l':
                lightsOn = !lightsOn;
                log.info(Handy.f("Lights are %s", lightsOn ? "On" : "Off"));
                break;
        }

        // forward the event
        suit.keyPressed(key, keyCode);
        cameraMan.keyPressed(key, keyCode);
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

    private PShape createFloor()
    {
        PShape floor = createShape(BOX, floorWidth, 4, floorDepth);
        floor.setFill(props.getInt("visualizer.floorColor"));
        floor.setStroke(200);

        return floor;
    }

    private void drawFloor(PGraphics graphics) {
        graphics.pushMatrix();

        // lower the floor so that the surface matches y = 0
        graphics.translate(0, 5, 0);

        graphics.shape(floor);

        graphics.popMatrix();
    }
}
