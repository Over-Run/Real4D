package org.overrun.real4d.client.main;

import org.overrun.real4d.client.Real4D;
import org.overrun.real4d.client.input.Mouse;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.system.MemoryUtil.memAllocDouble;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Main {
    public static void main(String[] args) throws Exception {
        try (var game = Real4D.INSTANCE) {
            game.create();
            game.init();
            var mx = memAllocDouble(1);
            var my = memAllocDouble(1);
            long lastTime = System.currentTimeMillis();
            int frames = 0;
            while (!game.window.shouldClose()) {
                game.timer.advanceTime();
                for (int i = 0; i < game.timer.ticks; i++) {
                    game.tick();
                }
                game.render();
                glfwGetCursorPos(game.window.hWnd, mx, my);
                Mouse.update(mx.get(0), my.get(0));
                glfwPollEvents();
                ++frames;
                while (System.currentTimeMillis() >= lastTime + 1000) {
                    game.fps = frames;
                    lastTime += 1000;
                    frames = 0;
                }
            }
        }
    }
}
