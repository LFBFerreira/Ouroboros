package luisf.ouroboros.visualizer.scene;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.hmi.InputListennerInterface;
import processing.core.PApplet;
import processing.core.PConstants;
import remixlab.bias.Agent;
import remixlab.bias.event.MotionEvent;
import remixlab.dandelion.core.Eye;
import remixlab.dandelion.geom.Vec;
import remixlab.proscene.Scene;

import java.util.logging.Logger;

public class CameraControlAgent extends Agent implements InputListennerInterface {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private PApplet parent;
    private Scene scene;
    private Eye eye;

    public CameraControlAgent(Scene scene) {
        super(scene.inputHandler());

        parent = scene.pApplet();
        this.scene = scene;
        this.eye = scene.eye();
    }


    MotionEvent newEvent;
    MotionEvent prevEvent;

    // ================================================================


//    private void theRest(int x, int y, int action, int button, int modifiers, int count) {
//        move = action == processing.event.MouseEvent.MOVE;
//        press = action == processing.event.MouseEvent.PRESS;
//        moveXY = action == processing.event.MouseEvent.DRAG;
//        release = action == processing.event.MouseEvent.RELEASE;
//
//        if (move || press || moveXY || release) {
//            log.info(Handy.f("%s \t%s\t%s\t%s\t%s\t%s", x, y, action, button, modifiers, count));
//
//            currentEvent = new DOF2Event(prevEvent, x - scene.originCorner().x(), y - scene.originCorner().y(),
//                    modifiers, move ? BogusEvent.NO_ID : button);
//
//            if (move && (pickingMode() == MouseAgent.PickingMode.MOVE)) {
//                updateTrackedGrabber(currentEvent);
//            }
//
//            handle(press ? currentEvent.fire() : release ? currentEvent.flush() : currentEvent);
//            prevEvent = currentEvent.get();
//            return;
//        }
//
//        if (action == processing.event.MouseEvent.WHEEL) {
//            log.info(Handy.f("second IF"));
//            handle(new DOF1Event(count, modifiers, WHEEL_ID));
//            return;
//        }
//
//        if (action == processing.event.MouseEvent.CLICK) {
//            log.info(Handy.f("third If"));
//            ClickEvent bogusClickEvent = new ClickEvent(x - scene.originCorner().x(), y - scene.originCorner().y(),
//                    modifiers, button, count);
//
//            if (pickingMode() == MouseAgent.PickingMode.CLICK) {
//                updateTrackedGrabber(bogusClickEvent);
//            }
//
//            handle(bogusClickEvent);
//            return;
//        }
//    }


    // ================================================================


    @Override
    public void reactToInput(InputEvent input) {

        if (input.isPage("1") && input.isName("moveXY") && input.isGroup("1")) {
            translate(input.getAsXY().x, 0, input.getAsXY().y);
        } else if (input.isPage("1") && input.isName("lookXY") && input.isGroup("1")) {
            orient(input.getAsXY().y, input.getAsXY().x);
        } else if (input.isPage("1") && input.isName("heightXY") && input.isGroup("1")) {
            translate(0, input.getAsFloat(), 0);
        } else if (input.isPage("1") && input.isName("centerPush")) {
            if (input.getAsBoolean() == true) {
                center();
            }
        }

//        if (input.id.equals(OscStringBuilder.build(1, "moveXY", 1))) {
//            translate(input.getAsXY().x, 0, input.getAsXY().y);
//        } else if (input.id.equals(OscStringBuilder.build(1, "lookXY", 1))) {
//            orient(input.getAsXY().y, input.getAsXY().x);
//        } else if (input.id.equals(OscStringBuilder.build(1, "heightXY", 1))) {
//            translate(0, input.getAsFloat(), 0);
//        } else if (input.id.equals(OscStringBuilder.build(1, "centerPush"))) {
//            if (input.getAsBoolean() == true) {
//                center();
//            }
//        }
    }

    float dragSensitivity = 10;
    float heightSensitivity = 5;
    float rotationSensitivity = PConstants.PI;

    private void moveHorizontal(float normalizedX, float normalizedZ) {
        log.info(Handy.f("%.2f, %.2f", normalizedX, normalizedZ));

        // get current position
//        Vec newPosition = scene.eye().position();
//        Vec newPosition = scene.eyeFrame().position();

        // modify coordinates
        // X and Z are relative,
//        newPosition.add(normalizedX * dragSensitivity,
//                -normalizedZ * dragSensitivity, 0);

        //scene.eye().setPosition(newPosition);

        scene.eyeFrame().translate(new Vec(normalizedX * dragSensitivity,
                -normalizedZ * dragSensitivity, 0));


//        newEvent = new DOF2Event(prevEvent, normalizedX * dragSensitivity, normalizedZ * dragSensitivity,
//                MotionEvent.NO_MODIFIER_MASK,
//                MotionEvent.NO_ID);
//
//        scene.eyeFrame().performInteraction(newEvent);
//
//        prevEvent = newEvent;
    }

    private void moveZ(float normalizedY) {
        log.info(Handy.f("%.2f", normalizedY));

        // get current position
        Vec newPosition = scene.eye().position();

        // modify coordinates
        // X and Z are relative,
        newPosition.add(0,
                -normalizedY * heightSensitivity,
                0);

        scene.eye().setPosition(newPosition);
    }

    private void orient(float normalizedX, float normalizedY) {
        log.info(Handy.f("%.2f, %.2f", normalizedX, normalizedY));

//        Rotation orientation = eye.orientation();
//
//        Vec newDirection = eye.viewDirection();
//        newDirection.add(normalizedX * rotationSensitivity, -normalizedY * rotationSensitivity, 0);
//
//        orientation.fromTo(eye.viewDirection(), newDirection);
//
//        eye.setOrientation(orientation);

        // -------------------------------------------

        Vec newDirection2 = scene.camera().viewDirection();
        newDirection2.add(-normalizedX * rotationSensitivity, 0, 0);
        scene.camera().setOrientation(newDirection2.x(), newDirection2.y());

//        Camera.setViewDirection(Vec)


        //        Rotation rotation =
//        Vec rotatedVector = orientation.rotate(new Vec(normalizedX * rotationSensitivity, normalizedY * rotationSensitivity));
//
//        orientation.fromTo(scene.eye().orientation(), rotatedVector);

        // -------------------------------------------

        // NOT relative
//        Vec direction = eye.viewDirection();
//        direction.add(-normalizedX * rotationSensitivity, normalizedY * rotationSensitivity, 0);
//        eye.lookAt(direction);
    }


    private void translate(float normalizedX, float normalizedY, float normalizedZ) {
        scene.eyeFrame().translate(new Vec(normalizedX * dragSensitivity,
                -normalizedY * dragSensitivity, -normalizedZ * dragSensitivity));
    }

    private void center() {
        eye.interpolateToFitScene();
    }
}
