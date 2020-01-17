package luisf.ouroboros.director;

import luisf.ouroboros.analyzer.CodeAnalyzer;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.modelsWriter.ModelsIO;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.timemachine.TimeMachine;
import luisf.ouroboros.visualizer.Visualizer;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private int maxLoadedProjects;
    private int numCheckouts;
    private String pathFromOrigin;

    private final String metadataFileExtension = ".json";
    private String metadataIdFieldName = "";
    private String metadataCommitDateFieldName = "";
    private String metadataShortMessageFieldName = "";
    private String metadataFullMessageFieldName = "";
    private String metadataDateFormat = "";

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
        List<ProjectData> projects = new LinkedList<ProjectData>();

        if (checkoutCommits) {
            checkedOutFolders = checkoutCode(checkoutUrl, numCheckouts, checkoutFolder);
        }

        if (parseSingleProject) {
            try {
                FileUtils.cleanDirectory(modelsFolder);
            } catch (IOException e) {
                log.severe(Handy.f("It was not possible to delete the contents of '%s'",
                        projectFilesFolder.getAbsolutePath()));
                e.printStackTrace();
            }

            File projectMetadataFile = new File(projectFilesFolder.getParent(),
                    projectFilesFolder.getName() + metadataFileExtension);

            // parse the project
            ProjectData projectData = parseProject(projectFilesFolder, projectMetadataFile);

            // save project data
            saveProjectData(projectData, modelsFolder);

            projects.add(projectData);

            //log.info("Parsed " + metadata.classModels.size() + " classes");

        } else if (parseMultipleProjects) {
            try {
                FileUtils.cleanDirectory(modelsFolder);
            } catch (IOException e) {
                log.severe(Handy.f("It was not possible to delete the contents of '%s'",
                        modelsFolder.getAbsolutePath()));
                e.printStackTrace();
            }

            for (String folderName : getFoldersList(projectFilesFolder)) {

                File modelsSubFolder = Handy.createFolder(new File(modelsFolder, folderName));
                Path projectSubFolder = Paths.get(projectFilesFolder.getPath(), folderName, pathFromOrigin);
                File projectMetadataFile = new File(projectFilesFolder, folderName + metadataFileExtension);

                // parse the project
                ProjectData projectData = parseProject(projectSubFolder.toFile(), projectMetadataFile);

                // save project data
                saveProjectData(projectData, modelsSubFolder);

                projects.add(projectData);
                //log.info("Parsed " + metadata.classModels.size() + " classes");
            }
        }

        if (generateGraphics) {
            try {
                log.info(Handy.f("Saving graphics to '%s'", graphicsFolder.getCanonicalPath()));
            } catch (IOException e) {
                log.severe("An exception occurred while getting the canonical path");
                e.printStackTrace();
            }

            projects = loadProjects(modelsFolder, maxLoadedProjects);

            if (projects.isEmpty()) {
                log.severe("No models were parsed");
            } else {
                log.info("Starting generator");
                startGenerator(graphicsFolder, projects, !parseMultipleProjects);
            }
        }
    }


    // ================================================================

    // Helpers

    private void loadProperties() {
        checkoutUrl = Handy.validateUrl(props.getString("code.gitAddress"));
        if (checkoutUrl == null) {
            log.severe(Handy.f("The checkout URL is invalid"));
            System.exit(0);
        }

        numCheckouts = props.getInt("code.numberCheckouts");
        pathFromOrigin = props.getString("code.pathFromOrigin");
        metadataIdFieldName = props.getString("metadata.idFieldName");
        metadataCommitDateFieldName = props.getString("metadata.dateFieldName");
        metadataShortMessageFieldName = props.getString("metadata.shortMessageFieldName");
        metadataFullMessageFieldName = props.getString("metadata.fullMessageFieldName");
        metadataDateFormat = props.getString("metadata.dateFormat");
        maxLoadedProjects = props.getInt("visualizer.maxLoadedProjects");
    }

    private List<ProjectData> loadProjects(File modelsFolder) {
        return loadProjects(modelsFolder, 0);
    }

    private List<ProjectData> loadProjects(File modelsFolder, int maxLoadedProjects) {
        List<ProjectData> projects = new LinkedList<>();
        List<File> projectFiles = new LinkedList<>();

        // collect all project folders
        for (File f : modelsFolder.listFiles()) {
            // only work on directories
            if (!f.isDirectory()) {
                continue;
            }

            projectFiles.add(f);
        }

        // load projects, limited by maxLoadedProjects
        for (int i = 0; i < projectFiles.size(); i += projectFiles.size() / maxLoadedProjects) {
            File f = projectFiles.get(i);
            ProjectData projectData = parseProjectMetadata(new File(f, f.getName() + metadataFileExtension));
            projectData.addModels(readModels(f));
            projects.add(projectData);
        }

        return projects;
    }

    private void startGenerator(File graphicsFolder, List<ProjectData> projects, Boolean parseSingleProjects) {
        Visualizer.launchGenerator(graphicsFolder, projects, parseSingleProjects);
    }

    private List<File> checkoutCode(URL checkoutUrl, int numCheckouts, File checkoutFolder) {
        log.info("Getting commits...");
        TimeMachine timeMachine = new TimeMachine(checkoutUrl, checkoutFolder, numCheckouts);
        return timeMachine.checkout();
    }

    private List<ClassModel> readModels(File modelsFolder) {
        log.info(Handy.f("Reading models from %s", modelsFolder.getPath()));

        ModelsIO modelsIo = new ModelsIO(modelsFolder);
//        return modelsIo.loadModels();
        return modelsIo.loadModelsBinary();
    }

    private ProjectData parseProject(File projectFilesFolder, File projectMetadataFile) {
        ProjectData projectData;
        List<ClassModel> models = new LinkedList<>();

        try {
            log.info(Handy.f("Parsing code from '%s'", projectFilesFolder.getCanonicalPath()));
            log.info(Handy.f("Saving models to '%s'", modelsFolder.getCanonicalPath()));
        } catch (IOException e) {
            log.severe("An exception occurred while getting the canonical path");
            e.printStackTrace();
        }

        // parse metadata
        projectData = parseProjectMetadata(projectMetadataFile);

        // parse code
        parser = new CodeAnalyzer(projectFilesFolder, models);
        parser.parseProject();

        // add models to metadata
        projectData.addModels(models);

        return projectData;
    }

    private ProjectData parseProjectMetadata(File metadataFilePath) {
        ProjectData metadata = null;

        String id = "";
        String shortMessage = "";
        String fullMessage = "";
        LocalDateTime commitDate;

        Date tempDate;
        String tempDateText;
        TimeZone tempTimeZone;

        if (!metadataFilePath.exists()) {
            log.severe(Handy.f("The project metadata could not be loaded from '%s'", metadataFilePath.getPath()));
            return null;
        }

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(metadataFilePath)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            tempDateText = (String) jsonObject.get(metadataCommitDateFieldName);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(metadataDateFormat);

            // fill fields
            id = (String) jsonObject.get(metadataIdFieldName);
            shortMessage = (String) jsonObject.get(metadataIdFieldName);
            fullMessage = (String) jsonObject.get(metadataIdFieldName);
            commitDate = LocalDateTime.parse(tempDateText, formatter);

            // create metadata
            metadata = new ProjectData(id, commitDate, shortMessage, fullMessage);

        } catch (IOException e) {
            log.severe(Handy.f("Unable to parse the content of the metadata file at '%s'", metadataFilePath.getPath()));
            e.printStackTrace();
        } catch (ParseException e) {
            log.severe(Handy.f("Unable to parse the content of the metadata file at '%s'", metadataFilePath.getPath()));
            e.printStackTrace();
        }

        return metadata;
    }

    private void saveProjectData(ProjectData data, File modelsFolder) {
        ModelsIO modelsIo = new ModelsIO(modelsFolder);

        // save models
        modelsIo.saveModelsBinary(data.classModels);

        saveCommitMetadata(data, new File(modelsFolder, data.id + metadataFileExtension));
    }

    private void saveCommitMetadata(ProjectData data, File destinationFile) {
        JSONObject jsonObj = new JSONObject();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(metadataDateFormat);

        // fill object
        jsonObj.put(metadataIdFieldName, data.id);
        jsonObj.put(metadataShortMessageFieldName, data.shortMessage);
        jsonObj.put(metadataFullMessageFieldName, data.fullMessage);
        jsonObj.put(metadataCommitDateFieldName, data.commitDate.format(formatter));

        // write json object to file
        try (FileWriter file = new FileWriter(destinationFile)) {
            file.write(jsonObj.toJSONString());
            log.info(Handy.f("Metadata saved to %s", destinationFile.getPath()));
        } catch (IOException e) {
            log.severe(Handy.f("It was not possible to save the commits metadata to %s",
                    Paths.get(destinationFile.getPath())));
            e.printStackTrace();
        }
    }

    private List<String> getFoldersList(File folder) {
        if (folder == null) {
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
