package luisf.ouroboros.common;

import processing.core.PGraphics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class Handy {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    /**
     * Check if a string is Null and Empty
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.isEmpty());
    }

    /**
     * Mimics the String.format method with a smaller name
     *
     * @param format
     * @param args
     * @return
     */
    public static String f(String format, Object... args) {
        return String.format(format, args);
    }

    /**
     * Extracts the file extension from a given File
     *
     * @param file
     * @return extension name without a dot
     */
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

    /**
     * Checks if the given path points to an existing folder and converts it to a File
     *
     * @param path path to folder
     * @return
     */
    public static File validateFolderPath(String path) {
        if (Handy.isNullOrEmpty(path)) {
            return null;
        }

        File folder = new File(path);

        if (folder.exists() && folder.isDirectory()) {
            return folder;
        } else {
            try {
                log.severe(Handy.f("The folder '%s' doesn't exist or its not a directory", folder.getCanonicalPath()));
            } catch (IOException e) {
                log.severe("An exception occurred while getting the canonical path");
                e.printStackTrace();
            }
            return null;
        }
    }

    public static String fileToString(File file) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.severe(Handy.f("Exception occurred while reading the file '%s'", file.toPath()));
            e.printStackTrace();
        }
        return content;
    }

    public static String removeSubString(int startIndex, int endIndex, String content) {
        if (startIndex < 0 || endIndex > content.length()) {
            log.severe("The start or end indexes of the substring are incorrect");
            return content;
        }

        String result = content.substring(0, startIndex);
        return result.concat(content.substring(endIndex + 1, content.length()));
    }

    private static final int axisThickness = 1;

    /**
     * Draw X, Y and Z representations of the axes
     * @param g
     * @param prepare
     */
    public static void drawAxes(PGraphics g, Boolean prepare, int axisLength)
    {
        if (prepare)
        {
            g.beginDraw();
        }

        g.pushStyle();

        // X axis, red
        g.strokeWeight(0.5f);
        g.fill(180, 0, 0, 80);
        g.stroke(255, 0, 0);
        g.pushMatrix();
        g.translate(axisLength/2, 0, 0);
        g.box(axisLength, axisThickness, axisThickness);
        g.popMatrix();

        // Y axis, green
        g.fill(0, 180, 0, 80);
        g.stroke(0, 255, 0);
        g.pushMatrix();
        g.translate(0, axisLength/2, 0);
        g.box(axisThickness, axisLength, axisThickness);
        g.popMatrix();

        // Z axis, blue
        g.fill(0, 0, 180, 80);
        g.stroke(0, 0, 255);
        g.pushMatrix();
        g.translate(0, 0, axisLength/2);
        g.box(axisThickness, axisThickness, axisLength);
        g.popMatrix();

        g.popStyle();

        if (prepare)
        {
            g.endDraw();
        }
    }

    public static void drawAxes(PGraphics g, Boolean prepare)
    {
        drawAxes(g, prepare, 80);
    }
}
