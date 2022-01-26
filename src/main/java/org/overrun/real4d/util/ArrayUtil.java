package org.overrun.real4d.util;

/**
 * @author squid233
 * @since 0.1.0
 */
public class ArrayUtil {
    public static float[] append(float[] src, float... elements) {
        var dst = new float[src.length + elements.length];
        System.arraycopy(src, 0, dst, 0, src.length);
        System.arraycopy(elements, 0, dst, src.length, elements.length);
        return dst;
    }
}
