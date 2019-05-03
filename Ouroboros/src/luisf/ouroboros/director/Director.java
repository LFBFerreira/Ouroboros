package luisf.ouroboros.director;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.generator.Generator;
import processing.core.PApplet;

import java.io.File;
import java.util.logging.Logger;

public class Director {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    // ================================================================

    public Director(String projectFilesFolder)
    {

    }

    // ================================================================

    // Public

    public void startGenerator()
    {
        PApplet.main(Generator.class.getCanonicalName());
    }


    // ================================================================

    // Static

    public static String processProjectFilesPath(String path) {

        File tmpDir = new File(path);

        if (tmpDir.exists() && tmpDir.isDirectory()) {
            return path.trim();
        }
        else
        {
            log.severe(Handy.f("The project files folder path '%s' doesn't exist or its not a directory", path));
            return null;
        }
    }


    // ================================================================

    // Helpers
}
