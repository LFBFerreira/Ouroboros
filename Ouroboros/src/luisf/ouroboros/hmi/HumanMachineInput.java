package luisf.ouroboros.hmi;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.properties.PropertyManager;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class HumanMachineInput {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final PropertyManager props = PropertyManager.getInstance();

    private List<InputListennerInterface> listeners = new LinkedList<>();

    private OscP5 osc;

    private PApplet parent;

    // ================================================================

    public HumanMachineInput(int oscPort, PApplet parent) {
        this.parent = parent;
        osc = new OscP5(this, oscPort);
    }

    // ================================================================

    // Public

    public void registerListeners(InputListennerInterface[] listeners) {
        this.listeners = Arrays.asList(listeners);
    }


    // ================================================================

    // Helpers
    private void oscEvent(OscMessage theOscMessage) {
        /* print the address pattern and the typetag of the received OscMessage */
//        log.info(Handy.f("Add: %s,\tValue: %s (%s)",
//                theOscMessage.addrPattern(),
//                theOscMessage.get(0).floatValue(),
//                theOscMessage.typetag()));

        announceEvent(new InputEvent(theOscMessage));

//        if (addr.equals("/3/fader1")) {
//            log.info("FADER 1");
//        } else if (addr.equals("/1/fader2")) {
//            log.info("FADER 2");
//        } else if (addr.equals("/1/fader3")) {
//            log.info("CROSSFADE");
//        } else if (addr.equals("/1/rotary1")) {
//            log.info("ROTARY INPUT 1");
//        } else if (addr.equals("/3/xy")) {
//        }
    }


    private void announceEvent(InputEvent event)
    {
        if (listeners == null)
        {
            log.severe("The list of listeners is null!");
            return;
        }

        if (event == null)
        {
            log.severe("The event is null!");
            return;
        }

        log.info(Handy.f("Event %s: %s - %f", event.inputMethod, event.id, event.getFloatValue()));

        listeners.forEach(l -> l.reactToInput(event));
    }
}
