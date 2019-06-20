package luisf.ouroboros.visualizer.scene;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.hmi.InputListennerInterface;
import luisf.ouroboros.hmi.OscStringBuilder;
import processing.core.PApplet;
import processing.core.PConstants;
import remixlab.dandelion.core.Eye;
import remixlab.dandelion.geom.Rotation;
import remixlab.dandelion.geom.Vec;
import remixlab.proscene.Scene;

import java.util.logging.Logger;

public class CameraControlAgent implements InputListennerInterface {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private PApplet parent;
    private Scene scene;
    private Eye eye;

    public CameraControlAgent(Scene scene) {
        parent = scene.pApplet();
        this.scene = scene;
        this.eye = scene.eye();
    }


    // ================================================================


//    private void theRest(int x, int y, int action, int button, int modifiers, int count) {
//        move = action == processing.event.MouseEvent.MOVE;
//        press = action == processing.event.MouseEvent.PRESS;
//        drag = action == processing.event.MouseEvent.DRAG;
//        release = action == processing.event.MouseEvent.RELEASE;
//
//        if (move || press || drag || release) {
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
        if (input.id.equals(OscStringBuilder.build(2, "positionXY", 1))) {
            drag(input.getAsXY().x, input.getAsXY().y);
        } else if (input.id.equals(OscStringBuilder.build(2, "orientationXY", 1))) {
            orient(input.getAsXY().y, input.getAsXY().x);
        } else if (input.id.equals(OscStringBuilder.build(2, "centerPush"))) {
            if (input.getAsBoolean() == true) {
                center();
            }
        }
    }


    float dragSensitivity = 10;

    private void drag(float normalizedX, float normalizedY) {
        log.info(Handy.f("%.2f, %.2f", normalizedX, normalizedY));

        Vec newPosition = scene.eye().position();
        newPosition.add(-normalizedX * dragSensitivity, normalizedY * dragSensitivity, 0);

        scene.eye().setPosition(newPosition);
    }

    float rotationSensitivity = PConstants.PI;

    private void orient(float normalizedX, float normalizedY) {
        log.info(Handy.f("%.2f, %.2f", normalizedX, normalizedY));

        Rotation orientation = eye.orientation();

        Vec newDirection = eye.viewDirection();
        newDirection.add(-normalizedX * rotationSensitivity, normalizedY * rotationSensitivity, 0);

        orientation.fromTo(eye.viewDirection(), newDirection);

        eye.setOrientation(orientation);

        // -------------------------------------------

        eye
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


    private void center() {
        eye.interpolateToFitScene();
    }
}
