package luisf.ouroboros.visualizer.scene;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.hmi.InputEvent;
import luisf.ouroboros.hmi.InputListennerInterface;
import processing.core.PApplet;
import processing.event.MouseEvent;
import remixlab.bias.Agent;
import remixlab.bias.BogusEvent;
import remixlab.bias.event.*;
import remixlab.proscene.MouseAgent;
import remixlab.proscene.Scene;

import java.util.logging.Logger;

public class CameraControlAgent extends MouseAgent implements InputListennerInterface {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public CameraControlAgent(Scene scene) {
        super(scene);
    }

    public void setPickingMode(MouseAgent.PickingMode mode) {
        pMode = mode;
    }

    public MouseAgent.PickingMode pickingMode() {
        return pMode;
    }

    // ================================================================



    private void theRest(int x, int y, int action, int button, int modifiers, int count) {
        move = action == processing.event.MouseEvent.MOVE;
        press = action == processing.event.MouseEvent.PRESS;
        drag = action == processing.event.MouseEvent.DRAG;
        release = action == processing.event.MouseEvent.RELEASE;

        if (move || press || drag || release) {
            log.info(Handy.f("%s \t%s\t%s\t%s\t%s\t%s", x, y, action, button, modifiers, count));

            currentEvent = new DOF2Event(prevEvent, x - scene.originCorner().x(), y - scene.originCorner().y(),
                    modifiers, move ? BogusEvent.NO_ID : button);

            if (move && (pickingMode() == MouseAgent.PickingMode.MOVE)) {
                updateTrackedGrabber(currentEvent);
            }

            handle(press ? currentEvent.fire() : release ? currentEvent.flush() : currentEvent);
            prevEvent = currentEvent.get();
            return;
        }

        if (action == processing.event.MouseEvent.WHEEL) {
            log.info(Handy.f("second IF"));
            handle(new DOF1Event(count, modifiers, WHEEL_ID));
            return;
        }

        if (action == processing.event.MouseEvent.CLICK) {
            log.info(Handy.f("third If"));
            ClickEvent bogusClickEvent = new ClickEvent(x - scene.originCorner().x(), y - scene.originCorner().y(),
                    modifiers, button, count);

            if (pickingMode() == MouseAgent.PickingMode.CLICK) {
                updateTrackedGrabber(bogusClickEvent);
            }

            handle(bogusClickEvent);
            return;
        }
    }

    public void mouseEvent(MouseEvent e) {
//        theRest(e.getX(), e.getY(), e.getAction(), e.getButton(), e.getModifiers(), e.getCount());
        //scene.mouseAgent().mouseEvent(e);
    }

    // ================================================================

    Boolean started = false;

    @Override
    public void reactToInput(InputEvent input) {
        if (!started)
        {
            click(input.getAsXY().x, input.getAsXY().y);
            started = true;
        }

        drag(input.getAsXY().x, input.getAsXY().y);
    }

    private void click(float normalizedX, float normalizedY) {
        PApplet parent = scene.pApplet();

        int windowX = (int)parent.map(normalizedX, 0, 1, 0, parent.width);
        int windowY = (int)parent.map(normalizedY, 0, 1, 0, parent.height);

        theRest(windowX, windowY, MouseEvent.MOVE, 0, 0, 0);
    }

    private void drag(float normalizedX, float normalizedY) {
        PApplet parent = scene.pApplet();

        int windowX = (int)parent.map(normalizedX, 0, 1, 0, parent.width);
        int windowY = (int)parent.map(normalizedY, 0, 1, 0, parent.height);

        theRest(windowX, windowY, processing.event.MouseEvent.DRAG, 37, 0, 1);
    }

}
