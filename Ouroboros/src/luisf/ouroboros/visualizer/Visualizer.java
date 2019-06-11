package luisf.ouroboros.visualizer;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.common.colortools.ColorTools;
import luisf.ouroboros.visualizer.scene.CameraMan;
import luisf.ouroboros.visualizer.skins.SkinBase;
import luisf.ouroboros.visualizer.skins.SlicedSkin;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

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

    private PGraphics backgroundGraphics;


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

//        backgroundGraphics = createGraphics(g.width, g.height);
//        backgroundGraphics.beginDraw();
//        backgroundGraphics.background(100, 20, 20);
//        backgroundGraphics.endDraw();
//


        cameraMan = new CameraMan(this, skinGraphics, graphicsFolder);
        cameraMan.init();

        codeSkin = new SlicedSkin(models, cameraMan.pg(), this);
        codeSkin.initialize();
        cameraMan.setSkin(codeSkin);
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
        //clear();

        background(150);

        cameraMan.beginDraw();
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


    private void setGradient(int x, int y, float w, float h, int c1, int c2, boolean vertical, PGraphics graphics) {

        graphics.noFill();

        if (vertical) {  // Top to bottom gradient
            for (int i = y; i <= y + h; i++) {
                float inter = map(i, y, y + h, 0, 1);
                int c = lerpColor(c1, c2, inter);
                stroke(c);
                line(x, i, x + w, i);
            }
        } else {  // Left to right gradient
            for (int i = x; i <= x + w; i++) {
                float inter = map(i, x, x + w, 0, 1);
                int c = lerpColor(c1, c2, inter);
                stroke(c);
                line(i, y, i, y + h);
            }
        }
    }
}
