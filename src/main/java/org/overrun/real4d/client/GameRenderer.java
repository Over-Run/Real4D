package org.overrun.real4d.client;

import org.overrun.glutils.gl.GLProgram;
import org.overrun.glutils.gl.Vbo;
import org.overrun.real4d.client.gl.Mesh;
import org.overrun.real4d.world.entity.Player;

import static org.lwjgl.opengl.GL20.*;
import static org.overrun.glutils.FilesReader.lines;
import static org.overrun.real4d.asset.AssetManager.makePath;
import static org.overrun.real4d.client.gl.GLMatrix.*;
import static org.overrun.real4d.client.gl.GLStateMgr.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GameRenderer {
    protected final Real4D client;
    public final GLProgram blockProgram;
    public final GLProgram guiProgram;
    public final Mesh crossHair = new Mesh();
    public Camera attachCamera;
    public Player player;

    public GameRenderer(final Real4D client) {
        this.client = client;

        player = client.player;
        attachCamera = player.camera;

        blockProgram = new GLProgram();
        blockProgram.createVsh(lines(
            this,
            makePath("shaders/core/block.vert")
        ));
        blockProgram.createFsh(lines(
            this,
            makePath("shaders/core/block.frag")
        ));
        blockProgram.link();

        guiProgram = new GLProgram();
        guiProgram.createVsh(lines(
            this,
            makePath("shaders/core/gui.vert")
        ));
        guiProgram.createFsh(lines(
            this,
            makePath("shaders/core/gui.frag")
        ));
        guiProgram.link();

        crossHair.init();
        crossHair.ibo = new Vbo(GL_ELEMENT_ARRAY_BUFFER);
        crossHair.vao.bind();
        crossHair.vbo.bind();
        crossHair.vbo.data(new float[]{
            // 3 Vert / 4 Color
            -4, 0, 0, 1, 1, 1, 0.5f,
            -4, 1, 0, 1, 1, 1, 0.5f,
            5, 0, 0, 1, 1, 1, 0.5f,
            5, 1, 0, 1, 1, 1, 0.5f,
            0, -4, 0, 1, 1, 1, 0.5f,
            0, 0, 0, 1, 1, 1, 0.5f,
            1, -4, 0, 1, 1, 1, 0.5f,
            1, 0, 0, 1, 1, 1, 0.5f,
            0, 1, 0, 1, 1, 1, 0.5f,
            0, 5, 0, 1, 1, 1, 0.5f,
            1, 1, 0, 1, 1, 1, 0.5f,
            1, 5, 0, 1, 1, 1, 0.5f
        }, GL_STATIC_DRAW);
        crossHair.ibo.bind();
        crossHair.ibo.data(new int[]{
            0, 1, 2, 2, 1, 3,
            4, 5, 6, 6, 5, 7,
            8, 9, 10, 10, 9, 11
        }, GL_STATIC_DRAW);
        int stride = (3 + 4) * Float.BYTES;
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0,
            3,
            GL_FLOAT,
            false,
            stride,
            0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1,
            4,
            GL_FLOAT,
            false,
            stride,
            3 * Float.BYTES);
        crossHair.vbo.unbind();
        crossHair.vao.unbind();
    }

    public void render(float delta) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        setupCamera(delta);

        glEnable(GL_CULL_FACE);
        client.planetRenderer.updateDirtyChunks(player);
        blockProgram.bind();
        blockProgram.setUniform("texture_sampler", 0);
        blockProgram.setUniformMat4("proj", proj);
        blockProgram.setUniformMat4("modelView", modelView);
        client.planetRenderer.render(0);
        blockProgram.unbind();

        final int screenWidth =
            client.framebuffer.width * 240 / client.framebuffer.height;
        // height * 240 / height
        final int screenHeight = 240;
        drawGui(screenWidth, screenHeight, delta);
    }

    private void drawGui(int width,
                         int height,
                         float delta) {
        glClear(GL_DEPTH_BUFFER_BIT);
        setMode(GL_PROJECTION);
        setOrtho(0, width, height, 0, 100, 300);
        setMode(GL_MODELVIEW);
        translation(0, 0, -200);

        int wc = width / 2;
        int hc = height / 2;

        guiProgram.bind();

        disableTexture2D();

        // Cross-hair
        pushMatrix();
        translate(wc, hc, 0);
        if (true) {
            disableAlphaTest();
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
            guiProgram.setUniform("texture2D_enabled", texture2DEnabled);
            guiProgram.setUniformMat4("proj", proj);
            guiProgram.setUniformMat4("modelView", modelView);
            crossHair.vao.bind();
            glDrawElements(GL_TRIANGLES, 18, GL_UNSIGNED_INT, 0);
            crossHair.vao.unbind();
            glDisable(GL_BLEND);
            enableAlphaTest();
        }
        popMatrix();

        guiProgram.unbind();
    }

    private void setupCamera(float delta) {
        setMode(GL_PROJECTION);
        setPerspectived(90,
            (float) client.framebuffer.width / (float) client.framebuffer.height,
            0.05f,
            1000.0f);
        setMode(GL_MODELVIEW);
        loadIdentity();
        attachCamera.moveToPlayer(player, delta);
    }

    public void free() {
        crossHair.free();
        blockProgram.free();
        guiProgram.free();
    }
}
