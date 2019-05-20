package luisf.ouroboros.generator;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.common.Handy;
import processing.core.PApplet;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Generator extends PApplet {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public List<ClassModel> models = new LinkedList<>();
    public File graphicsFolder = null;

    // ================================================================

    /**
     *
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
    public static void launchProcessing(File graphicsFolder, List<ClassModel> models)
    {
        Generator generator= new Generator(graphicsFolder, models);
        runSketch(new String[] {""}, generator  );
    }

    public void settings() {
        log.warning("Hey Settings!");
    }

    public void setup() {
        log.warning("Hey Setup!");

        log.info(Handy.f("Outputing to %s", graphicsFolder));
    }

    public void dispose() {
        log.info(String.format("Terminating %s", Generator.class.getSimpleName()));
    }

    public void draw() {
    }
}
