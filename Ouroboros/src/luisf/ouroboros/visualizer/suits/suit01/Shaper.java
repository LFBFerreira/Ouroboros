package luisf.ouroboros.visualizer.suits.suit01;

import processing.core.PApplet;
import processing.core.PShape;

import static processing.core.PConstants.CLOSE;

public class Shaper {

    public static PShape createBox(float width, float height, float depth, PApplet parent)
    {
        PShape shape = parent.createShape(parent.BOX, width, height, depth);
        return shape;
    }
}
