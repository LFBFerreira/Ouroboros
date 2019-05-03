package luisf.ouroboros.generator;

import processing.core.PApplet;

import java.util.logging.Logger;

public class Generator extends PApplet {
    Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public void settings() {

    }

    public void setup() {

    }

    public void dispose() {
        log.info(String.format("Terminating %s", Generator.class.getSimpleName()));
    }

    public void draw() {
    }
}
