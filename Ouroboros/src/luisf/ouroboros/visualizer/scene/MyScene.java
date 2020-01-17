package luisf.ouroboros.visualizer.scene;

import com.hamoid.VideoExport;
import luisf.interfaces.InputEvent;
import luisf.interfaces.InputListennerInterface;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.common.ProjectData;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;
import remixlab.proscene.Scene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class MyScene extends Scene implements InputListennerInterface {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private VideoExport videoExport;

    private Boolean captureStarted = false;
    private Boolean capturing = false;
    private String videoPath = "";
    private File outputFolder;

    private SuitBase suit;

    private PVector cameraPosition = new PVector(0, 0, 0);

    private final String screenshotExtension = ".png";
    private int videoExportFramerate;
    private int videoExportQuality;

    // ================================================================

    /**
     * Constructor
     *
     * @param p
     * @param graphics
     */
    public MyScene(PApplet p, PGraphics graphics, File outputFolder, SuitBase suit) {
        super(p, graphics);
        this.outputFolder = outputFolder;
        this.suit = suit;
    }

    // ================================================================

    /**
     * Dispose
     */
    public void dispose() {
        super.dispose();
        if (captureStarted) {
            endCapture();
        }
    }

    // ================================================================

    // Public

    public void initialize() {
        loadProperties();

        //removeKeyBindings();

        setGridVisualHint(false);
        setAxesVisualHint(false);
        setPathsVisualHint(false);

        eyeFrame().setDamping(0.5f);

        setRadius(2000);
        camera().setUpVector(0, 1, 0);
        setCameraPosition(cameraPosition);
        camera().lookAt(0, 0, 0);

//        showAll();
        //center();
    }

    public void clearScene() {
        pg().clear();
    }

    /**
     *
     * @param videoPath
     */
    public void startStopCapture(String videoPath) {
        if (!captureStarted) {
            log.info(Handy.f("Starting video capture to " + videoPath));

            this.videoPath = videoPath;
//            videoExport = new VideoExport(parent, videoPath, pg());
            videoExport = new VideoExport(parent, videoPath);

            configureVideo(videoExport);

            videoExport.startMovie();
            captureStarted = true;
        }

        capturing = !capturing;

        log.info(Handy.f("Video capture is %s", capturing ? "On" : "Off"));
    }

    /**
     *
     */
    public void endCapture() {
        if (!captureStarted) {
            log.warning("There is no video being recorded");
            return;
        }

        captureStarted = false;
        capturing = false;

        log.info(Handy.f("Saving video to '%s'", videoPath));
        videoExport.endMovie();
    }

    public void addFrameToVideo() {
        if (captureStarted && capturing) {
            videoExport.saveFrame();
        }
    }

    /**
     * Draw on the scene's pgraphics
     * Called after Scene.endDraw()
     */
    @Override
    public void proscenium() {
        suit.draw(pg());
        //drawTitle(pg());
    }


    public void keyPressed(int key, int keyCode) {
        if (key == CODED || key == 0) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");

            switch (keyCode) {
                case 97: // F1
                    formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");

                    try {
                        startStopCapture(Paths.get(outputFolder.getCanonicalPath(),
                                "capture " + now.format(formatter)).toString() + ".mp4");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 98: // F2
                    endCapture();
                    break;
                case 99: // F3
                    takeScreenshot();
                    break;
            }
        } else {

        }
    }

    public Boolean isCapturing() {
        return capturing;
    }

    public void setCameraPosition(PVector position) {
        setCameraPosition((int) position.x, (int) position.y, (int) position.z);
    }

    public void setCameraPosition(int x, int y, int z) {
        cameraPosition = new PVector(x, y, z);
        camera().setPosition(cameraPosition.x, cameraPosition.y, cameraPosition.z);
    }

    // ================================================================

    // InputListennerInterface

    private float angleSensitityY = 10;

    @Override
    public void newEvent(InputEvent input) {
//        if (input.id.equals(OscStringBuilder.build(1, "multixy1", 1))) {
//            log.info(Handy.f("XY: %.2f, %.2f", input.getAsXY().x, input.getAsXY().y));
//
//        }else if (input.id.equals(OscStringBuilder.build(1, "push1"))) {
//            log.info("Fit scene");
//            interpolateToFitScene();
//        }
    }

    // ================================================================

    // Helpers

    private void loadProperties() {
        videoExportFramerate = props.getInt("visualizer.capture.framerate");
        videoExportQuality = props.getInt("visualizer.capture.quality");
    }

    private void takeScreenshot() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
        String filename = Handy.f("screenshot %s%s" ,now.format(formatter), screenshotExtension);
        String filePath = "";

        try {
            filePath = Paths.get(outputFolder.getCanonicalPath(), filename).toString();

            parent.save(filePath);
        } catch (IOException e) {
            log.severe(Handy.f("Could not save the screenshot to '%s'", filePath));
            e.printStackTrace();
        }

        log.severe(Handy.f("Screenshot saved the to '%s'", filePath));
    }

    private void configureVideo(VideoExport video) {
        videoExport.setFrameRate(videoExportFramerate);
        videoExport.setDebugging(false);
        videoExport.setLoadPixels(true);
        videoExport.setQuality(videoExportQuality, 0);
    }
}
