package luisf.ouroboros.visualizer.suits;

import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.visualizer.GraphicsResources;
import luisf.ouroboros.visualizer.DrawableInterface;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.List;

public abstract class SuitBase extends GraphicsResources implements DrawableInterface {
    protected List<ClassModel> models;

    public SuitBase(List<ClassModel> models, PGraphics graphics, PApplet parent) {
        super(parent, graphics);

        this.models = models;
    }

    public abstract void initialize();

    public abstract void keyPressed(int key, int keyCode);
}
