package luisf.ouroboros.properties;

import luisf.ouroboros.common.Handy;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;


/**
 * @author LuisFerreira
 */
public class PropertyManager {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private static PropertyManager instance = null;

    private File configurationFile;

    private XMLConfiguration configuration;

    private FileBasedConfigurationBuilder<XMLConfiguration> builder;
    private Parameters params;

    private String configFilePath = "";
    private URL configFileUrl;

    private Boolean needsSave = false;

    // ================================================================

    /**
     * Hidden constructor
     */
    private PropertyManager() {
    }

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    // ================================================================

    // region Public methods

    /**
     * Loads the configuration file
     *
     * @return
     */

    public Boolean loadConfiguration(String configurationFilePath) {

        if (configurationFilePath == null || configurationFilePath.isEmpty()) {
            log.severe("The configuration file path is null or empty");
            return false;
        }

        configurationFile = new File(configurationFilePath);

        if (configurationFile == null) {
            log.severe(Handy.f("Could not load the Configuration file '%s' from Resources", configFilePath));
            return false;
        }

        log.info(Handy.f("Configuration file loaded from '%s'", configurationFile.getAbsolutePath()));

        params = new Parameters();

        builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                .configure(params.xml().setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                        .setFile(configurationFile).setValidating(false));

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
    public void saveConfiguration() {
        log.info(Handy.f("Saving configuration to Resources %s", configFilePath));

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
    public Boolean needsSave() {
        return needsSave;
    }

    // endregion

    // ---------------------------------------------------------------------------

    // region Properties Getters


//    public int[] getIntArray(String propertyName) {
//        String[] stringValues = configuration.getStringArray(propertyName);
//
//        int[] intValues = new int[stringValues.length];
//        for (int i = 0; i < stringValues.length; i++) {
//            intValues[i] = Integer.parseInt(stringValues[i]);
//        }
//
//        return intValues;
//    }


    public List<Integer> getIntList(String propertyName)
    {
        return configuration.getList(Integer.class, propertyName);
    }

    public int[] getIntArray(String propertyName)
    {
        return getIntList(propertyName).stream().mapToInt(Integer::intValue).toArray();
    }

    public String getString(String propertyName) {
        return configuration.getString(propertyName);
    }

    public char getChar(String propertyName) {
        return configuration.getString(propertyName).charAt(0);
    }

    public int getInt(String propertyName) {
        return configuration.getInt(propertyName);
    }

    public float getFloat(String propertyName) {
        return configuration.getFloat(propertyName);
    }

    public Double getDouble(String propertyName) {
        return configuration.getDouble(propertyName);
    }

    public Long getLong(String propertyName) {
        return configuration.getLong(propertyName);
    }

    public Boolean getBoolean(String propertyName) {
        return configuration.getBoolean(propertyName);
    }

    public Byte getByte(String propertyName) {
        return configuration.getByte(propertyName);
    }

    public List<String> getStringList(String propertyName)
    {
        return configuration.getList(String.class, propertyName);
    }


    // ---------------------------------------------------------------------------

    // region Properties Setters

    public void set(String name, Object value) {
        needsSave = true;
        configuration.setProperty(name, value);
    }

    public void add(String name, Object value) {
        needsSave = true;
        configuration.addProperty(name, value);
    }


    // ---------------------------------------------------------------------------

    // region Helpers


}