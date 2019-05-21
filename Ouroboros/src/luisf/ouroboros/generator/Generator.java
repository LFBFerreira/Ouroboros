package luisf.ouroboros.generator;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.generator.skins.DrawableInterface;
import luisf.ouroboros.generator.skins.SlicedSkin;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Generator extends PApplet {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public List<ClassModel> models = new LinkedList<>();
    public File graphicsFolder = null;

    private DrawableInterface codeSkin;
    private PGraphics skinGraphics;

    private final int windowHeight = 400;
    private final int windowWidth = 600;

    // ================================================================

    /**
     * Constructor
     * @param graphicsFolder
     * @param models
     */
    public Generator(File graphicsFolder, List<ClassModel> models) {
        this.models = models;
        this.graphicsFolder = graphicsFolder;
    }

    /**
     * Static Launcher
     * @param graphicsFolder
     * @param models
     */
    public static void launchGenerator(File graphicsFolder, List<ClassModel> models)
    {
        Generator generator= new Generator(graphicsFolder, models);

        runSketch(new String[] {""}, generator  );
    }

    // ================================================================

    public void dispose() {
        log.info(String.format("Terminating %s", Generator.class.getSimpleName()));
    }

    public void settings() {
        log.warning("Hey Settings!");
        size(windowWidth, windowHeight, P2D);
    }

    public void setup() {
        log.warning("Hey Setup!");

        skinGraphics = createGraphics(windowWidth, windowHeight);

        codeSkin = new SlicedSkin(models, skinGraphics, this);
    }

    public void draw() {
        clear();
        background(100);

        if(codeSkin != null)
        {
            image(codeSkin.draw(), 0, 0);
        }
    }
}
