package org.overrun.real4d.util;

import org.jetbrains.annotations.Contract;

import static java.lang.Math.min;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Maths {
    /**
     * Clamp a decimal to value.
     *
     * @param a           The origin decimal.
     * @param boundMin    The return value when a < boundCenter
     * @param boundMax    The return value when a > boundCenter
     * @param boundCenter The bound center.
     * @return The value.
     */
    @Contract(pure = true)
    public static float clamp(
        float a,
        float boundMin,
        float boundMax,
        float boundCenter
    ) {
        return a < boundCenter ? boundMin : (a > boundCenter ? boundMax : boundCenter);
    }

    /**
     * Clamp a decimal to value {@code [min,max]=a}.
     *
     * @param a   The origin decimal.
     * @param min The minimum decimal.
     * @param max The maximum decimal.
     * @return The decimal.
     */
    @Contract(pure = true)
    public static float clamp(
        float a,
        float min,
        float max
    ) {
        return a < min ? min : (min(a, max));
    }
}
