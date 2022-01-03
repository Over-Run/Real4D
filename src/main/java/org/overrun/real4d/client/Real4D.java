package org.overrun.real4d.client;

import org.overrun.glutils.game.Game;
import org.overrun.glutils.game.Texture2D;
import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.glutils.tex.TexParam;
import org.overrun.real4d.client.world.renderer.PlanetRenderer;
import org.overrun.real4d.world.entity.Player;
import org.overrun.real4d.world.planet.HitResult;
import org.overrun.real4d.world.planet.Planet;
import org.overrun.real4d.world.planet.block.Blocks;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.overrun.glutils.game.GameEngine.*;
import static org.overrun.glutils.gl.ll.GLU.gluPerspective;
import static org.overrun.glutils.gl.ll.GLU.gluPickMatrix;
import static org.overrun.real4d.client.Frustum.*;

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
    private Planet planet;
    private PlanetRenderer planetRenderer;
    private Player player;
    private HitResult hitResult;
    private Texture2D widgets;
    private int lastDestroyTick = 0;
    private int lastPlaceTick = 0;

    /**
     * Init game objects
     * {@inheritDoc}
     */
    @Override
    public void create() {
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

        Blocks.register();
        SpriteAtlases.load();
        widgets = new Texture2D(Real4D.class,
            "assets.real4d/textures/gui/widgets.png",
            TexParam.glNearest());
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
            hitResult = new HitResult(names[1], names[2], names[3], names[4]);
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
        glEnable(GL_CULL_FACE);
        planetRenderer.updateDirtyChunks(player);
        setupFog(0);
        planetRenderer.render(0);
        setupFog(1);
        planetRenderer.render(1);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        if (hitResult != null) {
            glDisable(GL_ALPHA_TEST);
            planetRenderer.renderHit(hitResult);
            glEnable(GL_ALPHA_TEST);
        }
        drawGui(bufFrame.width(), bufFrame.height(), delta);
    }

    private void drawGui(int width,
                         int height,
                         float delta) {
        final int screenWidth = width * 240 / height;
        // height * 240 / height
        final int screenHeight = 240;
        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, screenWidth, screenHeight, 0, 100, 300);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0F, 0.0F, -200);

        Tesselator t = Tesselator.getInstance();

        int wc = screenWidth / 2;
        int hc = screenHeight / 2;

        glColor4f(1, 1, 1, 1);
        // Hot bar
        glEnable(GL_TEXTURE_2D);
        widgets.bind();
        glPushMatrix();
        {
            final int w = 182;
            final int h = 22;
            glTranslatef(wc - w / 2f, screenHeight - 1 - h, 0);
            var u0 = 0f;
            var u1 = 182 / 256f;
            var v0 = 0f;
            var v1 = 22 / 256f;
            t.init()
                .vertexUV(0, 0, 0, u0, v0)
                .vertexUV(0, h, 0, u0, v1)
                .vertexUV(w, h, 0, u1, v1)
                .vertexUV(w, 0, 0, u1, v0)
                .draw(GL_QUADS);
        }
        glPopMatrix();
        glDisable(GL_TEXTURE_2D);

        // Crossing
        glPushMatrix();
        glTranslatef(wc, hc, 0);
        t.init()
            .vertex(1, -4, 0)
            .vertex(0, -4, 0)
            .vertex(0, 5, 0)
            .vertex(1, 5, 0)
            .vertex(5, 0, 0)
            .vertex(-4, 0, 0)
            .vertex(-4, 1, 0)
            .vertex(5, 1, 0)
            .draw(GL_QUADS);
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
                int x = hitResult.x;
                int y = hitResult.y;
                int z = hitResult.z;
                switch (hitResult.face) {
                    case LEFT -> --x;
                    case RIGHT -> ++x;
                    case BOTTOM -> --y;
                    case TOP -> ++y;
                    case BACK -> --z;
                    case FRONT -> ++z;
                }
                boolean changed = planet.setBlock(x,
                    y,
                    z,
                    Blocks.STONE);
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
        gluPerspective(90,
            (float) bufFrame.width() / (float) bufFrame.height(),
            0.05f,
            1000.0f);
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
        gluPerspective(90,
            (float) bufFrame.width() / (float) bufFrame.height(),
            0.05f,
            1000.0f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        moveCameraToPlayer(delta);
    }

    private void setupFog(int layer) {
        switch (layer) {
            case 0 -> glDisable(GL_LIGHTING);
            case 1 -> {
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
            window.close();
        }
        super.keyPressed(key, scancode, mods);
    }

    @Override
    public void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void free() {
        widgets.free();
        memFree(viewportBuffer);
        memFree(selectBuffer);
        memFree(lightingBuffer);
        SpriteAtlases.free();
    }
}
