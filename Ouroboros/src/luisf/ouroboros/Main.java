package luisf.ouroboros;


import luisf.ouroboros.common.Handy;
import luisf.ouroboros.director.Director;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.*;

public class Main {

    // logger configuration
    static {
        String fileName = "logging.properties";

        //method 1
//        URL configFileUrl = Main.class.getResource("/logging.properties");
//        System.setProperty("java.util.logging.config.file", configFileUrl.getPath());

        // method 2
//        String path = Main.class.getClassLoader().getResource("logging.properties").getFile();
//        System.setProperty("java.util.logging.config.file", path);

        //method 3
        InputStream stream = Main.class.getClassLoader().getResourceAsStream(fileName);

        if (stream == null) {
            System.err.println("Could not find the resource'" + fileName + "'");
        }

        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            System.err.println("Could not load the logging configuration from '" + fileName + "'");
            e.printStackTrace();
        }
    }

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private static final String helpArgument = "h";
    private static final String parseCodeArgument = "p";
    private static final String generateGraphicsArgument = "g";
    private static final String codeFolderArgument = "c";
    private static final String outputFolderArgument = "o";

    public static void main(String[] args) throws IOException, IllegalArgumentException {

        Map<String, String> argumentsMapping = getArguments(args);

        // parse project files folder
        File projectFilesFolder = Director.validateFolderPath(argumentsMapping.get(codeFolderArgument));
        if (projectFilesFolder == null) {
            log.severe(Handy.f("The project files folder is invalid"));
            System.exit(0);
        }

        File outputFolder = Director.validateFolderPath(argumentsMapping.get(outputFolderArgument));
        if (outputFolder == null) {
            log.severe(Handy.f("The output folder is invalid"));
            System.exit(0);
        }

        log.info("Starting Ouroboros");

        Director director = new Director(argumentsMapping.containsKey(parseCodeArgument), projectFilesFolder,
                argumentsMapping.containsKey(generateGraphicsArgument), outputFolder
        );
    }

    // ================================================================

    private static Map<String, String> getArguments(String[] args) {
        Map<String, String> argumentsMapping = new HashMap<String, String>();
        Options argumentOptions = new Options();
        CommandLineParser parser = new DefaultParser();

        argumentOptions.addOption(parseCodeArgument, false, "parse code");
        argumentOptions.addOption(generateGraphicsArgument, false, "generate graphics");
        argumentOptions.addOption(codeFolderArgument, true, "code folder path");
        argumentOptions.addOption(outputFolderArgument, true, "output folder path");

        CommandLine commandLineArguments = null;

        try {
            commandLineArguments = parser.parse(argumentOptions, args);
        } catch (ParseException e) {
            System.err.println("An exception occurred while parsing the command line arguments");
            e.printStackTrace();
        }

        if (args.length == 0 || argumentsMapping.containsKey(helpArgument)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "ant", argumentOptions);
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
        if (commandLineArguments.hasOption(outputFolderArgument)) {
            argumentsMapping.put(outputFolderArgument, commandLineArguments.getOptionValue(outputFolderArgument));
        }

        return argumentsMapping;
    }

    private static void testResources() {
//        CodeParser parser = new CodeParser();
//        System.out.println(parser);
//
//        ClassLoader classLoader = new Main().getClass().getClassLoader();
//        File file = parser.getFileFromResources("configuration.xml");
//
//        if (file != null) {
//            System.out.println("Got it!");
//        } else {
//            System.out.println("Failed file");
//        }

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("configuration.xml");

        if (is != null) {
            System.out.println("Got the stream!");
        } else {
            System.out.println("Failed stream");
        }
    }
}
