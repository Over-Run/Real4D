package org.overrun.real4d.client;

import org.overrun.glutils.GLUtils;
import org.overrun.glutils.game.Game;
import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.glutils.light.Direction;
import org.overrun.real4d.client.gui.screen.PausingScreen;
import org.overrun.real4d.client.world.Skybox;
import org.overrun.real4d.client.world.chunk.Chunk;
import org.overrun.real4d.client.world.render.PlanetRenderer;
import org.overrun.real4d.world.HitResult;
import org.overrun.real4d.world.block.Blocks;
import org.overrun.real4d.world.entity.Player;
import org.overrun.real4d.world.planet.Planet;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.overrun.glutils.game.GameEngine.*;
import static org.overrun.glutils.gl.ll.GLU.gluPerspective;
import static org.overrun.glutils.gl.ll.GLU.gluPickMatrix;
import static org.overrun.real4d.client.SpriteAtlases.BLOCK_ATLAS;
import static org.overrun.real4d.client.SpriteAtlases.WIDGETS_ATLAS;
import static org.overrun.real4d.client.gui.DrawableHelper.draw;
import static org.overrun.real4d.client.gui.Widgets.HOT_BAR;
import static org.overrun.real4d.client.gui.Widgets.HOT_BAR_SELECT;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Real4D extends Game {
    public static final String VERSION = "0.1.0";
    public static final Real4D INSTANCE = new Real4D();
    private final IntBuffer viewportBuffer = memAllocInt(16);
    private final IntBuffer selectBuffer = memAllocInt(2000);
    private final FloatBuffer lightingBuffer = memAllocFloat(16);
    private final float[] fogColor0 = {0xfe / 255f, 0xfb / 255f, 0xfa / 255f, 1};
    private final float[] fogColor1 = {0x0e / 255f, 0x0b / 255f, 0x0a / 255f, 1};
    public boolean enableFog = false;
    private Skybox skybox;
    private Planet planet;
    private PlanetRenderer planetRenderer;
    private Player player;
    private HitResult hitResult;
    private int lastDestroyTick = 0;
    private int lastPlaceTick = 0;
    private int matHotBarBlock;
    private boolean isPaused;

    /**
     * Init game objects
     * {@inheritDoc}
     */
    @Override
    public void create() {
        GLUtils.printLibInfo();
        var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null) {
            window.setPos(
                (vidMode.width() - bufFrame.width()) >> 1,
                (vidMode.height() - bufFrame.height()) >> 1
            );
        }

        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glClearColor(0.4f, 0.6f, 0.9f, 0.0f);
        glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.5f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);

        matHotBarBlock = glGenLists(1);
        glNewList(matHotBarBlock, GL_COMPILE);
        glScalef(10, -10, 10);
        glRotatef(30, 1, 0, 0);
        glRotatef(45, 0, 1, 0);
        glTranslatef(2.5f, -2, -0.5f);
        glEndList();

        skybox = new Skybox();

        Blocks.register();
        SpriteAtlases.load();
        planet = new Planet(256, 64, 256);
        planetRenderer = new PlanetRenderer(planet);
        player = new Player(planet);
        window.setGrabbed(true);
    }

    private void pick(float delta) {
        glSelectBuffer(selectBuffer.clear());
        glRenderMode(GL_SELECT);
        setupPickCamera(delta, bufFrame.width() / 2.0f, bufFrame.height() / 2.0f);
        planetRenderer.pick(player, Frustum.getFrustum());
        int hits = glRenderMode(GL_RENDER);
        selectBuffer.flip().limit(selectBuffer.capacity());
        long closest = 0;
        var names = new int[10];
        int hitNameCount = 0;
        for (int i = 0; i < hits; i++) {
            int nameCount = selectBuffer.get();
            long minZ = selectBuffer.get();
            selectBuffer.get();
            int j;
            if (minZ >= closest && i != 0) {
                for (j = 0; j < nameCount; ++j) {
                    selectBuffer.get();
                }
            } else {
                closest = minZ;
                hitNameCount = nameCount;
                for (j = 0; j < nameCount; ++j) {
                    names[j] = selectBuffer.get();
                }
            }
        }
        if (hitNameCount > 0) {
            hitResult = new HitResult(names[1],
                names[2],
                names[3],
                Direction.getById(names[4]));
        } else {
            hitResult = null;
        }
    }

    @Override
    public void render() {
        var delta = (float) timer.getDelta();
        pick(delta);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        setupCamera(delta);
        skybox.render(0, 0, 0, 1, 0.5f, 1);
        glEnable(GL_CULL_FACE);
        planetRenderer.updateDirtyChunks(player);
        setupFog(0);
        if (enableFog) glEnable(GL_FOG);
        planetRenderer.render(0);
        setupFog(1);
        planetRenderer.render(1);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        if (enableFog) glDisable(GL_FOG);
        if (hitResult != null) {
            glDisable(GL_ALPHA_TEST);
            planetRenderer.renderHit(hitResult);
            glEnable(GL_ALPHA_TEST);
        }
        final int screenWidth = bufFrame.width() * 240 / bufFrame.height();
        // height * 240 / height
        final int screenHeight = 240;
        drawGui(screenWidth, screenHeight, delta);
        super.render();
    }

    private void drawGui(int width,
                         int height,
                         float delta) {
        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, 100, 300);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0, 0, -200);

        Tesselator t = Tesselator.getInstance();

        int wc = width / 2;
        int hc = height / 2;

        glColor4f(1, 1, 1, 1);
        // Hot bar
        glEnable(GL_TEXTURE_2D);
        WIDGETS_ATLAS.bind();
        glPushMatrix();
        {
            final int w = 202;
            final int h = 22;
            glTranslatef(wc - w / 2f, height - h, 0);
            var s = player.select;
            var sx = s * 20 - 1;
            t.init();
            draw(t, WIDGETS_ATLAS, HOT_BAR, 0, 0, w, h);
            draw(t, WIDGETS_ATLAS, HOT_BAR_SELECT, sx, -2, 24, 24);
            t.draw(GL_QUADS);
        }
        BLOCK_ATLAS.bind();
        for (int i = 0; i < player.hotBar.length; i++) {
            glPushMatrix();
            glTranslatef(4 + i * 20, 1, 20);
            glCallList(matHotBarBlock);
            t.init();
            player.hotBar[i].render(t, planet, 0, -2, 0, 0);
            t.draw(GL_QUADS);
            glPopMatrix();
        }
        glPopMatrix();
        glDisable(GL_TEXTURE_2D);

        // Crossing
        glPushMatrix();
        glTranslatef(wc, hc, 0);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
        glColor4f(1, 1, 1, 0.5f);
        t.init()
            .vertex(1, -4, 0)
            .vertex(0, -4, 0)
            .vertex(0, 5, 0)
            .vertex(1, 5, 0)
            .vertex(5, 0, 0)
            .vertex(1, 0, 0)
            .vertex(1, 1, 0)
            .vertex(5, 1, 0)
            .vertex(0, 0, 0)
            .vertex(-4, 0, 0)
            .vertex(-4, 1, 0)
            .vertex(0, 1, 0)
            .draw(GL_QUADS);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glPopMatrix();
    }

    @Override
    public void tick() {
        if (lastDestroyTick > 0) --lastDestroyTick;
        if (lastPlaceTick > 0) --lastPlaceTick;
        if (hitResult != null) {
            if (lastDestroyTick == 0
                && input.mousePressed(GLFW_MOUSE_BUTTON_LEFT)) {
                boolean changed = planet.setBlock(hitResult.x,
                    hitResult.y,
                    hitResult.z,
                    Blocks.AIR);
                if (changed) {
                    lastDestroyTick = 3;
                }
            }
            if (lastPlaceTick == 0
                && input.mousePressed(GLFW_MOUSE_BUTTON_RIGHT)) {
                var face = hitResult.face;
                int x = hitResult.x += face.getAxisX();
                int y = hitResult.y += face.getAxisY();
                int z = hitResult.z += face.getAxisZ();
                boolean changed = planet.getBlock(x, y, z).isAir()
                    && planet.setBlock(x, y, z, player.hotBar[player.select]);
                if (changed) {
                    lastPlaceTick = 3;
                }
            }
        }
        planet.tick();
        player.tick();
    }

    private void moveCameraToPlayer(float delta) {
        glTranslatef(0, 0, -0.3f);
        glRotatef(player.xRot, 1, 0, 0);
        glRotatef(player.yRot, 0, 1, 0);
        float x = player.prevX + (player.x - player.prevX) * delta;
        float y = player.prevY + (player.y - player.prevY) * delta + player.eyeHeight;
        float z = player.prevZ + (player.z - player.prevZ) * delta;
        glTranslatef(-x, -y, -z);
    }

    private void setupCamera(float delta) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        var fovy = 90f;
        if (player.isRunning) {
            fovy += 5f;
        }
        if (input.keyPressed(GLFW_KEY_C)) {
            fovy -= 25f;
        }
        gluPerspective(fovy,
            (float) bufFrame.width() / (float) bufFrame.height(),
            0.05f,
            4000.0f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        moveCameraToPlayer(delta);
    }

    private void setupPickCamera(float delta,
                                 float x,
                                 float y) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glGetIntegerv(GL_VIEWPORT, viewportBuffer.clear());
        gluPickMatrix(x, y, 5, 5, viewportBuffer.flip().limit(16));
        var fovy = 90f;
        if (player.isRunning) {
            fovy += 5f;
        }
        if (input.keyPressed(GLFW_KEY_C)) {
            fovy -= 25f;
        }
        gluPerspective(fovy,
            (float) bufFrame.width() / (float) bufFrame.height(),
            0.05f,
            4000.0f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        moveCameraToPlayer(delta);
    }

    private void setupFog(int layer) {
        switch (layer) {
            case 0 -> {
                if (enableFog) {
                    glFogi(GL_FOG_MODE, GL_EXP);
                    glFogf(GL_FOG_DENSITY, 0.001f);
                    glFogfv(GL_FOG_COLOR, fogColor0);
                }
                glDisable(GL_LIGHTING);
            }
            case 1 -> {
                if (enableFog) {
                    glFogi(GL_FOG_MODE, GL_EXP);
                    glFogf(GL_FOG_DENSITY, 0.06f);
                    glFogfv(GL_FOG_COLOR, fogColor1);
                }
                glEnable(GL_LIGHTING);
                glEnable(GL_COLOR_MATERIAL);
                // Brightness
                float br = 0.6f;
                glLightModelfv(GL_LIGHT_MODEL_AMBIENT, getLightingBuffer(br, br, br, 1));
            }
        }
    }

    private FloatBuffer getLightingBuffer(float r,
                                          float g,
                                          float b,
                                          float a) {
        return lightingBuffer.clear()
            .put(r).put(g).put(b).put(a)
            .flip();
    }

    @Override
    public void passedFrame() {
        Chunk.updates = 0;
    }

    @Override
    public void cursorPosCb(int x, int y) {
        if (window.isGrabbed()) {
            float xo = input.getDeltaMX();
            float yo = input.getDeltaMY();
            player.turn(xo, yo);
        }
        super.cursorPosCb(x, y);
    }

    @Override
    public void keyPressed(int key, int scancode, int mods) {
        if (key == GLFW_KEY_ESCAPE) {
            isPaused = !isPaused;
            window.setGrabbed(!isPaused);
            timer.setTimeScale(isPaused ? 0 : 1);
            if (isPaused) {
                openScreen(new PausingScreen(null));
            } else {
                openScreen(null);
            }
        }
        switch (key) {
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
        super.keyPressed(key, scancode, mods);
    }

    @Override
    public void mouseWheel(double xo, double yo) {
        player.mouseWheel((int) xo, (int) yo);
        super.mouseWheel(xo, yo);
    }

    @Override
    public void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void free() {
        memFree(viewportBuffer);
        memFree(selectBuffer);
        memFree(lightingBuffer);
        skybox.free();
        SpriteAtlases.free();
    }
}
