package luisf.ouroboros.properties;

import luisf.ouroboros.common.Handy;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;


import java.io.File;
import java.net.URL;
import java.util.logging.Logger;


/**
 * @author LuisFerreira
 */
public class PropertyManager {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private static File configurationFile;

    private static XMLConfiguration configuration;

    private static FileBasedConfigurationBuilder<XMLConfiguration> builder;
    private static Parameters params;

    private static String configFilePath = "";
    private static URL configFileUrl;

    private static Boolean needsSave = false;

    /**
     * Hidden constructor
     */
    private PropertyManager() {
    }

    // ---------------------------------------------------------------------------

    // region Public methods

    /**
     * Loads the configuration file
     *
     * @return
     */

    public static Boolean loadConfiguration(String configurationFilePath) {

        if (configurationFilePath == null || configurationFilePath.isEmpty()) {
            log.severe("The configuration file path is null or empty");
            return false;
        }

        configurationFile = new File(configurationFilePath);

        if (configurationFile == null) {
            log.severe(Handy.f("Could not load the Configuration file '{}' from Resources", configFilePath));
            return false;
        }

        log.info(Handy.f("Configuration file loaded from '{}'", configurationFile.getAbsolutePath()));

        params = new Parameters();

        builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                .configure(params.xml().setFile(configurationFile).setValidating(false));

        // load the configuration file
        try {
            configuration = builder.getConfiguration();
        } catch (ConfigurationException e) {
            log.severe("An exception occurred while loading the Configuration file");
            log.severe(e.toString());
            return false;
        }

        return true;
    }

    /**
     * Saves the current configuration to a file
     */
    public static void saveConfiguration() {
        log.info(Handy.f("Saving configuration to Resources {}", configFilePath));

        // create the file if it doesn't exist already
        if (configurationFile == null) {
            configurationFile = new File(configFileUrl.getFile());
        }

        // try save
        try {
            builder.save();
        } catch (ConfigurationException e) {
            log.severe("An exception occurred while saving the Configuration file");
            log.severe(e.toString());
        }

        needsSave = false;
    }

    /**
     * Checks if the current configuration needs to be saved to File
     *
     * @return
     */
    public static Boolean needsSave() {
        return needsSave;
    }

    // endregion

    // ---------------------------------------------------------------------------

    // region Properties Getters

    public static int[] getIntArray(String propertyName) {
        String[] stringValues = configuration.getStringArray(propertyName);

        int[] intValues = new int[stringValues.length];
        for (int i = 0; i < stringValues.length; i++) {
            intValues[i] = Integer.parseInt(stringValues[i]);
        }

        return intValues;
    }

    public static String getString(String propertyName) {
        return configuration.getString(propertyName);
    }

    public static char getChar(String propertyName) {
        return configuration.getString(propertyName).charAt(0);
    }

    public static int getInt(String propertyName) {
        return configuration.getInt(propertyName);
    }

    public static float getFloat(String propertyName) {
        return configuration.getFloat(propertyName);
    }

    public static Double getDouble(String propertyName) {
        return configuration.getDouble(propertyName);
    }

    public static Long getLong(String propertyName) {
        return configuration.getLong(propertyName);
    }

    public static Boolean getBoolean(String propertyName) {
        return configuration.getBoolean(propertyName);
    }

    public static Byte getByte(String propertyName) {
        return configuration.getByte(propertyName);
    }

    // endregion

    // region Properties Setters

    public static void set(String name, Object value) {
        needsSave = true;
        configuration.setProperty(name, value);
    }

    public static void add(String name, Object value) {
        needsSave = true;
        configuration.addProperty(name, value);
    }

    // endregion

    // ---------------------------------------------------------------------------

    // region Helpers

    // endregion
}