package org.overrun.real4d.client.input;

import org.overrun.real4d.client.Real4D;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Input {
    public int mouseX, mouseY, deltaMX, deltaMY;

    /**
     * Is key pressed
     *
     * @param key The key
     * @return key pressed
     */
    public boolean keyPressed(final int key) {
        return Real4D.INSTANCE.window.key(key) == GLFW_PRESS;
    }

    /**
     * Is key released
     *
     * @param key The key
     * @return key released
     * @since 2.0.0
     */
    public boolean keyReleased(final int key) {
        return Real4D.INSTANCE.window.key(key) == GLFW_RELEASE;
    }

    /**
     * Is mouse button pressed
     *
     * @param button The mouse button
     * @return mouse button pressed
     */
    public boolean mousePressed(final int button) {
        return Real4D.INSTANCE.window.mouse(button) == GLFW_PRESS;
    }

    /**
     * Is mouse button released
     *
     * @param button The mouse button
     * @return mouse button released
     */
    public boolean mouseReleased(final int button) {
        return Real4D.INSTANCE.window.mouse(button) == GLFW_RELEASE;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getDeltaMX() {
        return deltaMX;
    }

    public int getDeltaMY() {
        return deltaMY;
    }
}
