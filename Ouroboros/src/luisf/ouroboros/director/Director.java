package luisf.ouroboros.director;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.generator.Generator;
import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.modelsWriter.ModelsIO;
import luisf.ouroboros.analyzer.CodeAnalyzer;
import processing.core.PApplet;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Director {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private CodeAnalyzer parser;
    private List<ClassModel> classModels;

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

        classModels = new LinkedList<ClassModel>();

        if (parseCode) {
            parseProjectFiles(projectFilesFolder, classModels);
            saveModels(modelsFolder, classModels);
        }

        if (generateGraphics) {
            if (classModels.isEmpty()) {
                classModels = readModels(modelsFolder);
            }
            startGenerator(projectFilesFolder, graphicsFolder, classModels);
        }

        log.info("Got " + classModels.size() + " models");
    }


    // ================================================================

    // Public


    // ================================================================

    // Helpers

    private List<ClassModel> readModels(File modelsFolder) {
        log.info(Handy.f("Reading models..."));

        ModelsIO modelsIo = new ModelsIO(modelsFolder);
//        return modelsIo.loadModels();
        return modelsIo.loadModelsBinary();
    }

    private void parseProjectFiles(File projectFilesFolder, List<ClassModel> models) {
        models.clear();

        parser = new CodeAnalyzer(projectFilesFolder, models);
        parser.parseFiles();
    }

    private void saveModels(File modelsFolder, List<ClassModel> models) {
        ModelsIO modelsIo = new ModelsIO(modelsFolder);
//        modelsIo.saveModels(models);
        modelsIo.saveModelsBinary(models);
    }

    private void startGenerator(File projectFilesFolder, File outputFolder, List<ClassModel> models) {
        PApplet.main(Generator.class.getCanonicalName());
    }
}
