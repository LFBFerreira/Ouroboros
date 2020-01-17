package luisf.ouroboros.visualizer.suits.suit01;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

public class Shaper {

    public static PShape createBox(float width, float height, float depth, PApplet parent) {
        PShape shape = parent.createShape(parent.BOX, width, height, depth);
        return shape;
    }


    public static PShape createBoxWireframe(float width, float height, float depth, PApplet parent) {
        PShape shape = new PShape();
        PShape baseShape = createBox(width, height, depth, parent);
        PVector lastVertex = null;

//        shape.noFill();
        shape.setFill(255);
        shape.setStroke(0);
        shape.setStrokeWeight(2);

        shape.beginShape();

        // create a new shape using only vertexes
        for (int i = 0; i < baseShape.getVertexCount(); i++) {
            PVector v = baseShape.getVertex(i);
            shape.vertex(v.x, v.y, v.z);

//            if(lastVertex != null)
//            {
//                shape.vertex(lastVertex.x, lastVertex.y, lastVertex.z);
//            }

            lastVertex = v;
        }

        shape.endShape(PConstants.CLOSE);

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
