package luisf.ouroboros.visualizer;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.common.colortools.ColorTools;
import luisf.ouroboros.visualizer.scene.CameraMan;
import luisf.ouroboros.visualizer.skins.SkinBase;
import luisf.ouroboros.visualizer.skins.SlicedSkin;
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

    public List<ClassModel> models = new LinkedList<>();
    public File graphicsFolder;

    private SkinBase codeSkin;
    private PGraphics skinGraphics;
    private PGraphics background;
    private CameraMan cameraMan;

    private final int windowHeight = 768;
    private final int windowWidth = 1024;

    private final String renderer = P3D;

    private PShape floor;
    private final int floorWidth = 500;
    private final int floorDepth = 1100;

    private final int[] backgroundColors = new int[]{0xFFD5D8DC, 0xFFACB5C6};

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
    }

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

        skinGraphics = createGraphics(windowWidth, windowHeight, renderer);
        background = createGraphics(windowWidth, windowHeight, renderer);

        cameraMan = new CameraMan(this, (PGraphics3D) skinGraphics, graphicsFolder);
        cameraMan.initialize();

        codeSkin = new SlicedSkin(models, skinGraphics, this);
        codeSkin.initialize();

        floor = initializeFloor();

        initializeBackground();
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
        surface.setTitle(String.format("Ouroboros (%d fps)", (int)frameRate));

        lights();

        // Background
        //background(100);
        image(background, 0, 0);

        // Camera and Skin
        cameraMan.beginDraw();

        skinGraphics.clear();

        drawFloor(skinGraphics);
        codeSkin.draw(skinGraphics);

        cameraMan.endDraw();

        cameraMan.display();

//         must be called after main buffer is updated
        cameraMan.addFrameToVideo(g);
    }


    // ================================================================

    /**
     * Key pressed
     */

    public void keyPressed() {

        // forward the event
        codeSkin.keyPressed(key, keyCode);
        cameraMan.keyPressed(key, keyCode);
    }

    // ================================================================

    // Helpers

    private void initializeBackground() {
        background.beginDraw();

//        setGradient(0, 0, background.width, background.height, 0xFF000000, 0xFFFFFFFF, true, background);

        ColorTools.fillGradient(backgroundColors,
                new PVector(background.width * 0.1f, 0),
                new PVector(background.width * 0.8f, background.height),
                background,
                false);

        background.endDraw();
    }

    private PShape initializeFloor()
    {
        PShape floor = createShape(BOX, floorWidth, 4, floorDepth);
        floor.setFill(color(180));
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
