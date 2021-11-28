package org.overrun.real4d.client;

import org.joml.Matrix4fStack;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.overrun.glutils.wnd.Framebuffer;
import org.overrun.glutils.wnd.GLFWindow;
import org.overrun.real4d.Timer;
import org.overrun.real4d.client.input.Keyboard;
import org.overrun.real4d.client.input.Mouse;
import org.overrun.real4d.client.renderer.PlanetRenderer;
import org.overrun.real4d.universe.planet.Planet;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public enum Real4D implements AutoCloseable {
    INSTANCE;

    public static final String VERSION = "0.1.0";
    public GLFWindow window;
    public Framebuffer framebuffer;
    public Timer timer;
    public int fps;
    private PlanetRenderer planetRenderer;
    private Planet planet;
    private final Matrix4fStack proj = new Matrix4fStack(2);
    private final Matrix4fStack modelView = new Matrix4fStack(32);

    /**
     * Create GL context
     */
    public void create() {
        GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new RuntimeException("Can't init GLFW!");
        }
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        window = new GLFWindow(854,
            480,
            "Real4D " + VERSION);
        window.keyCb((hWnd, key, scancode, action, mods) -> {
            Keyboard.update(key, action);
            if (action == GLFW_RELEASE) {
                if (key == GLFW_KEY_ESCAPE) {
                    window.closeWindow();
                }
            }
        });
        window.cursorPosCb((hWnd, x, y) -> {
            window.mouseX = (int) x;
            window.mouseY = (int) y;
        });
        framebuffer = new Framebuffer(this::resize, window);
        var mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (mode != null) {
            window.setPos((int) ((mode.width() - 854) / 2.0),
                (int) ((mode.height() - 480) / 2.0));
        }
        window.makeCurr();
        GL.createCapabilities(true);
        glClearColor(0, 0, 0, 1);
    }

    /**
     * Init game objects
     */
    public void init() throws Exception {
        planet = new Planet(256, 64, 256);
        planetRenderer = new PlanetRenderer(planet);
        planetRenderer.init();
        timer = new Timer(20);
        window.show();
    }

    public void render() {
        float xo = (float) Mouse.getDX();
        float yo = (float) Mouse.getDY();
        planet.player.turn(xo, yo);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        planetRenderer.render(timer.delta, proj, modelView);
        window.swapBuffers();
    }

    public void tick() {
        planet.tick();
    }

    private void resize(long hWnd, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void close() {
        planetRenderer.free();
        window.free();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
