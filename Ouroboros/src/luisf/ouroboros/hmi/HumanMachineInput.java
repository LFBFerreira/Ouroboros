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

    public void dispose()
    {
        osc.dispose();
    }

    public void registerListeners(InputListennerInterface[] listeners) {
        this.listeners = Arrays.asList(listeners);
    }


    // ================================================================

    // Helpers
    private void oscEvent(OscMessage theOscMessage) {
        announceEvent(new InputEvent(theOscMessage));
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

        //log.info(Handy.f("Event %s: %s - %f", event.inputMethod, event.id, event.getAsFloat()));

        listeners.forEach(l -> l.reactToInput(event));
    }
}
