package luisf.ouroboros.visualizer.scene;

import luisf.ouroboros.common.Handy;
import processing.core.PApplet;
import processing.core.PGraphics;
import remixlab.proscene.Scene;
import com.hamoid.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class CameraMan extends Scene {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private VideoExport videoExport;

    private Boolean captureStarted = false;
    private Boolean capturing = false;
    private String videoPath = "";
    private File outputFolder;

    // ================================================================


    /**
     * Constructor
     *
     * @param p
     * @param canvas
     */
    public CameraMan(PApplet p, PGraphics canvas, File outputFolder) {
        super(p, canvas);
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

    public void init() {
        setRadius(700);

        setGridVisualHint(true);
        setAxesVisualHint(true);

        eyeFrame().setDamping(0.5f);

        camera().setUpVector(0, 1, 0);
        camera().setPosition(0, -10, 100);
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

    public void newCaptureFrame(PGraphics frame) {
        if (captureStarted) {
            videoExport.saveFrame();
        }
    }

    /**
     * Draw on the scene's pgraphics
     * Called after Scene.endDraw()
     */
    @Override
    public void proscenium() {
        pg().lights();

//        beginScreenDrawing();
//        pg().stroke(255);
//        pg().line(60, 10, 60, 110);
//        pg().line(10, 60, 110, 60);
//        endScreenDrawing();
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


    // ================================================================

    // Helpers

    private void configureVideo(VideoExport video)
    {
        videoExport.setFrameRate(60);
        videoExport.setDebugging(false);
        //videoExport.setLoadPixels(false); // calls loadpixels
        videoExport.setQuality(90, 128);
    }


}
