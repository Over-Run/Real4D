package org.overrun.real4d.client;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.overrun.glutils.GLUtils;
import org.overrun.glutils.timer.SystemTimer;
import org.overrun.glutils.wnd.GLFWindow;
import org.overrun.real4d.client.gl.Framebuffer;
import org.overrun.real4d.client.gl.GLStateMgr;
import org.overrun.real4d.client.gui.render.TextRenderer;
import org.overrun.real4d.client.gui.screen.PausingScreen;
import org.overrun.real4d.client.gui.screen.Screen;
import org.overrun.real4d.client.input.Input;
import org.overrun.real4d.client.world.chunk.Chunk;
import org.overrun.real4d.client.world.render.PlanetRenderer;
import org.overrun.real4d.world.HitResult;
import org.overrun.real4d.world.block.Blocks;
import org.overrun.real4d.world.entity.Human;
import org.overrun.real4d.world.entity.Player;
import org.overrun.real4d.world.planet.Planet;

import java.util.ArrayList;

import static java.lang.Math.floor;
import static java.lang.System.nanoTime;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.overrun.glutils.GLUtils.getLogger;
import static org.overrun.real4d.internal.RandomSeed.seedUniquifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Real4D {
    public static final String VERSION = "0.1.0";
    public static final Real4D INSTANCE = new Real4D();
    private final String[] debugText = {
        "Facing: %s (Towards %s) (%f / %f)",
        "Client Light: %d"
    };
    public final ArrayList<Human> humans = new ArrayList<>();
    public boolean debugging = false;
    public boolean enableFog = true;
    public Planet planet;
    public PlanetRenderer planetRenderer;
    public Player player;
    public GLFWindow window;
    public Input input;
    public Framebuffer framebuffer;
    public GameRenderer gameRenderer;
    public SystemTimer timer;
    public Screen screen;
    public HitResult hitResult;
    private int lastDestroyTick = 0;
    private int lastPlaceTick = 0;
    private boolean isPaused;
    private Callback debugProc;

    public void start() {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        final int cw = 854;
        final int ch = 480;
        window = new GLFWindow(cw,
            ch,
            "Real4D " + Real4D.VERSION);
        input = new Input();
        window.keyCb((hWnd, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                keyPressed(key, scancode, mods);
            }
        });
        window.cursorPosCb((hWnd, xp, yp) -> {
            var nxp = (int) floor(xp);
            var nyp = (int) floor(yp);
            input.deltaMX = nxp - input.mouseX;
            input.deltaMY = nyp - input.mouseY;
            cursorPosCb(nxp, nyp);
            input.mouseX = nxp;
            input.mouseY = nyp;
        });
        window.scrollCb((hWnd, xo, yo) -> mouseWheel(xo, yo));
        framebuffer = new Framebuffer();
        framebuffer.width = cw;
        framebuffer.height = ch;
        glfwSetFramebufferSizeCallback(
            window.hWnd,
            (hWnd, w, h) -> {
                resize(w, h);
                framebuffer.width = w;
                framebuffer.height = h;
            }
        );
        timer = new SystemTimer(20);
        window.makeCurr();
        glfwSwapInterval(0);
        GL.createCapabilities();
        try {
            init();
            resize(cw, ch);
            window.show();
            long lastTime = System.currentTimeMillis();
            int frames = 0;
            while (!window.shouldClose()) {
                timer.advanceTime();
                for (int j = 0; j < timer.getTicks(); j++) {
                    tick();
                }
                render();
                window.swapBuffers();
                glfwPollEvents();
                ++frames;
                while (System.currentTimeMillis() >= lastTime + 1000) {
                    framebuffer.fps = frames;
                    passedFrame();
                    lastTime += 1000;
                    frames = 0;
                }
            }
        } catch (Throwable t) {
            getLogger().catching(t);
        } finally {
            free();
            window.free();
            GL.setCapabilities(null);
            glfwTerminate();
            var cb = glfwSetErrorCallback(null);
            if (cb != null) {
                cb.free();
            }
        }
    }

    /**
     * Init game objects
     */
    public void init() {
        GLUtils.printLibInfo();
        var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null) {
            window.setPos(
                (vidMode.width() - framebuffer.width) >> 1,
                (vidMode.height() - framebuffer.height) >> 1
            );
        }

        if (JVMArgs.isDebugging()) {
            debugProc = GLUtil.setupDebugMessageCallback();
        }

        GLStateMgr.init();

        TextRenderer.loadFont();
        TextRenderer.buildLists();

        Blocks.register();
        SpriteAtlases.load();
        planet = new Planet(seedUniquifier() ^ nanoTime(),
            256,
            64,
            256);
        planetRenderer = new PlanetRenderer(planet);
        player = new Player(planet);
        gameRenderer = new GameRenderer(this);
        gameRenderer.init();
        window.setGrabbed(true);
        for (int i = 0; i < 10; i++) {
            var human = new Human(planet, 128, 0, 128);
            human.resetPos();
            humans.add(human);
        }
    }

    public void render() {
        gameRenderer.render((float) timer.delta);
    }

    public void tick() {
        if (lastDestroyTick > 0) --lastDestroyTick;
        if (lastPlaceTick > 0) --lastPlaceTick;
        if (hitResult != null) {
            if (lastDestroyTick == 0
                && input.mousePressed(GLFW_MOUSE_BUTTON_LEFT)) {
                boolean changed = planet.setBlock(hitResult.pos,
                    Blocks.AIR);
                if (changed) {
                    lastDestroyTick = 3;
                }
            }
            if (lastPlaceTick == 0
                && input.mousePressed(GLFW_MOUSE_BUTTON_RIGHT)) {
                var pos = hitResult.face.toVector().add(hitResult.pos);
                boolean changed = planet.getBlock(pos).isAir()
                    && planet.setBlock(pos, player.hotBar[player.select]);
                if (changed) {
                    lastPlaceTick = 3;
                }
            }
        }
        if (input.keyPressed(GLFW_KEY_G)) {
            var p = player.pos;
            humans.add(new Human(planet, p.x, p.y, p.z));
        }
        planet.tick();
        for (int i = 0; i < humans.size(); i++) {
            var human = humans.get(i);
            human.tick();
            if (human.removed) {
                humans.remove(i--);
            }
        }
        player.tick();
    }

    public void passedFrame() {
        Chunk.updates = 0;
    }

    public void cursorPosCb(int x, int y) {
        if (window.isGrabbed()) {
            float xo = input.getDeltaMX();
            float yo = input.getDeltaMY();
            player.turn(xo, yo);
        }
        if (screen != null) {
            screen.cursorPosCb(x, y);
        }
    }

    public void keyPressed(int key, int scancode, int mods) {
        switch (key) {
            case GLFW_KEY_ESCAPE -> {
                isPaused = !isPaused;
                window.setGrabbed(!isPaused);
                timer.setTimeScale(isPaused ? 0 : 1);
                if (isPaused) {
                    openScreen(new PausingScreen(null));
                } else {
                    openScreen(null);
                }
            }
            case GLFW_KEY_F3 -> debugging = !debugging;
            case GLFW_KEY_0 -> player.select = 9;
            case GLFW_KEY_1 -> player.select = 0;
            case GLFW_KEY_2 -> player.select = 1;
            case GLFW_KEY_3 -> player.select = 2;
            case GLFW_KEY_4 -> player.select = 3;
            case GLFW_KEY_5 -> player.select = 4;
            case GLFW_KEY_6 -> player.select = 5;
            case GLFW_KEY_7 -> player.select = 6;
            case GLFW_KEY_8 -> player.select = 7;
            case GLFW_KEY_9 -> player.select = 8;
        }
        if (screen != null) {
            screen.keyPressed(key, scancode, mods);
        }
    }

    public void mouseWheel(double xo, double yo) {
        player.mouseWheel((int) xo, (int) yo);
        if (screen != null) {
            screen.mouseWheel(xo, yo);
        }
    }

    public void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    public void openScreen(Screen s) {
        if (screen != null) {
            screen.close();
        }
        screen = s;
        if (s != null) {
            s.init(this,
                framebuffer.width,
                framebuffer.height);
        }
    }

    public void free() {
        if (debugProc != null) {
            debugProc.free();
        }
        GLStateMgr.free();
        TextRenderer.free();
        gameRenderer.free();
        Human.free();
        SpriteAtlases.free();
    }
}
