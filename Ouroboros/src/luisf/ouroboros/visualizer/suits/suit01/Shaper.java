package luisf.ouroboros.visualizer.suits.suit01;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;

public class Shaper {

    public static PShape createBox(float width, float height, float depth, PApplet parent) {
        PShape shape = parent.createShape(parent.BOX, width, height, depth);
        return shape;
    }

    public static PShape createBoxVertex(float width, float height, float depth, PApplet parent) {
        PShape shape = new PShape();

        shape.beginShape(PConstants.QUADS);

        shape.vertex(-1, 1, 1);
        shape.vertex(1, 1, 1);
        shape.vertex(1, -1, 1);
        shape.vertex(-1, -1, 1);

        shape.vertex(1, 1, 1);
        shape.vertex(1, 1, -1);
        shape.vertex(1, -1, -1);
        shape.vertex(1, -1, 1);

        shape.vertex(1, 1, -1);
        shape.vertex(-1, 1, -1);
        shape.vertex(-1, -1, -1);
        shape.vertex(1, -1, -1);

        shape.vertex(-1, 1, -1);
        shape.vertex(-1, 1, 1);
        shape.vertex(-1, -1, 1);
        shape.vertex(-1, -1, -1);

        shape.vertex(-1, 1, -1);
        shape.vertex(1, 1, -1);
        shape.vertex(1, 1, 1);
        shape.vertex(-1, 1, 1);

        shape.vertex(-1, -1, -1);
        shape.vertex(1, -1, -1);
        shape.vertex(1, -1, 1);
        shape.vertex(-1, -1, 1);

        shape.endShape();

        return shape;
    }

    /**
     * Creates a Cylinder with custom bottom and top radius
     * https://processing.org/examples/vertices.html
     *
     * @param topRadius
     * @param bottomRadius
     * @param tall
     * @param sides
     * @param parent
     */
    public static void createCylinder(float topRadius, float bottomRadius, float tall, int sides, PApplet parent) {
        float angle = 0;
        float angleIncrement = PConstants.TWO_PI / sides;

        parent.beginShape(PConstants.QUAD_STRIP);

        for (int i = 0; i < sides + 1; ++i) {
            parent.vertex(topRadius * parent.cos(angle), 0, topRadius * parent.sin(angle));
            parent.vertex(bottomRadius * parent.cos(angle), tall, bottomRadius * parent.sin(angle));
            angle += angleIncrement;
        }

        parent.endShape();

        // If it is not a cone, draw the circular top cap
        if (topRadius != 0) {
            angle = 0;
            parent.beginShape(PConstants.TRIANGLE_FAN);

            // Center point
            parent.vertex(0, 0, 0);
            for (int i = 0; i < sides + 1; i++) {
                parent.vertex(topRadius * parent.cos(angle), 0, topRadius * parent.sin(angle));
                angle += angleIncrement;
            }
            parent.endShape();
        }

        // If it is not a cone, draw the circular bottom cap
        if (bottomRadius != 0) {
            angle = 0;
            parent.beginShape(PConstants.TRIANGLE_FAN);

            // Center point
            parent.vertex(0, tall, 0);
            for (int i = 0; i < sides + 1; i++) {
                parent.vertex(bottomRadius * parent.cos(angle), tall, bottomRadius * parent.sin(angle));
                angle += angleIncrement;
            }
            parent.endShape();
        }
    }


    /**
     * https://processing.org/tutorials/p3d/
     */
    public void createPyramid()
    {
//        beginShape();
//        vertex(-100, -100, -100);
//        vertex( 100, -100, -100);
//        vertex(   0,    0,  100);
//
//        vertex( 100, -100, -100);
//        vertex( 100,  100, -100);
//        vertex(   0,    0,  100);
//
//        vertex( 100, 100, -100);
//        vertex(-100, 100, -100);
//        vertex(   0,   0,  100);
//
//        vertex(-100,  100, -100);
//        vertex(-100, -100, -100);
//        vertex(   0,    0,  100);
//        endShape();
    }
}
