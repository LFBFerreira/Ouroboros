package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.visualizer.GraphicsResources;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.List;

public abstract class SkinBase extends GraphicsResources implements DrawableInterface  {
    protected List<ClassModel> models;

    public SkinBase(List<ClassModel> models, PGraphics graphics, PApplet parent) {
        super(parent, graphics);

        this.models = models;
    }

    public abstract void initialize();
}
