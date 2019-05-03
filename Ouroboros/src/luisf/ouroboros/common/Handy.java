package luisf.ouroboros.common;

import java.io.File;

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

    public static String getFileExtension(File file) {

        String name = file.getName().substring(Math.max(file.getName().lastIndexOf('/'),
                file.getName().lastIndexOf('\\')) < 0 ? 0 : Math.max(file.getName().lastIndexOf('/'),
                file.getName().lastIndexOf('\\')));
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1); // doesn't return "." with extension
    }
}
