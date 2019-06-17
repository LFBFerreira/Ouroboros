package luisf.ouroboros;


import luisf.ouroboros.common.Handy;
import luisf.ouroboros.director.Director;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    // logger configuration
    static {
        String logConfigurationfileName = "logging.properties";

        //method 1
//        URL configFileUrl = Main.class.getResource("/logging.properties");
//        System.setProperty("java.util.logging.config.file", configFileUrl.getPath());

        // method 2
//        String path = Main.class.getClassLoader().getResource("logging.properties").getFile();
//        System.setProperty("java.util.logging.config.file", path);

        //method 3
        InputStream stream = Main.class.getClassLoader().getResourceAsStream(logConfigurationfileName);

        if (stream == null) {
            System.err.println("Could not find the resource'" + logConfigurationfileName + "'");
        }

        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            System.err.println("Could not load the logging configuration from '" + logConfigurationfileName + "'");
            e.printStackTrace();
        }
    }

    // ================================================================

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private static final String defaultAppConfigFileName = "app.config";

    private static final String helpArgument = "h";
    private static final String parseCodeArgument = "p";
    private static final String generateGraphicsArgument = "g";
    private static final String codeFolderArgument = "c";
    private static final String modelsOutputFolderArgument = "m";
    private static final String visualsOutputFolderArgument = "v";
    private static final String appPropertiesFileArgument = "a";

    // ================================================================

    /**
     * Main
     *
     * @param args
     */
    public static void main(String[] args) {

        Map<String, String> argumentsMapping = getArguments(args);

        // validate project files folder
        File projectFilesFolder = Handy.validateFolderPath(argumentsMapping.get(codeFolderArgument));
        if (projectFilesFolder == null) {
            log.severe(Handy.f("The code folder is invalid"));
            //System.exit(0);
        }

        // validate models output folder
        File modelsOutputFolder = Handy.validateFolderPath(argumentsMapping.get(modelsOutputFolderArgument));
        if (modelsOutputFolder == null) {
            log.severe(Handy.f("The models output folder is invalid"));
            //System.exit(0);
        }

        // validate visuals output folder
        File visualsOutputFolder = null;
        if (argumentsMapping.containsKey(generateGraphicsArgument)) {
            visualsOutputFolder = Handy.validateFolderPath(argumentsMapping.get(visualsOutputFolderArgument));
            if (visualsOutputFolder == null) {
                log.severe(Handy.f("The graphics output folder is invalid"));
                System.exit(0);
            }
        }

        File propertiesFile = Handy.validateFilePath(argumentsMapping.get(appPropertiesFileArgument));
        if (propertiesFile == null) {
            if (!Handy.isNullOrEmpty(argumentsMapping.get(appPropertiesFileArgument))) {
                log.severe(Handy.f("It was not possible to load the app configuration file from %s",
                        argumentsMapping.get(appPropertiesFileArgument)));
            }

            String configFile = Main.class.getClassLoader().getResource(defaultAppConfigFileName).getFile();
            log.info(Handy.f("Loading default app configuration file from %s", configFile));
            propertiesFile = Handy.validateFilePath(configFile);

            if (propertiesFile == null) {
                log.severe(Handy.f("Could not load the default app configuration file from %s", configFile));
                System.exit(0);
            }
        }

        Director director = new Director(argumentsMapping.containsKey(parseCodeArgument),
                argumentsMapping.containsKey(generateGraphicsArgument),
                projectFilesFolder,
                visualsOutputFolder,
                modelsOutputFolder,
                propertiesFile);

        log.info("Starting Ouroboros");

        director.start();
    }

    // ================================================================

    // Helpers

    /**
     * Converts the arguments from an array of strings to a map of arguments and values
     *
     * @param args
     * @return
     */
    private static Map<String, String> getArguments(String[] args) {
        Map<String, String> argumentsMapping = new HashMap<String, String>();
        Options argumentOptions = new Options();
        CommandLineParser parser = new DefaultParser();

        argumentOptions.addOption(parseCodeArgument, false, "parse code");
        argumentOptions.addOption(generateGraphicsArgument, false, "generate graphics");
        argumentOptions.addOption(codeFolderArgument, true, "code folder path");
        argumentOptions.addOption(visualsOutputFolderArgument, true, "graphics output folder path");
        argumentOptions.addOption(modelsOutputFolderArgument, true, "models output folder path");
        argumentOptions.addOption(appPropertiesFileArgument, true, "app configuration file path");

        CommandLine commandLineArguments = null;

        try {
            commandLineArguments = parser.parse(argumentOptions, args);
        } catch (UnrecognizedOptionException e) {
            log.severe("There was an unrecognized input parameter");
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("An exception occurred while parsing the command line arguments");
            e.printStackTrace();
        }

        if (args.length == 0 || argumentsMapping.containsKey(helpArgument)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", argumentOptions);
        }

        // populate arguments and their values
        if (commandLineArguments.hasOption(parseCodeArgument)) {
            argumentsMapping.put(parseCodeArgument, "");
        }

        if (commandLineArguments.hasOption(generateGraphicsArgument)) {
            argumentsMapping.put(generateGraphicsArgument, "");
        }

        if (commandLineArguments.hasOption(codeFolderArgument)) {
            argumentsMapping.put(codeFolderArgument, commandLineArguments.getOptionValue(codeFolderArgument));
        }

        if (commandLineArguments.hasOption(visualsOutputFolderArgument)) {
            argumentsMapping.put(visualsOutputFolderArgument, commandLineArguments.getOptionValue(visualsOutputFolderArgument));
        }

        if (commandLineArguments.hasOption(modelsOutputFolderArgument)) {
            argumentsMapping.put(modelsOutputFolderArgument, commandLineArguments.getOptionValue(modelsOutputFolderArgument));
        }

        if (commandLineArguments.hasOption(appPropertiesFileArgument)) {
            argumentsMapping.put(appPropertiesFileArgument, commandLineArguments.getOptionValue(appPropertiesFileArgument));
        }

        return argumentsMapping;
    }

//    private static void testResources() {
//        CodeAnalyzer analyzer = new CodeAnalyzer();
//        System.out.println(analyzer);
//
//        ClassLoader classLoader = new Main().getClass().getClassLoader();
//        File file = analyzer.getFileFromResources("app.config");
//
//        if (file != null) {
//            System.out.println("Got it!");
//        } else {
//            System.out.println("Failed file");
//        }

//        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//        InputStream is = classloader.getResourceAsStream("app.config");
//
//        if (is != null) {
//            System.out.println("Got the stream!");
//        } else {
//            System.out.println("Failed stream");
//        }
//    }
}
