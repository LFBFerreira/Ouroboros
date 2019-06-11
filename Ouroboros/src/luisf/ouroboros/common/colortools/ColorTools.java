package luisf.ouroboros.common.colortools;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class ColorTools {

    private static final float colorByteRangeRatio = 1 / 255.0f;

    /**
     * Composes a color given a vector with 4 components, in range 0 .. 1
     *
     * @param in
     * @return color
     */
    public static int composeclr(float[] in) {
        return composeclr(in[0], in[1], in[2], in[3]);
    }

    /**
     * Composes a color given its 4 components, in range 0 .. 1
     *
     * @param red
     * @param green
     * @param blue
     * @param alpha
     * @return color
     */
    public static int composeclr(float red, float green, float blue, float alpha) {
        return Math.round(alpha * 255.0f) << 24
                | Math.round(red * 255.0f) << 16
                | Math.round(green * 255.0f) << 8
                | Math.round(blue * 255.0f);
    }

    /**
     * Composes a vector of colors given a vector of vector with 4 components, in range 0 .. 1
     *
     * @param in
     * @return color
     */
    public static int[] composeclr(float[][] in) {
        int sz = in.length;
        int[] out = new int[sz];
        for (int i = 0; i < sz; ++i) {
            out[i] = composeclr(in[i][0], in[i][1], in[i][2], in[i][3]);
        }
        return out;
    }

    //-------------------------------------------------------------------------------

    /**
     * Decomposes a color given its 4 components, in range 0 .. 1
     *
     * @param clr
     * @return
     */
    public static float[] decomposeclr(int clr) {
        return decomposeclr(clr, new float[]{0.0f, 0.0f, 0.0f, 1.0f});
    }

    public static float[] decomposeclr(int clr, float[] out) {
        out[3] = (clr >> 24 & 0xff) * colorByteRangeRatio;
        out[0] = (clr >> 16 & 0xff) * colorByteRangeRatio;
        out[1] = (clr >> 8 & 0xff) * colorByteRangeRatio;
        out[2] = (clr & 0xff) * colorByteRangeRatio;
        return out;
    }

    //-------------------------------------------------------------------------------

    public static float[] hsbToRgb(float[] in) {
        float[] out = new float[]{0, 0, 0, 1};
        return hsbToRgb(in[0], in[1], in[2], in[3], out);
    }

    public static float[] hsbToRgb(float[] in, float[] out) {
        if (in.length == 3) {
            return hsbToRgb(in[0], in[1], in[2], 1, out);
        } else if (in.length == 4) {
            return hsbToRgb(in[0], in[1], in[2], in[3], out);
        }
        return out;
    }

    public static float[] hsbToRgb(float hue, float sat, float bri, float alpha) {
        float[] out = new float[]{0, 0, 0, 1};
        return hsbToRgb(hue, sat, bri, alpha, out);
    }

    public static float[] hsbToRgb(float hue, float sat, float bri, float alpha, float[] out) {
        if (sat == 0.0) {
            // 0.0 saturation is grayscale, so all values are equal.
            out[0] = out[1] = out[2] = bri;
        } else {

            // Divide color wheel into 6 sectors.
            // Scale up hue to 6, convert to sector index.
            float h = hue * 6.0f;
            int sector = (int) h;

            // Depending on the sector, three tints will
            // be distributed among R, G, B channels.
            float tint1 = bri * (1f - sat);
            float tint2 = bri * (1f - sat * (h - sector));
            float tint3 = bri * (1f - sat * (1f + sector - h));

            switch (sector) {
                case 1:
                    out[0] = tint2;
                    out[1] = bri;
                    out[2] = tint1;
                    break;
                case 2:
                    out[0] = tint1;
                    out[1] = bri;
                    out[2] = tint3;
                    break;
                case 3:
                    out[0] = tint1;
                    out[1] = tint2;
                    out[2] = bri;
                    break;
                case 4:
                    out[0] = tint3;
                    out[1] = tint1;
                    out[2] = bri;
                    break;
                case 5:
                    out[0] = bri;
                    out[1] = tint1;
                    out[2] = tint2;
                    break;
                default:
                    out[0] = bri;
                    out[1] = tint3;
                    out[2] = tint1;
            }
        }

        out[3] = alpha;
        return out;
    }


    //-------------------------------------------------------------------------------


    public static float[] rgbToHsb(int clr) {
        return rgbToHsb(clr, new float[]{0, 0, 0, 1});
    }

    public static float[] rgbToHsb(int clr, float[] out) {
        return rgbToHsb((clr >> 16 & 0xff) * colorByteRangeRatio,
                (clr >> 8 & 0xff) * colorByteRangeRatio,
                (clr & 0xff) * colorByteRangeRatio,
                (clr >> 24 & 0xff) * colorByteRangeRatio, out);
    }

    public static float[] rgbToHsb(float[] in) {
        return rgbToHsb(in, new float[]{0, 0, 0, 1});
    }

    public static float[] rgbToHsb(float[] in, float[] out) {
        if (in.length == 3) {
            return rgbToHsb(in[0], in[1], in[2], 1, out);
        } else if (in.length == 4) {
            return rgbToHsb(in[0], in[1], in[2], in[3], out);
        }
        return out;
    }

    public static float[] rgbToHsb(float red, float green, float blue, float alpha, float[] out) {

        // Find highest and lowest values.
        float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));

        // Find the difference between max and min.
        float delta = max - min;

        // Calculate hue.
        float hue = 0;
        if (delta != 0) {
            if (red == max) {
                hue = (green - blue) / delta;
            } else if (green == max) {
                hue = 2 + (blue - red) / delta;
            } else {
                hue = 4 + (red - green) / delta;
            }

            hue /= 6.0;
            if (hue < 0.0) {
                hue += 1.0;
            }
        }

        out[0] = hue;
        out[1] = max == 0 ? 0 : (max - min) / max;
        out[2] = max;
        out[3] = alpha;

        return out;
    }

    //-------------------------------------------------------------------------------

    private static float smootherStep(float st) {
        return st * st * st * (st * (st * 6f - 15f) + 10f);
    }

    /**
     * @param a
     * @param b
     * @param st
     * @param out
     * @return
     */
    public static float[] smootherStepRgb(float[] a, float[] b, float st, float[] out) {
        float eval = smootherStep(st);
        out[0] = a[0] + eval * (b[0] - a[0]);
        out[1] = a[1] + eval * (b[1] - a[1]);
        out[2] = a[2] + eval * (b[2] - a[2]);
        out[3] = a[3] + eval * (b[3] - a[3]);
        return out;
    }

    /**
     * Calculates the intermediate color between two colors in a color array, given a step from 0 .. 1
     *
     * @param arr
     * @param st  0..1
     * @param out
     * @return
     */
    public static float[] smootherStepRgb(float[][] arr, float st, float[] out) {
        int sz = arr.length;
        if (sz == 1 || st < 0) {
            out = java.util.Arrays.copyOf(arr[0], 0);
            return out;
        } else if (st > 1) {
            out = java.util.Arrays.copyOf(arr[sz - 1], 0);
            return out;
        }

        float scl = st * (sz - 1);
        int i = (int) scl;
        float eval = smootherStep(scl - i);

        out[0] = arr[i][0] + eval * (arr[i + 1][0] - arr[i][0]);
        out[1] = arr[i][1] + eval * (arr[i + 1][1] - arr[i][1]);
        out[2] = arr[i][2] + eval * (arr[i + 1][2] - arr[i][2]);
        out[3] = arr[i][3] + eval * (arr[i + 1][3] - arr[i][3]);
        return out;
    }

    /**
     * Calculates the intermediate color between two colors in a color array, given a step from 0 .. 1
     *
     * @param arr
     * @param st  0..1
     * @param out
     * @return
     */
    public static float[] smootherStepRgb(Gradient arr, float st, float[] out) {
        int sz = arr.colorStops.size();

        if (sz == 1 || st <= 0) {
            return decomposeclr(arr.colorStops.get(0).clr);
        } else if (st > 1) {
            return decomposeclr(arr.colorStops.get(sz - 1).clr);
        }

        int section = -1;
        int startColorIndex = 0;
        int endColorIndex = 0;

        // find start and end color on the array
        for (int i = 0; i < arr.colorStops.size(); i++) {
            if (st < arr.colorStops.get(i).percent) {
                startColorIndex = i - 1;
                endColorIndex = i;
                break;
            }
        }

        float scl = PApplet.map(st,
                0, 1,
                arr.colorStops.get(startColorIndex).percent, arr.colorStops.get(endColorIndex).percent);

        float[] startColor = decomposeclr(arr.colorStops.get(startColorIndex).clr);
        float[] endColor = decomposeclr(arr.colorStops.get(endColorIndex).clr);
        float[] result = new float[4];

        smootherStepRgb(startColor, endColor, scl, result);

        return result;
    }

    //-------------------------------------------------------------------------------

    /**
     * Calculates the intermediate color between two colors in a color array, given a step from 0 .. 1
     *
     * @param a
     * @param b
     * @param st  0..1
     * @param out
     * @return
     */
    public static float[] smootherStepHsb(float[] a, float[] b, float st, float[] out) {

        // Find difference in hues.
        float huea = a[0];
        float hueb = b[0];
        float delta = hueb - huea;

        // Prefer shortest distance.
        if (delta < -0.5) {
            hueb += 1.0;
        } else if (delta > 0.5) {
            huea += 1.0;
        }

        float eval = smootherStep(st);

        // The two hues may be outside of 0 .. 1 range,
        // so modulate by 1.
        out[0] = (huea + eval * (hueb - huea)) % 1;
        out[1] = a[1] + eval * (b[1] - a[1]);
        out[2] = a[2] + eval * (b[2] - a[2]);
        out[3] = a[3] + eval * (b[3] - a[3]);
        return out;
    }

    /**
     * Calculates the intermediate color between two colors in a color array, given a step from 0 .. 1
     *
     * @param arr
     * @param st
     * @param out
     * @return
     */
    public static float[] smootherStepHsb(float[][] arr, float st, float[] out) {
        int sz = arr.length;
        if (sz == 1 || st < 0) {
            out = java.util.Arrays.copyOf(arr[0], 0);
            return out;
        } else if (st > 1) {
            out = java.util.Arrays.copyOf(arr[sz - 1], 0);
            return out;
        }

        float scl = st * (sz - 1);
        int i = (int) scl;


        float huea = arr[i][0];
        float hueb = arr[i + 1][0];
        float delta = hueb - huea;

        if (delta < -0.5) {
            hueb += 1.0 / (sz - 1.0);
        } else if (delta > 0.5) {
            huea += 1.0 / (sz - 1.0);
        }

        float eval = smootherStep(scl - i);
        out[0] = (huea + eval * (hueb - huea)) % 1;

        return out;
    }

    //-------------------------------------------------------------------------------

    /**
     * Overload of lerpColor to be able to handle an array of colors
     *
     * @param colors
     * @param step      0 .. 1
     * @param colorMode
     * @return
     */
    public static int lerpColors(int[] colors, float step, int colorMode) {
        int sz = colors.length;

        if (sz == 1 || step <= 0.0) {
            return colors[0];
        } else if (step >= 1.0) {
            return colors[sz - 1];
        }

        float scl = step * (sz - 1);
        int i = (int) scl;

        return PApplet.lerpColor(colors[i], colors[i + 1], scl - i, colorMode);
    }

    //-------------------------------------------------------------------------------

    public static void fillGradient(int[] palette,
                                    PVector startPoint,
                                    PVector endPoint,
                                    PGraphics graphics,
                                    Boolean showLine) {
        float w = graphics.width;
        float h = graphics.height;

        graphics.loadPixels();

        // Imaginary line of gradient.
        float ox = startPoint.x;
        float oy = startPoint.y;
        float dx = endPoint.x;
        float dy = endPoint.y;
        float st;

        // paint pixel by pixel of the graphics
        for (int i = 0, y = 0, x; y < h; ++y) {
            for (x = 0; x < w; ++x, ++i) {
                st = project(ox, oy, dx, dy, x, y);
                graphics.pixels[i] = ColorTools.lerpColors(palette, st, PConstants.RGB);
            }
        }

        graphics.updatePixels();

        if (showLine) {
            graphics.pushStyle();
            graphics.strokeWeight(1);
            graphics.stroke(255);
            graphics.line(ox, oy, dx, dy);
            graphics.popStyle();
        }
    }


    private static void setGradient(int x, int y, float w, float h, int c1, int c2, boolean vertical, int colorMode, PGraphics graphics) {

        graphics.pushStyle();

        graphics.noFill();

        if (vertical) {  // Top to bottom gradient
            for (int i = y; i <= y + h; i++) {
                float inter = PApplet.map(i, y, y + h, 0, 1);
                int c = PApplet.lerpColor(c1, c2, inter, colorMode);
                graphics.stroke(c);
                graphics.line(x, i, x + w, i);
            }
        } else {  // Left to right gradient
            for (int i = x; i <= x + w; i++) {
                float inter = PApplet.map(i, x, x + w, 0, 1);
                int c = PApplet.lerpColor(c1, c2, inter, colorMode);
                graphics.stroke(c);
                graphics.line(i, y, i, y + h);
            }
        }

        graphics.popStyle();
    }

    //-------------------------------------------------------------------------------

    private static float project(float originX, float originY,
                                 float destX, float destY,
                                 int pointX, int pointY) {

        // Rise and run of line.
        float odX = destX - originX;
        float odY = destY - originY;

        // Distance-squared of line.
        float odSq = odX * odX + odY * odY;

        // Rise and run of projection.
        float opX = pointX - originX;
        float opY = pointY - originY;
        float opXod = opX * odX + opY * odY;

        // Normalize and clamp range.
        return PApplet.constrain(opXod / odSq, 0, 1);
    }
}
