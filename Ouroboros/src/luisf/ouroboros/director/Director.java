package luisf.ouroboros.director;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.generator.Generator;
import luisf.ouroboros.model.ClassModelInterface;
import luisf.ouroboros.parser.CodeParser;
import processing.core.PApplet;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Director {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private CodeParser parser;
    private List<ClassModelInterface> codeModels;

    // ================================================================


    public Director(boolean parseCode, File projectFilesFolder, boolean generateGraphics, File graphicsFolder, File modelsFolder) {

        if (parseCode) {
            try {
                log.info(Handy.f("Parsing code from '%s'", projectFilesFolder.getCanonicalPath()));
                log.info(Handy.f("Saving models to '%s'", modelsFolder.getCanonicalPath()));
            } catch (IOException e) {
                log.severe("An exception occurred while getting the canonical path");
                e.printStackTrace();
            }
        } else {
            log.info(Handy.f("Not parsing code "));
        }

        if (generateGraphics) {
            try {
                log.info(Handy.f("Saving graphics to '%s'", graphicsFolder.getCanonicalPath()));
            } catch (IOException e) {
                log.severe("An exception occurred while getting the canonical path");
                e.printStackTrace();
            }
        } else {
            log.info(Handy.f("Not generating graphics"));
        }

        if (parseCode) {
            startParser(projectFilesFolder);
        }

        if (generateGraphics) {
            startGenerator(projectFilesFolder, graphicsFolder);
        }
    }

    // ================================================================

    // Public


    // ================================================================

    // Static

    public static File validateFolderPath(String path) {
        if (Handy.isNullOrEmpty(path)) {
            return null;
        }

        File folder = new File(path);

        if (folder.exists() && folder.isDirectory()) {
            return folder;
        } else {
            try {
                log.severe(Handy.f("The folder '%s' doesn't exist or its not a directory", folder.getCanonicalPath()));
            } catch (IOException e) {
                log.severe("An exception occurred while getting the canonical path");
                e.printStackTrace();
            }
            return null;
        }
    }


    // ================================================================

    // Helpers

    private void startParser(File projectFilesFolder) {
        codeModels = new LinkedList<ClassModelInterface>();

        parser = new CodeParser(projectFilesFolder, projectFilesFolder, codeModels);

        parser.parseFiles();
    }

    private void startGenerator(File projectFilesFolder, File outputFolder) {
        PApplet.main(Generator.class.getCanonicalName());
    }
}
