package org.overrun.real4d.client.input;

import org.overrun.real4d.client.Real4D;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Input {
    public static int mouseX, mouseY, deltaMX, deltaMY;

    /**
     * Is key pressed
     *
     * @param key The key
     * @return key pressed
     */
    public static boolean keyPressed(final int key) {
        return Real4D.getInstance().handle.key(key) == GLFW_PRESS;
    }

    /**
     * Is key released
     *
     * @param key The key
     * @return key released
     */
    public static boolean keyReleased(final int key) {
        return Real4D.getInstance().handle.key(key) == GLFW_RELEASE;
    }

    /**
     * Is mouse button pressed
     *
     * @param button The mouse button
     * @return mouse button pressed
     */
    public static boolean mousePressed(final int button) {
        return Real4D.getInstance().handle.mouse(button) == GLFW_PRESS;
    }

    /**
     * Is mouse button released
     *
     * @param button The mouse button
     * @return mouse button released
     */
    public static boolean mouseReleased(final int button) {
        return Real4D.getInstance().handle.mouse(button) == GLFW_RELEASE;
    }

    public static int getMouseX() {
        return mouseX;
    }

    public static int getMouseY() {
        return mouseY;
    }

    public static int getDeltaMX() {
        return deltaMX;
    }

    public static int getDeltaMY() {
        return deltaMY;
    }
}
