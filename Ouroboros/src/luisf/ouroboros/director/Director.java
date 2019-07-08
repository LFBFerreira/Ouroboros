package luisf.ouroboros.director;

import luisf.ouroboros.analyzer.CodeAnalyzer;
import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.modelsWriter.ModelsIO;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.timemachine.TimeMachine;
import luisf.ouroboros.visualizer.Visualizer;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Director {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private CodeAnalyzer parser;
    private List<ClassModel> classModels;

    private Boolean parseCode;
    private Boolean generateGraphics;
    private Boolean checkoutCommits;

    private File projectFilesFolder;
    private File graphicsFolder;
    private File modelsFolder;
    private File checkoutFolder;
    private File appConfigFile;
    private URL checkoutUrl;
    private int numCheckouts;

    // ================================================================

    public Director(Boolean parseCode,
                    Boolean generateGraphics,
                    Boolean checkoutCommits,
                    File projectFilesFolder,
                    File graphicsFolder,
                    File modelsFolder,
                    File appConfigFile,
                    File checkoutFolder) {

        this.parseCode = parseCode;
        this.generateGraphics = generateGraphics;
        this.checkoutCommits = checkoutCommits;

        this.projectFilesFolder = projectFilesFolder;
        this.graphicsFolder = graphicsFolder;
        this.modelsFolder = modelsFolder;
        this.appConfigFile = appConfigFile;
        this.checkoutFolder = checkoutFolder;
    }


    public void start() {

        // load configuration file
        if (props.loadConfiguration(appConfigFile.getPath()) == false) {
            log.severe(Handy.f("It was not possible to load the configuration file from %s", appConfigFile.getPath()));
            System.exit(0);
        }

        loadProperties();

        List<File> checkedOutFolders = null;
        classModels = new LinkedList<ClassModel>();

        if (checkoutCommits)
        {
            checkedOutFolders = checkoutCode(checkoutUrl, numCheckouts, checkoutFolder);
        }

        if (parseCode) {
            if (projectFilesFolder == null) {
                // use the checked out folders
                for (File projectFolder : checkedOutFolders) {
                    File modelsSubFolder = Handy.createFolder(new File(modelsFolder, projectFolder.getName()));

                    parseProjectFiles(projectFolder, classModels);
                    saveModels(modelsSubFolder, classModels);
                    log.info("Parsed " + classModels.size() + " classes");
                }
            }
            else
            {
                // use the folder specified by the user
                parseProjectFiles(projectFilesFolder, classModels);
                saveModels(modelsFolder, classModels);
                log.info("Parsed " + classModels.size() + " classes");
            }
        }

        if (generateGraphics) {
            try {
                log.info(Handy.f("Saving graphics to '%s'", graphicsFolder.getCanonicalPath()));
            } catch (IOException e) {
                log.severe("An exception occurred while getting the canonical path");
                e.printStackTrace();
            }

            if (classModels.isEmpty()) {
                classModels = readModels(modelsFolder);
            }

            startGenerator(projectFilesFolder, graphicsFolder, classModels);
        }

    }

    private void loadProperties() {
        numCheckouts = props.getInt("code.numberCheckouts");

        checkoutUrl = Handy.validateUrl(props.getString("code.gitAddress"));
        if (checkoutUrl == null) {
            log.severe(Handy.f("The checkout URL is invalid"));
            System.exit(0);
        }
    }


    // ================================================================

    // Helpers

    private List<File> checkoutCode(URL checkoutUrl, int numCheckouts, File checkoutFolder) {
        log.info("Getting commits...");
        TimeMachine timeMachine = new TimeMachine(checkoutUrl, checkoutFolder, numCheckouts);
        return timeMachine.checkout();
    }

    private List<ClassModel> readModels(File modelsFolder) {
        log.info(Handy.f("Reading models..."));

        ModelsIO modelsIo = new ModelsIO(modelsFolder);
//        return modelsIo.loadModels();
        return modelsIo.loadModelsBinary();
    }

    private void parseProjectFiles(File projectFilesFolder, List<ClassModel> models) {
        try {
            log.info(Handy.f("Parsing code from '%s'", projectFilesFolder.getCanonicalPath()));
            log.info(Handy.f("Saving models to '%s'", modelsFolder.getCanonicalPath()));
        } catch (IOException e) {
            log.severe("An exception occurred while getting the canonical path");
            e.printStackTrace();
        }

        models.clear();

        parser = new CodeAnalyzer(projectFilesFolder, models);
        parser.parseFiles();
    }

    private void saveModels(File modelsFolder, List<ClassModel> models) {
        ModelsIO modelsIo = new ModelsIO(modelsFolder);
//        modelsIo.saveModels(models);
        modelsIo.saveModelsBinary(models);
    }

    private void startGenerator(File projectFilesFolder, File graphicsFolder, List<ClassModel> models) {
        Visualizer.launchGenerator(graphicsFolder, models);
    }
}
