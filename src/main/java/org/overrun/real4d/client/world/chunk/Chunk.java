package org.overrun.real4d.client.world.chunk;

import org.joml.Vector3i;
import org.overrun.glutils.gl.Vao;
import org.overrun.glutils.gl.Vbo;
import org.overrun.glutils.gl.VertexAttrib;
import org.overrun.glutils.tex.Textures;
import org.overrun.real4d.client.Real4D;
import org.overrun.real4d.client.world.render.BlockRenderer;
import org.overrun.real4d.client.world.render.PlanetRenderer;
import org.overrun.real4d.world.entity.Player;
import org.overrun.real4d.world.phys.AABBox;
import org.overrun.real4d.world.planet.Planet;

import static java.lang.Math.floor;
import static org.lwjgl.opengl.GL15.*;
import static org.overrun.real4d.client.SpriteAtlases.BLOCK_ATLAS;
import static org.overrun.real4d.client.gl.GLStateMgr.bindTexture2D;
import static org.overrun.real4d.util.ArrayUtil.append;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Chunk {
    public final Real4D client = Real4D.getInstance();
    public final AABBox aabb;
    public final Planet planet;
    public final PlanetRenderer planetRenderer;
    public final Vao vao0, vao1;
    public final Vbo vbo0, vbo1;
    public int layer0, layer1;
    public final VertexAttrib aPos = new VertexAttrib(0);
    public final VertexAttrib aTexCoords = new VertexAttrib(1);
    public final int x0;
    public final int y0;
    public final int z0;
    public final int x1;
    public final int y1;
    public final int z1;
    public final float x;
    public final float y;
    public final float z;
    private boolean dirty = true;
    public long dirtiedTime = 0L;
    public static int updates = 0;
    private static long totalTime = 0L;
    private static int totalUpdates = 0;

    public Chunk(Planet planet,
                 PlanetRenderer planetRenderer,
                 int x0,
                 int y0,
                 int z0,
                 int x1,
                 int y1,
                 int z1) {
        this.planet = planet;
        this.planetRenderer = planetRenderer;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        x = (float) (x0 + x1) / 2.0f;
        y = (float) (y0 + y1) / 2.0f;
        z = (float) (z0 + z1) / 2.0f;
        aabb = new AABBox(x0, y0, z0, x1, y1, z1);
        vao0 = new Vao();
        vao0.bind();
        vao1 = new Vao();
        vao1.unbind();
        vbo0 = new Vbo(GL_ARRAY_BUFFER);
        vbo0.bind();
        vbo1 = new Vbo(GL_ARRAY_BUFFER);
        vbo1.bind();
        vbo1.unbind();
    }

    private void rebuild(int layer) {
        dirty = false;
        ++updates;
        long before = System.nanoTime();

        float[] fb = {};
        int blocks = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    var block = planet.getBlock(x, y, z);
                    if (!block.isAir()) {
                        fb = append(fb,
                            BlockRenderer.getData(planet,
                                block,
                                x,
                                y,
                                z,
                                layer));
                        ++blocks;
                    }
                }
            }
        }
        Textures.active(0);
        bindTexture2D(BLOCK_ATLAS.getId());
        Vao vao;
        Vbo vbo;
        if (layer == 0) {
            vao = vao0;
            vbo = vbo0;
            layer0 = fb.length;
        } else {
            vao = vao1;
            vbo = vbo1;
            layer1 = fb.length;
        }
        vao.bind();
        vbo.bind();
        vbo.data(fb, GL_STREAM_DRAW);
        int stride = (3 + 2) * Float.BYTES;
        aPos.enable();
        aPos.pointer(3,
            GL_FLOAT,
            false,
            stride,
            0);
        aTexCoords.enable();
        aTexCoords.pointer(2,
            GL_FLOAT,
            false,
            stride,
            3 * Float.BYTES);
        vbo.unbind();
        vao.unbind();
        bindTexture2D(0);

        long after = System.nanoTime();
        if (blocks > 0) {
            totalTime += after - before;
            ++totalUpdates;
        }
    }

    public void rebuild() {
        rebuild(0);
        rebuild(1);
    }

    public void render(int layer) {
        var vao = layer == 0 ? vao0 : vao1;
        vao.bind();
        glDrawArrays(GL_TRIANGLES,
            0,
            layer == 0 ? layer0 : layer1);
        vao.unbind();
    }

    public void markDirty() {
        if (!dirty) {
            dirtiedTime = System.currentTimeMillis();
        }
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public Vector3i getBlockPos() {
        return new Vector3i(
            (int) floor(x),
            (int) floor(y),
            (int) floor(z));
    }

    public float distanceToSqr(Player player) {
        return player.pos.distanceSquared(x, y, z);
    }

    public void free() {
        vao0.free();
        vao1.free();
        vbo0.free();
        vbo1.free();
    }
}
