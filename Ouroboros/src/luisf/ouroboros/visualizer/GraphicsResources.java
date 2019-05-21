package luisf.ouroboros.visualizer;

import processing.core.PApplet;
import processing.core.PGraphics;

public class GraphicsResources {
    protected PApplet parent;
    protected PGraphics graphics;

    public GraphicsResources(PApplet parent, PGraphics graphics) {
        this.parent = parent;
        this.graphics = graphics;
    }
}
