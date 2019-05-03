package luisf.ouroboros;

import luisf.ouroboros.codeParser.CodeParser;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.director.Director;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

    public static void main(String[] args) throws IOException, IllegalArgumentException {

        if (args.length < 1) {
            log.severe("No arguments found. Argument 1: path to project files folder");
            System.exit(0);
        }

        // parse project files folder
        String projectFilesFolder = Director.processProjectFilesPath(args[0]);
        if (Handy.isNullOrEmpty(projectFilesFolder)) {
            log.severe(Handy.f("The project files folder is invalid"));
            System.exit(0);
        }

        log.info("Starting Ouroboros");

        Director director = new Director(projectFilesFolder);

        director.startGenerator();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================================================================

    private static void testResources() {
        CodeParser parser = new CodeParser();
        System.out.println(parser);

        ClassLoader classLoader = new Main().getClass().getClassLoader();
        File file = parser.getFileFromResources("configuration.xml");

        if (file != null) {
            System.out.println("Got it!");
        } else {
            System.out.println("Failed file");
        }

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("configuration.xml");

        if (is != null) {
            System.out.println("Got the stream!");
        } else {
            System.out.println("Failed stream");
        }
    }
}
