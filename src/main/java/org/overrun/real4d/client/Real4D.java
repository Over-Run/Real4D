package org.overrun.real4d.client;

import org.joml.Vector3i;
import org.overrun.glutils.GLUtils;
import org.overrun.glutils.game.Game;
import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.glutils.light.Direction;
import org.overrun.real4d.client.gui.render.TextRenderer;
import org.overrun.real4d.client.gui.screen.PausingScreen;
import org.overrun.real4d.client.world.Skybox;
import org.overrun.real4d.client.world.chunk.Chunk;
import org.overrun.real4d.client.world.render.PlanetRenderer;
import org.overrun.real4d.world.HitResult;
import org.overrun.real4d.world.block.Blocks;
import org.overrun.real4d.world.entity.Human;
import org.overrun.real4d.world.entity.Player;
import org.overrun.real4d.world.planet.Planet;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

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
import static org.overrun.real4d.client.gui.render.TextRenderer.drawText;
import static org.overrun.real4d.client.world.render.PlanetRenderer.CHUNK_SIZE;

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
    private final String[] debugText = {
        "Facing: %s (Towards %s) (%f / %f)",
        "Client Light: %d"
    };
    private final Camera camera = new Camera();
    private final Vector3i hotBarVec = new Vector3i(-2, 0, 0);
    private final ArrayList<Human> humans = new ArrayList<>();
    private boolean debugging = false;
    public boolean enableFog = true;
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

        TextRenderer.loadFont();
        TextRenderer.buildLists();

        skybox = new Skybox();

        Blocks.register();
        SpriteAtlases.load();
        planet = new Planet(256, 64, 256);
        planetRenderer = new PlanetRenderer(planet);
        player = new Player(planet);
        window.setGrabbed(true);
        for (int i = 0; i < 10; i++) {
            var human = new Human(planet, 128, 0, 128);
            human.resetPos();
            humans.add(human);
        }
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

        glEnable(GL_CULL_FACE);
        var frustum = Frustum.getFrustum();
        planetRenderer.updateDirtyChunks(player);

        setupFog(0);
        if (enableFog) glEnable(GL_FOG);
        planetRenderer.render(0);
        for (var human : humans) {
            if (human.isLit() && frustum.isVisible(human.box)) {
                human.render(delta);
            }
        }

        setupFog(1);
        planetRenderer.render(1);
        for (var human : humans) {
            if (!human.isLit() && frustum.isVisible(human.box)) {
                human.render(delta);
            }
        }

        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        if (enableFog) glDisable(GL_FOG);

        if (hitResult != null) {
            glDisable(GL_ALPHA_TEST);
            planetRenderer.renderHit(hitResult);
            glEnable(GL_ALPHA_TEST);
        }

        skybox.render();

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

        var t = Tesselator.getInstance();

        int wc = width / 2;
        int hc = height / 2;

        glColor4f(1, 1, 1, 1);

        if (debugging) {
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
            var chunk = planetRenderer.getChunk(cx, cy, cz);
            String chunkPos;
            if (chunk != null && planet.inIndex(pos))
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
            drawText("Real4D " + VERSION + "\n"
                    + graphics.getFps() + " fps ("
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
        glEnable(GL_TEXTURE_2D);
        WIDGETS_ATLAS.bind();
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
        BLOCK_ATLAS.bind();
        for (int i = 0; i < player.hotBar.length; i++) {
            glPushMatrix();
            glTranslatef(4 + i * 20, 1, 20);
            glCallList(matHotBarBlock);
            t.init(GL_QUADS);
            player.hotBar[i].render(t, planet, 0, hotBarVec);
            t.draw();
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
        glPopMatrix();
    }

    @Override
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

    private void moveCameraToPlayer(float delta) {
        glTranslatef(0, 0, -0.3f);
        glRotatef(player.rot.x, 1, 0, 0);
        glRotatef(player.rot.y, 0, 1, 0);
        var pos = camera.pos.set(player.prevPos).lerp(player.pos, delta);
        float x = pos.x;
        float y = pos.y;
        float z = pos.z;
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
            fovy = 25f;
        }
        gluPerspective(fovy,
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
        var fovy = 90f;
        if (player.isRunning) {
            fovy += 5f;
        }
        if (input.keyPressed(GLFW_KEY_C)) {
            fovy = 25f;
        }
        gluPerspective(fovy,
            (float) bufFrame.width() / (float) bufFrame.height(),
            0.05f,
            1000.0f);
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
        Human.free();
        skybox.free();
        SpriteAtlases.free();
    }
}
