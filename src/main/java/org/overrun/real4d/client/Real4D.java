package org.overrun.real4d.client;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.overrun.glutils.GLUtils;
import org.overrun.glutils.timer.SystemTimer;
import org.overrun.glutils.wnd.GLFWindow;
import org.overrun.real4d.client.gl.GLStateMgr;
import org.overrun.real4d.client.input.Input;
import org.overrun.real4d.client.model.BlockModels;
import org.overrun.real4d.client.world.render.BlockRenderer;
import org.overrun.real4d.client.world.render.PlanetRenderer;
import org.overrun.real4d.internal.RandomSeed;
import org.overrun.real4d.world.block.Blocks;
import org.overrun.real4d.world.entity.Player;
import org.overrun.real4d.world.planet.Planet;

import static java.lang.Math.floor;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Real4D {
    private static final Real4D INSTANCE = new Real4D();
    public static final String VERSION = "0.1.0";
    public Planet planet;
    public Player player;
    public GLFWindow handle;
    public Framebuffer framebuffer;
    public GameRenderer gameRenderer;
    public BlockRenderer blockRenderer;
    public PlanetRenderer planetRenderer;
    public SystemTimer timer;
    public int fps;
    public Callback debugProc;

    public void init() {
        GLUtils.printLibInfo();
        final int cw = 854, ch = 480;
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        if (JVMArgs.isDebugging()) {
            glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);
        }
        handle = new GLFWindow(cw,
            ch,
            "Real4D " + VERSION);

        var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null) {
            handle.setPos((vidMode.width() - cw) >> 1,
                (vidMode.height() - ch) >> 1);
        }

        framebuffer = new Framebuffer();
        handle.makeCurr();
        handle.cursorPosCb((window, xp, yp) -> {
            var nxp = (int) floor(xp);
            var nyp = (int) floor(yp);
            Input.deltaMX = nxp - Input.mouseX;
            Input.deltaMY = nyp - Input.mouseY;
            cursorPosCb(nxp, nyp);
            Input.mouseX = nxp;
            Input.mouseY = nyp;
        });
        glfwSetFramebufferSizeCallback(
            handle.hWnd,
            (window, width, height) -> resize(width, height)
        );

        timer = new SystemTimer(20);

        GL.createCapabilities();
        resize(cw, ch);
        GLStateMgr.init();
        Blocks.register();
        SpriteAtlases.load();
        BlockModels.load();
        planet = new Planet(RandomSeed.seedUniquifier(),
            16,
            64,
            16);
        player = new Player(planet);
        blockRenderer = new BlockRenderer(this);
        gameRenderer = new GameRenderer(this);
        planetRenderer = new PlanetRenderer(this);

        if (JVMArgs.isDebugging()) {
            debugProc = GLUtil.setupDebugMessageCallback(System.err);
        }

        handle.setGrabbed(true);
        handle.show();
        long lastTime = System.currentTimeMillis();
        int frames = 0;
        while (!handle.shouldClose()) {
            timer.advanceTime();
            for (int j = 0; j < timer.ticks; j++) {
                tick();
            }
            render();
            handle.swapBuffers();
            glfwPollEvents();
            ++frames;
            while (System.currentTimeMillis() >= lastTime + 1000) {
                fps = frames;
                lastTime += 1000;
                frames = 0;
            }
        }
        free();
    }

    public void render() {
        gameRenderer.render((float) timer.delta);
    }

    public void tick() {
        planet.tick();
        player.tick();
    }

    public void cursorPosCb(int x, int y) {
        if (handle.isGrabbed()) {
            float xo = Input.getDeltaMX();
            float yo = Input.getDeltaMY();
            player.turn(xo, yo);
        }
    }

    public void resize(int width,
                       int height) {
        glViewport(0, 0, width, height);
        framebuffer.width = width;
        framebuffer.height = height;
    }

    public void free() {
        SpriteAtlases.free();
        planetRenderer.free();
        gameRenderer.free();
        if (debugProc != null) {
            debugProc.free();
        }
        handle.free();
    }

    public static Real4D getInstance() {
        return INSTANCE;
    }
}
