package luisf.ouroboros.hmi;

import luisf.ouroboros.common.Handy;

public class OscStringBuilder {

    private final static char separator = '/';

    public static String build(int page, String control)
    {
        return separator + String.valueOf(page) + separator + control;
    }
}
