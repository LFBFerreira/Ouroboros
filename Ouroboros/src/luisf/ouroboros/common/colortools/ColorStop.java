package luisf.ouroboros.common.colortools;

import processing.core.PApplet;

import static processing.core.PConstants.HSB;

public class ColorStop implements Comparable<ColorStop> {
    static final float TOLERANCE = 0.09f;
    public float percent;
    public int clr;

    public ColorStop(int colorMode, float percent, float[] arr) {
        this(colorMode, percent, arr[0], arr[1], arr[2],
                arr.length == 4 ? arr[3] : 1);
    }

    public ColorStop(int colorMode, float percent, float x, float y, float z, float w) {
        this(percent, colorMode == HSB ? ColorTools.composeclr(ColorTools.hsbToRgb(x, y, z, w))
                : ColorTools.composeclr(x, y, z, w));
    }

    public ColorStop(float percent, int clr) {
        this.percent = PApplet.constrain(percent, 0, 1);
        this.clr = clr;
    }

    public boolean approxPercent(ColorStop cs, float tolerance) {
        return Math.abs(percent - cs.percent) < tolerance;
    }

    // Mandated by the interface Comparable<ColorStop>.
    // Permits color stops to be sorted by Collections.sort.
    public int compareTo(ColorStop cs) {
        return percent > cs.percent ? 1 : percent < cs.percent ? -1 : 0;
    }
}
