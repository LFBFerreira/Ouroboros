package luisf.ouroboros.generator.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.List;

public abstract class SkinBase implements DrawableInterface  {
    List<ClassModel> models;
    PApplet parent;
    PGraphics graphics;

    public SkinBase(List<ClassModel> models, PGraphics graphics, PApplet parent) {
        this.models = models;
        this.parent = parent;
        this.graphics = graphics;
    }
}
