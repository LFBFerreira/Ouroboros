package luisf.ouroboros.common;

public class Handy {
    /**
     * Check if a string is Null and Empty
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.isEmpty());
    }

    /**
     * Mimics the String.format method with a smaller name
     * @param format
     * @param args
     * @return
     */
    public static String f(String format, Object... args)
    {
        return String.format(format, args);
    }
}
