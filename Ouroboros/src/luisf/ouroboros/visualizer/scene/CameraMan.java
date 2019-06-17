package luisf.ouroboros.visualizer.scene;

import com.hamoid.VideoExport;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.hmi.InputListennerInterface;
import luisf.ouroboros.hmi.OscStringBuilder;
import luisf.ouroboros.properties.PropertyManager;
import luisf.ouroboros.visualizer.suits.SuitBase;
import processing.core.PApplet;
import processing.core.PGraphics;
import remixlab.proscene.Scene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class CameraMan extends Scene implements InputListennerInterface {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private VideoExport videoExport;

    private Boolean captureStarted = false;
    private Boolean capturing = false;
    private String videoPath = "";
    private File outputFolder;

    private SuitBase skin;

    // ================================================================

    /**
     * Constructor
     *
     * @param p
     * @param graphics
     */
    public CameraMan(PApplet p, PGraphics graphics, File outputFolder) {
        super(p, graphics);
        this.outputFolder = outputFolder;
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
        setRadius(700);

        setGridVisualHint(false);
        setAxesVisualHint(false);

        eyeFrame().setDamping(0.5f);

        camera().setUpVector(0, 1, 0);
        camera().setPosition(0, -50, 100);
        camera().lookAt(0, 0, 0);

        showAll();
    }


    public void startStopCapture(String videoPath) {
        if (!captureStarted) {
            log.info(Handy.f("Starting video capture to " + videoPath));

            this.videoPath = videoPath;
            videoExport = new VideoExport(parent, videoPath);

            configureVideo(videoExport);

            videoExport.startMovie();
            captureStarted = true;
        }

        capturing = !capturing;

        log.info(Handy.f("Video capture is %s", capturing ? "On" : "Off"));
    }

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

    public void addFrameToVideo(PGraphics frame) {
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
    }

    public void keyPressed(int key, int keyCode) {
        if (key == CODED || key == 0) {
            switch (keyCode) {
                case 97: // F1
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");

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
            }
        }

        // normal key captures
        switch (key) {
            case '+':

                break;
        }
    }

    public Boolean isCapturing()
    {
        return capturing;
    }

    // ================================================================

    // InputListennerInterface

    @Override
    public void reactToInput(InputEvent input) {
        if (input.id.equals(OscStringBuilder.build(3, "xy")))
        {
            log.info(Handy.f("XY: %.2f, %.2f", input.getAsXY().x, input.getAsXY().y));
        }
    }

    // ================================================================

    // Helpers

    private void configureVideo(VideoExport video) {
        videoExport.setFrameRate(60);
        videoExport.setDebugging(false);
        //videoExport.setLoadPixels(false); // calls loadpixels
        videoExport.setQuality(props.getInt("visualizer.capture.quality"), 128);
    }
}
