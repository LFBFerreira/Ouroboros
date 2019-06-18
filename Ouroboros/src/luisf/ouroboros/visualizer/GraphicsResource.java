package luisf.ouroboros.visualizer;

import processing.core.PApplet;
import processing.core.PGraphics;

public class GraphicsResource {
    protected PApplet parent;
    protected PGraphics graphics;

    public GraphicsResource(PApplet parent, PGraphics graphics) {
        this.parent = parent;
        this.graphics = graphics;
    }
}
