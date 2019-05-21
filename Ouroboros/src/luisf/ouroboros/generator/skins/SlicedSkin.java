package luisf.ouroboros.generator.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.List;
import java.util.logging.Logger;

public class SlicedSkin extends SkinBase {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());


    // ================================================================

    public SlicedSkin(List<ClassModel> models, PGraphics graphics, PApplet parent) {
        super(models, graphics, parent);
    }

    // ================================================================

    // DrawableInterface Interface
    @Override
    public PGraphics draw() {
        graphics.beginDraw();

        graphics.endDraw();

        return graphics;
    }
}
