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

    private static final String checkoutCommitsArgument = "k";
    private static final String checkoutDirectoryArgument = "d";

    private static final String parseSingleProjectArgument = "ps";
    private static final String parseMultipleProjectsArgument = "pm";
    private static final String projectFolderArgument = "c";

    private static final String generateGraphicsArgument = "g";
    private static final String visualsFolderArgument = "v";

    private static final String modelsFolderArgument = "m";

    private static final String appPropertiesFileArgument = "a";

    // ================================================================

    public static void main(String[] args) {
        Options argumentOptions = initializeArgumentOptions();

        Map<String, String> argumentsMapping = getArguments(args, argumentOptions);

        if (argumentsMapping.isEmpty())
        {
            System.err.println("\nDon't know what to do without some parameters");
            System.exit(1);
        }

        // checkout commits validation
        File checkoutFolder = null;
        if (argumentsMapping.containsKey(checkoutCommitsArgument))
        {
            checkoutFolder = Handy.validateFolderPath(argumentsMapping.get(checkoutDirectoryArgument));
            if (checkoutFolder == null)
            {
                System.err.println(Handy.f("You need a valid Checkout folder to checkout commits"));
                System.exit(1);
            }
        }

        // parse code validation
        File projectFilesFolder = null;
        File modelsOutputFolder = null;
        if (argumentsMapping.containsKey(parseSingleProjectArgument) ||
                argumentsMapping.containsKey(parseMultipleProjectsArgument))
        {
            projectFilesFolder = Handy.validateFolderPath(argumentsMapping.get(projectFolderArgument));
            if (projectFilesFolder == null)
            {
                System.err.println(Handy.f("You need a valid Checkout folder to checkout commits"));
                System.exit(1);
            }

            modelsOutputFolder = Handy.validateFolderPath(argumentsMapping.get(modelsFolderArgument));
            if (modelsOutputFolder == null)
            {
                System.err.println(Handy.f("The models output folder is invalid"));
                System.exit(1);
            }
        }

        // visuals validation
        File visualsOutputFolder = null;
        if (argumentsMapping.containsKey(generateGraphicsArgument)) {
            visualsOutputFolder = Handy.validateFolderPath(argumentsMapping.get(visualsFolderArgument));
            if (visualsOutputFolder == null) {
                System.err.println(Handy.f("The graphics output folder is invalid"));
            }

            modelsOutputFolder = Handy.validateFolderPath(argumentsMapping.get(modelsFolderArgument));
            if (modelsOutputFolder == null)
            {
                System.err.println(Handy.f("The models output folder is invalid"));
                System.exit(1);
            }
        }

        // app config file validation
        String propertiesFilePath = argumentsMapping.get(appPropertiesFileArgument);
        File propertiesFile = Handy.validateFilePath(propertiesFilePath);
        if (propertiesFile == null) {
            if (!Handy.isNullOrEmpty(propertiesFilePath)) {
                System.err.println(Handy.f("It was not possible to load the app configuration file from %s",
                        argumentsMapping.get(appPropertiesFileArgument)));
            }

            String configFile = Main.class.getClassLoader().getResource(defaultAppConfigFileName).getFile();

            System.out.println(Handy.f("Loading default app configuration file from %s", configFile));

            propertiesFile = Handy.validateFilePath(configFile);
            if (propertiesFile == null) {
                System.err.println(Handy.f("Could not load the default app configuration file from %s", configFile));
                System.exit(1);
            }
        }

        Director director = new Director(argumentsMapping.containsKey(parseSingleProjectArgument),
                argumentsMapping.containsKey(parseMultipleProjectsArgument),
                argumentsMapping.containsKey(generateGraphicsArgument),
                argumentsMapping.containsKey(checkoutCommitsArgument),
                projectFilesFolder,
                visualsOutputFolder,
                modelsOutputFolder,
                propertiesFile,
                checkoutFolder);

        log.info("Starting Ouroboros");

        director.start();
    }

    /**
     * Main
     *
     * @param args
     */
