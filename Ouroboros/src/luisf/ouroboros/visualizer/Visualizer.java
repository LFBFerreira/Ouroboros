package luisf.ouroboros.visualizer;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.visualizer.scene.CameraMan;
import luisf.ouroboros.visualizer.skins.DrawableInterface;
import luisf.ouroboros.visualizer.skins.SkinBase;
import luisf.ouroboros.visualizer.skins.SlicedSkin;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Visualizer extends PApplet {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public List<ClassModel> models = new LinkedList<>();
    public File graphicsFolder = null;

    private SkinBase codeSkin;
    private PGraphics skinGraphics;
    private CameraMan cameraMan;

    private final int windowHeight = 768;
    private final int windowWidth = 1024;

    private final String renderer = P3D;

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
     * Dispose
     */
    public void dispose() {
        log.info(String.format("Terminating %s", Visualizer.class.getSimpleName()));
    }

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

        skinGraphics = createGraphics(windowWidth, windowHeight, renderer);

        codeSkin = new SlicedSkin(models, skinGraphics, this);

        codeSkin.initialize();

        cameraMan = new CameraMan(this, skinGraphics);
        cameraMan.init();
    }

    // ================================================================

    /**
     * Draw
     */
    public void draw() {
        background(100);

        cameraMan.beginDraw();
        cameraMan.pg().clear();

        if (codeSkin != null) {
            codeSkin.draw(cameraMan.pg());
        }

        cameraMan.endDraw();

        cameraMan.display();
    }

    // ================================================================


    /**
     * Key pressed
     */
    public void keyPressed()
    {
        // forward the event
        codeSkin.keyPressed(key);
    }

    // ================================================================

    // Helpers

}
