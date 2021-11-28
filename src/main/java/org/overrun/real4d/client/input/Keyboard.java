package org.overrun.real4d.client.input;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Keyboard {
    private static final Map<Integer, Integer> KEYS =
        new HashMap<>();

    public static boolean isKeyDown(int key) {
        return glfwGetKey(glfwGetCurrentContext(), key) == GLFW_PRESS;
    }

    public static boolean isKeyRelease(int key) {
        return glfwGetKey(glfwGetCurrentContext(), key) == GLFW_RELEASE;
    }

    public static boolean isKeyPressed(int key) {
        return KEYS.computeIfAbsent(key, k -> GLFW_RELEASE)
            == GLFW_PRESS;
    }

    public static boolean isKeyReleased(int key) {
        return KEYS.computeIfAbsent(key, k -> GLFW_RELEASE)
            == GLFW_RELEASE;
    }

    public static void update(int key, int action) {
        KEYS.put(key, action);
    }
}
