package luisf.ouroboros.director;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.generator.Generator;
import luisf.ouroboros.model.CodeModel;
import luisf.ouroboros.model.CodeModelInterface;
import luisf.ouroboros.parser.CodeParser;
import processing.core.PApplet;

import java.io.File;
import java.net.URI;
import java.util.logging.Logger;

public class Director {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private CodeParser parser;
    private CodeModelInterface codeModel;

    // ================================================================


    public Director(File projectFilesFolder, File outputFolder) {
        codeModel = new CodeModel();

        parser = new CodeParser(projectFilesFolder, outputFolder, codeModel);

        parser.parseFiles();
    }

    // ================================================================

    // Public

    public void startParser() {

    }

    public void startGenerator() {
        PApplet.main(Generator.class.getCanonicalName());
    }


    // ================================================================

    // Static

    public static File validateFolderPath(String path) {
        File folder = new File(path);

        if (folder.exists() && folder.isDirectory()) {
            return folder;
        } else {
            log.severe(Handy.f("The folder '%s' doesn't exist or its not a directory", path));
            return null;
        }
    }


    // ================================================================

    // Helpers
}
