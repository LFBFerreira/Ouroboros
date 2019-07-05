package luisf.ouroboros.timemachine;

import luisf.ouroboros.properties.PropertyManager;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

public class TimeMachine {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();


    public void TimeMachine(URL repoUrl, int numCheckouts, File saveFolder)
    {

    }
}
