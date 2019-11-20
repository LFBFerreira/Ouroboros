package luisf.ouroboros.visualizer.suits;

import luisf.interfaces.InputListennerInterface;
import luisf.ouroboros.visualizer.DrawableInterface;
import luisf.ouroboros.visualizer.GraphicsResource;
import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class SuitBase extends GraphicsResource implements DrawableInterface, InputListennerInterface {

    public SuitBase(PGraphics graphics, PApplet parent) {
        super(parent, graphics);
    }

    public abstract void initialize();

    public abstract void keyPressed(int key, int keyCode);
}
