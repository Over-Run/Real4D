package org.overrun.real4d.util;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Mth {
    public static long divSafe(long a, long b) {
        if (b == 0) {
            return a;
        }
        return a / b;
    }
}
