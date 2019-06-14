package luisf.ouroboros.hmi;

import oscP5.OscMessage;
import processing.core.PVector;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class InputEvent {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public InputMethodEnum inputMethod = InputMethodEnum.NONE;

    public String id = "";

    public String source = "";

    private List<Float> values = new LinkedList<>();

    // ================================================================

//    public InputEvent(InputMethodEnum method, String id, String source)
//    {
//        this.inputMethod = method;
//        this.id = id;
//        this.source = source;
//    }
//
//    public InputEvent(InputMethodEnum method, String id)
//    {
//        this(method, id, "");
//    }

    public InputEvent(OscMessage message) {
        inputMethod = InputMethodEnum.OSC;
        id = message.addrPattern();
        source = message.address();
        values = parseOscValues(message);
    }


    // ================================================================

    // Public

    public float getFloatValue() {
        if (values.isEmpty())
        {
            log.severe("There are no values for this input");
            return 0;
        }

        return values.get(0).floatValue();
    }

    public Boolean getBooleanValue() {
        if (values.isEmpty())
        {
            log.severe("There are no values for this input");
            return false;
        }

        return values.get(0) == 1;
    }

    public PVector getXYValue() {
        if (values.size() < 2)
        {
            log.severe("There are not enough values for this input");
            return new PVector();
        }

        return new PVector(values.get(0).floatValue(), values.get(1).floatValue());
    }

    // ================================================================

    // Helpers

    private List<Float> parseOscValues(OscMessage message) {
        List<Float> values = new LinkedList<>();

        String types = message.typetag();

        for (int i = 0; i < types.length(); i++) {
            switch (types.charAt(i)) {
                case 'f':
                    values.add(message.get(i).floatValue());
                    break;

                default:
                    log.severe("Could not parse the type of the OSC message");
            }
        }

        return  values;
    }
}
