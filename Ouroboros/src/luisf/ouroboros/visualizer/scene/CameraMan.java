package luisf.ouroboros.visualizer.scene;

import com.hamoid.VideoExport;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.visualizer.skins.SkinBase;
import processing.core.PApplet;
import processing.core.PGraphics;
import remixlab.proscene.Scene;

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

    private SkinBase skin;

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

    public void setSkin(SkinBase skin) {
        this.skin = skin;
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

        setGridVisualHint(false);
        setAxesVisualHint(false);

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

    public void addFrameToVideo(PGraphics frame) {
        if (captureStarted) {
            videoExport.saveFrame();
        }
    }

    @Override
    public void draw() {
        log.info("Draw");
    }

    /**
     * Draw on the scene's pgraphics
     * Called after Scene.endDraw()
     */
    @Override
    public void proscenium() {

        log.info("Proscenium");
//        beginDraw();

        clearBackground();
        //cameraMan.drawBackground();

        skin.draw(null);

        pg().pushMatrix();
        pg().pushStyle();
        pg().translate(0, 4, 0);
        pg().fill(150, 100);
        pg().noStroke();

        pg().box(600, 2, 1000);
        pg().popStyle();
        pg().popMatrix();


//        endDraw();
        //pg().background(100, 20, 20);
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

    public void clearBackground() {
        pg().clear();
    }


//    public void drawBackground() {
//        if (backgroundGraphics == null) {
//            backgroundGraphics = parent.createGraphics(pg().width, pg().height);
//
//            backgroundGraphics.beginDraw();
//
//            ColorTools.fillLineGradientRGB(new int[]{0xFFFFFF, 0x000000},
//                    new PVector(0, 0),
//                    new PVector(backgroundGraphics.width * 0.8f, backgroundGraphics.height),
//                    backgroundGraphics,
//                    true);
//
////            backgroundGraphics.background(150);
//            backgroundGraphics.beginDraw();
//        }
//
//        pg().image(backgroundGraphics, 0, 0);
//    }


    // ================================================================

    // Helpers

    private void configureVideo(VideoExport video) {
        videoExport.setFrameRate(60);
        videoExport.setDebugging(false);
        //videoExport.setLoadPixels(false); // calls loadpixels
        videoExport.setQuality(90, 128);
    }


}
