package luisf.ouroboros.director;

import luisf.ouroboros.analyzer.CodeAnalyzer;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.modelsWriter.ModelsIO;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.timemachine.TimeMachine;
import luisf.ouroboros.visualizer.Visualizer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Director {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private CodeAnalyzer parser;
    private List<ClassModel> classModels;

    private Boolean parseSingleProject;
    private Boolean parseMultipleProjects;
    private Boolean generateGraphics;
    private Boolean checkoutCommits;

    private File projectFilesFolder;
    private File graphicsFolder;
    private File modelsFolder;
    private File checkoutFolder;
    private File appConfigFile;
    private URL checkoutUrl;
    private int numCheckouts;
    private String pathFromOrigin;

    // ================================================================

    public Director(Boolean parseSingleProject,
                    Boolean parseMultipleProjects,
                    Boolean generateGraphics,
                    Boolean checkoutCommits,
                    File projectFilesFolder,
                    File graphicsFolder,
                    File modelsFolder,
                    File appConfigFile,
                    File checkoutFolder) {

        this.parseSingleProject = parseSingleProject;
        this.parseMultipleProjects = parseMultipleProjects;
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

        if (checkoutCommits) {
            checkedOutFolders = checkoutCode(checkoutUrl, numCheckouts, checkoutFolder);
        }

        if (parseSingleProject) {
            try {
                FileUtils.deleteDirectory(modelsFolder);
            } catch (IOException e) {
                log.severe(Handy.f("It was not possible to delete the contents of '%s'",
                        projectFilesFolder.getAbsolutePath()));
                e.printStackTrace();
            }

            // use the folder specified by the user
            parseProjectFiles(projectFilesFolder, classModels);
            saveModels(modelsFolder, classModels);
            log.info("Parsed " + classModels.size() + " classes");

        } else if (parseMultipleProjects) {
            try {
                FileUtils.cleanDirectory(modelsFolder);
            } catch (IOException e) {
                log.severe(Handy.f("It was not possible to delete the contents of '%s'",
                        modelsFolder.getAbsolutePath()));
                e.printStackTrace();
            }

            for (String folderName : getFoldersList(projectFilesFolder)) {
                classModels.clear();

                File modelsSubFolder = Handy.createFolder(new File(modelsFolder, folderName));
                Path projectSubFolder = Paths.get(projectFilesFolder.getPath(), folderName, pathFromOrigin);

                parseProjectFiles(projectSubFolder.toFile(), classModels);
                saveModels(modelsSubFolder, classModels);
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

            startGenerator(graphicsFolder, classModels);
        }

    }

    private void loadProperties() {
        numCheckouts = props.getInt("code.numberCheckouts");

        checkoutUrl = Handy.validateUrl(props.getString("code.gitAddress"));
        if (checkoutUrl == null) {
            log.severe(Handy.f("The checkout URL is invalid"));
            System.exit(0);
        }

        pathFromOrigin = props.getString("code.pathFromOrigin");
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
        parser.parseProject();
    }

    private void saveModels(File modelsFolder, List<ClassModel> models) {
        ModelsIO modelsIo = new ModelsIO(modelsFolder);
//        modelsIo.saveModels(models);
        modelsIo.saveModelsBinary(models);
    }

    private void startGenerator(File graphicsFolder, List<ClassModel> models) {
        Visualizer.launchGenerator(graphicsFolder, models);
    }

    private List<String> getFoldersList(File folder)
    {
        if (folder == null)
        {
            return null;
        }

        String[] directories = folder.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        return Arrays.asList(directories);
    }
}