//    public static void main2(String[] args) {
//
//        Options argumentOptions = initializeArgumentOptions();
//
//        Map<String, String> argumentsMapping = getArguments(args, argumentOptions);
//
//        if (argumentsMapping.isEmpty())
//        {
//            System.out.println("\nParameters expected");
//            System.exit(1);
//        }
//
//        // validate checkout folder
//        File checkoutFolder = Handy.validateFolderPath(argumentsMapping.get(checkoutDirectoryArgument));
//        if (argumentsMapping.containsKey(checkoutCommitsArgument) && checkoutFolder == null) {
//            log.severe(Handy.f("You need a valid Checkout folder to checkout commits"));
//            //System.exit(0);
//        }
//
//        // validate project files folder
//        File projectFilesFolder = Handy.validateFolderPath(argumentsMapping.get(projectFolderArgument));
//        if (argumentsMapping.containsKey(parseSingleProjectArgument) &&
//                !argumentsMapping.containsKey(checkoutCommitsArgument) &&
//                projectFilesFolder == null) {
//            log.severe(Handy.f("You need a valid Code folder to parse"));
//            //System.exit(0);
//        }
//
//        // validate models output folder
//        File modelsOutputFolder = Handy.validateFolderPath(argumentsMapping.get(modelsFolderArgument));
//        if ((argumentsMapping.containsKey(parseSingleProjectArgument) || argumentsMapping.containsKey(generateGraphicsArgument)) &&
//                modelsOutputFolder == null) {
//            log.severe(Handy.f("The models output folder is invalid"));
//            //System.exit(0);
//        }
//
//        // validate visuals output folder
//        File visualsOutputFolder = null;
//        if (argumentsMapping.containsKey(generateGraphicsArgument)) {
//            visualsOutputFolder = Handy.validateFolderPath(argumentsMapping.get(visualsFolderArgument));
//            if (visualsOutputFolder == null) {
//                log.severe(Handy.f("The graphics output folder is invalid"));
//                System.exit(0);
//            }
//        }
//
//        // load the selected app config file, or loads a default it none is specified
//        File propertiesFile = Handy.validateFilePath(argumentsMapping.get(appPropertiesFileArgument));
//        if (propertiesFile == null) {
//            if (!Handy.isNullOrEmpty(argumentsMapping.get(appPropertiesFileArgument))) {
//                log.severe(Handy.f("It was not possible to load the app configuration file from %s",
//                        argumentsMapping.get(appPropertiesFileArgument)));
//            }
//
//            String configFile = Main.class.getClassLoader().getResource(defaultAppConfigFileName).getFile();
//            log.info(Handy.f("Loading default app configuration file from %s", configFile));
//            propertiesFile = Handy.validateFilePath(configFile);
//
//            if (propertiesFile == null) {
//                log.severe(Handy.f("Could not load the default app configuration file from %s", configFile));
//                System.exit(0);
//            }
//        }
//
//
//        Director director = new Director(argumentsMapping.containsKey(parseSingleProjectArgument),
//                argumentsMapping.containsKey(generateGraphicsArgument),
//                argumentsMapping.containsKey(checkoutCommitsArgument),
//                projectFilesFolder,
//                visualsOutputFolder,
//                modelsOutputFolder,
//                propertiesFile,
//                checkoutFolder);
//
//        log.info("Starting Ouroboros");
//
//        director.start();
//    }

    // ================================================================

    // Helpers

    /**
     * Converts the arguments from an array of strings to a map of arguments and values
     *
     * @param args
     * @return
     */
    private static Map<String, String> getArguments(String[] args, Options argumentOptions) {

        Map<String, String> argumentsMapping = new HashMap<String, String>();
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLineArguments = null;

        try {
            commandLineArguments = parser.parse(argumentOptions, args);
        } catch (UnrecognizedOptionException e) {
            System.err.println("One of the arguments you used is incorrect");
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("An exception occurred while parsing the command line arguments");
            e.printStackTrace();
        }

        for (Option option: commandLineArguments.getOptions()) {
            argumentsMapping.put(option.getOpt(), option.getValue());
        }

        if (args.length == 0 || argumentsMapping.containsKey(helpArgument)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("", argumentOptions);
        }

        return argumentsMapping;
    }

    private static Options initializeArgumentOptions() {
        Options argumentOptions = new Options();

        argumentOptions.addOption(parseSingleProjectArgument, false, "parse single project");
        argumentOptions.addOption(parseMultipleProjectsArgument, false, "parse multiple projects");
        argumentOptions.addOption(generateGraphicsArgument, false, "generate graphics");
        argumentOptions.addOption(checkoutCommitsArgument, false, "checkout commits");

        argumentOptions.addOption(checkoutDirectoryArgument, true, "code folder");
        argumentOptions.addOption(projectFolderArgument, true, "code folder");
        argumentOptions.addOption(visualsFolderArgument, true, "graphics output folder");
        argumentOptions.addOption(modelsFolderArgument, true, "models output folder");
        argumentOptions.addOption(appPropertiesFileArgument, true, "app configuration file");

        return argumentOptions;
    }
}
