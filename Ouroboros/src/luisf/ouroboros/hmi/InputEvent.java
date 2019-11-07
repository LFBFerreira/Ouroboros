package luisf.ouroboros.hmi;

import luisf.ouroboros.common.Handy;
import oscP5.OscMessage;
import processing.core.PGraphics;
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
    private final String separatingChar = "/";

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

    public float getAsFloat() {
        if (values.isEmpty()) {
            log.severe("There are no values for this input");
            return 0;
        }

        return values.get(0).floatValue();
    }

    public float getAsFloat(int min, int max) {
        return map(getAsFloat(), 0, 1, min, max);
    }

    public Boolean getAsBoolean() {
        if (values.isEmpty()) {
            log.severe("There are no values for this input");
            return false;
        }

        return values.get(0) == 1;
    }

    public PVector getAsXY() {
        if (values.size() < 2) {
            log.severe("There are not enough values for this input");
            return new PVector();
        }

        return new PVector(values.get(0).floatValue(), values.get(1).floatValue());
    }

    @Override
    public String toString() {
//        return Handy.f("id = %s \tsource = %s \tvalues = %s", id, source, values);
        return Handy.f("id = %s \tvalues = %s", id, values);
    }

    // ================================================================

    // Helpers

    private float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    private List<Float> parseOscValues(OscMessage message) {
        List<Float> values = new LinkedList<>();

        String types = message.typetag();

        float value = 0;
        for (int i = 0; i < types.length(); i++) {
            switch (types.charAt(i)) {
                case 'f':
                    try {
                        value = message.get(i).floatValue();
                    } catch (NumberFormatException e) {
                        log.severe("EXCEPTION!");
                        e.printStackTrace();
                    }
                    values.add(value);
                    break;

                default:
                    log.severe("Could not parse the type of the OSC message");
            }
        }

        return values;
    }

    public boolean isPage(String page) {
        return page.equals(getPage());
    }

    public boolean isName(String name) {
        return name.equals(getName());
    }

    public boolean isGroup(String group) {
        return group.equals(getGroup());
    }

    public String getPage() {
        if (id.equals("")) {
            return "";
        }

        String[] parts = id.split(separatingChar);
        if (parts.length > 1) {
            return parts[1];
        } else {
            return "";
        }
    }

    public String getName() {
        if (id.equals("")) {
            return "";
        }

        String[] parts = id.split(separatingChar);
        if (parts.length >= 2) {
            return parts[2];
        } else {
            return "";
        }
    }

    public String getGroup() {
        if (id.equals("")) {
            return "";
        }

        String[] parts = id.split(separatingChar);
        if (parts.length >= 3) {
            return parts[3];
        } else {
            return "";
        }
    }

}
