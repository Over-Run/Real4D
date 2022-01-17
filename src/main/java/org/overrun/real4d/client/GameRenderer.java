package org.overrun.real4d.client;

import org.joml.Vector3i;
import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.glutils.light.Direction;
import org.overrun.real4d.client.gl.GLMatrix;
import org.overrun.real4d.client.world.Skybox;
import org.overrun.real4d.client.world.chunk.Chunk;
import org.overrun.real4d.world.HitResult;
import org.overrun.real4d.world.entity.Player;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.system.MemoryUtil.*;
import static org.overrun.glutils.gl.ll.GLU.gluPerspective;
import static org.overrun.glutils.gl.ll.GLU.gluPickMatrix;
import static org.overrun.real4d.client.SpriteAtlases.BLOCK_ATLAS;
import static org.overrun.real4d.client.SpriteAtlases.WIDGETS_ATLAS;
import static org.overrun.real4d.client.gl.GLStateMgr.*;
import static org.overrun.real4d.client.gl.GLStateMgr.bindTexture2D;
import static org.overrun.real4d.client.gui.DrawableHelper.draw;
import static org.overrun.real4d.client.gui.Widgets.HOT_BAR;
import static org.overrun.real4d.client.gui.Widgets.HOT_BAR_SELECT;
import static org.overrun.real4d.client.gui.render.TextRenderer.drawText;
import static org.overrun.real4d.client.world.render.PlanetRenderer.CHUNK_SIZE;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GameRenderer {
    public Real4D client;
    private final IntBuffer viewportBuffer = memAllocInt(16);
    private final IntBuffer selectBuffer = memAllocInt(2000);
    private final FloatBuffer lightingBuffer = memAllocFloat(16);
    private final float[] fogColor0 = {0xfe / 255f, 0xfb / 255f, 0xfa / 255f, 1};
    private final float[] fogColor1 = {0x0e / 255f, 0x0b / 255f, 0x0a / 255f, 1};
    private final Vector3i hotBarVec = new Vector3i(-2, 0, 0);
    public Skybox skybox;
    public Camera attachCamera;
    public int matHotBarBlock;
    public Player player;

    public GameRenderer(final Real4D client) {
        this.client = client;
        player = client.player;
    }

    public void init() {
        matHotBarBlock = glGenLists(1);
        glNewList(matHotBarBlock, GL_COMPILE);
        glScalef(10, -10, 10);
        glRotatef(30, 1, 0, 0);
        glRotatef(45, 0, 1, 0);
        glTranslatef(2.5f, -2, -0.5f);
        glEndList();

        skybox = new Skybox();

        attachCamera = player.camera;
    }

    private void pick(float delta) {
        glSelectBuffer(selectBuffer.clear());
        glRenderMode(GL_SELECT);
        setupPickCamera(delta,
            client.framebuffer.width / 2.0f,
            client.framebuffer.height / 2.0f);
        client.planetRenderer.pick(player, Frustum.getFrustum());
        int hits = glRenderMode(GL_RENDER);
        selectBuffer.limit(selectBuffer.capacity());
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
            client.hitResult = new HitResult(names[1],
                names[2],
                names[3],
                Direction.getById(names[4]));
        } else {
            client.hitResult = null;
        }
    }

    public void render(float delta) {
        pick(delta);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        setupCamera(delta);

        glEnable(GL_CULL_FACE);
        var frustum = Frustum.getFrustum();
        client.planetRenderer.updateDirtyChunks(player);

        setupFog(0);
        if (client.enableFog) glEnable(GL_FOG);
        client.planetRenderer.render(0);
        for (var human : client.humans) {
            if (human.isLit() && frustum.isVisible(human.box)) {
                human.render(delta);
            }
        }

        setupFog(1);
        client.planetRenderer.render(1);
        for (var human : client.humans) {
            if (!human.isLit() && frustum.isVisible(human.box)) {
                human.render(delta);
            }
        }

        glDisable(GL_LIGHTING);
        disableTexture2D();
        if (client.enableFog) glDisable(GL_FOG);

        if (client.hitResult != null) {
            glDisable(GL_ALPHA_TEST);
            client.planetRenderer.renderHit(client.hitResult);
            glEnable(GL_ALPHA_TEST);
        }

        skybox.render();

        final int screenWidth =
            client.framebuffer.width * 240 / client.framebuffer.height;
        // height * 240 / height
        final int screenHeight = 240;
        drawGui(screenWidth, screenHeight, delta);

        if (client.screen != null) {
            client.screen.render();
        }
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

        GLMatrix.getProjection().setOrtho(0, width, height, 0, 100, 300);
        GLMatrix.getModelView().translation(0, 0, -200);

        var t = Tesselator.getInstance();

        int wc = width / 2;
        int hc = height / 2;

        glColor4f(1, 1, 1, 1);

        if (client.debugging) {
            glPushMatrix();
            glScalef(0.5f, 0.5f, 0);
            var px = player.pos.x;
            var py = player.pos.y;
            var pz = player.pos.z;
            var pos = player.getBlockPos();
            var pxi = pos.x();
            var pyi = pos.y();
            var pzi = pos.z();
            var cx = pxi / CHUNK_SIZE;
            var cy = pyi / CHUNK_SIZE;
            var cz = pzi / CHUNK_SIZE;
            var chunk = client.planetRenderer.getChunk(cx, cy, cz);
            String chunkPos;
            if (chunk != null && client.planet.inIndex(pos))
                chunkPos = "Chunk: "
                    + (pxi - chunk.x0) + " "
                    + (pyi - chunk.y0) + " "
                    + (pzi - chunk.z0) + " in "
                    + cx + " " + cy + " " + cz;
            else
                chunkPos = "Chunk: Outside of the Earth";
            glDisable(GL_ALPHA_TEST);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            drawText("Real4D " + Real4D.VERSION + "\n"
                    + client.framebuffer.fps + " fps ("
                    + Chunk.updates + " chunk updates)" + "\n \n"
                    + "XYZ: " + px + " / " + py + " / " + pz + "\n"
                    + "Block: " + pxi + " " + pyi + " " + pzi + "\n"
                    + chunkPos, 0, 0, 0,
                glyph -> {
                    glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
                    glBegin(GL_QUADS);
                    glVertex2f(glyph.x0, glyph.y0);
                    glVertex2f(glyph.x0, glyph.y1);
                    glVertex2f(glyph.x1, glyph.y1);
                    glVertex2f(glyph.x1, glyph.y0);
                    glEnd();
                    glColor3f(1, 1, 1);
                }
            );
            glDisable(GL_BLEND);
            glEnable(GL_ALPHA_TEST);
            glPopMatrix();
        }

        // Hot bar
        enableTexture2D();
        bindTexture2D(WIDGETS_ATLAS.getId());
        glPushMatrix();
        {
            final int w = 202;
            final int h = 22;
            glTranslatef(wc - w / 2f, height - h, 0);
            var s = player.select;
            var sx = s * 20 - 1;
            t.init(GL_QUADS);
            draw(t, WIDGETS_ATLAS, HOT_BAR, 0, 0, w, h);
            draw(t, WIDGETS_ATLAS, HOT_BAR_SELECT, sx, -2, 24, 24);
            t.draw();
        }
        bindTexture2D(BLOCK_ATLAS.getId());
        for (int i = 0; i < player.hotBar.length; i++) {
            glPushMatrix();
            glTranslatef(4 + i * 20, 1, 20);
            glCallList(matHotBarBlock);
            t.init(GL_QUADS);
            player.hotBar[i].render(t, client.planet, 0, hotBarVec);
            t.draw();
            glPopMatrix();
        }
        glPopMatrix();
        disableTexture2D();

        // Crossing
        glPushMatrix();
        glTranslatef(wc, hc, 0);
        if (!client.debugging) {
            glDisable(GL_ALPHA_TEST);
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
            glColor4f(1, 1, 1, 0.5f);
            t.init(GL_QUADS)
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
                .draw();
            glDisable(GL_BLEND);
            glEnable(GL_ALPHA_TEST);
        } else {
            glDisable(GL_CULL_FACE);
            glPushMatrix();
            glScalef(2, 2, 2);
            glRotatef(player.rot.x, -1, 0, 0);
            glRotatef(player.rot.y, 0, 1, 0);
            t.init(GL_LINES)
                .color(1, 0, 0)
                .vertex(0, 0, 0)
                .vertex(4, 0, 0)
                .color(0, 1, 0)
                .vertex(0, -4, 0)
                .vertex(0, 0, 0)
                .color(0, 0, 1)
                .vertex(0, 0, 0)
                .vertex(0, 0, 4)
                .draw();
            glPopMatrix();
            glEnable(GL_CULL_FACE);
        }
        glPopMatrix();
    }

    private void setupCamera(float delta) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        var fovy = 90f;
        if (client.input.keyPressed(GLFW_KEY_C)) {
            fovy = 25f;
        }
        if (player.isRunning) {
            fovy += 5f;
        }
        gluPerspective(fovy,
            (float) client.framebuffer.width / (float) client.framebuffer.height,
            0.05f,
            1000.0f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        attachCamera.moveToPlayer(player, delta);
    }

    private void setupPickCamera(float delta,
                                 float x,
                                 float y) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glGetIntegerv(GL_VIEWPORT, viewportBuffer.clear());
        gluPickMatrix(x, y, 5, 5, viewportBuffer.limit(16));
        var fovy = 90f;
        if (client.input.keyPressed(GLFW_KEY_C)) {
            fovy = 25f;
        }
        if (player.isRunning) {
            fovy += 5f;
        }
        gluPerspective(fovy,
            (float) client.framebuffer.width / (float) client.framebuffer.height,
            0.05f,
            1000.0f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        attachCamera.moveToPlayer(player, delta);
    }

    private void setupFog(int layer) {
        switch (layer) {
            case 0 -> {
                if (client.enableFog) {
                    glFogi(GL_FOG_MODE, GL_EXP);
                    glFogf(GL_FOG_DENSITY, 0.001f);
                    glFogfv(GL_FOG_COLOR, fogColor0);
                }
                glDisable(GL_LIGHTING);
            }
            case 1 -> {
                if (client.enableFog) {
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

    public void free() {
        Tesselator.getInstance().free();
        memFree(viewportBuffer);
        memFree(selectBuffer);
        memFree(lightingBuffer);
        skybox.free();
    }
}
