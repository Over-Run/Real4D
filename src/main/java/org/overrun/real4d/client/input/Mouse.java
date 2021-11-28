package org.overrun.real4d.client.input;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Mouse {
    public static float sensitivity = 15 / 100f;
    private static double mouseX;
    private static double mouseY;
    private static double deltaX;
    private static double deltaY;

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    public static double getDX() {
        return deltaX;
    }

    public static double getDY() {
        return deltaY;
    }

    public static void update(double x, double y) {
        deltaX = x - mouseX;
        deltaY = y - mouseY;
        mouseX = x;
        mouseY = y;
    }
}
