package org.overrun.real4d.client.world.render;

import org.overrun.glutils.tex.Textures;
import org.overrun.real4d.client.Frustum;
import org.overrun.real4d.client.Real4D;
import org.overrun.real4d.client.world.chunk.Chunk;
import org.overrun.real4d.client.world.chunk.DirtyChunkSorter;
import org.overrun.real4d.world.HitResult;
import org.overrun.real4d.world.entity.Player;
import org.overrun.real4d.world.planet.Planet;
import org.overrun.real4d.world.planet.PlanetListener;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.overrun.real4d.client.SpriteAtlases.BLOCK_ATLAS;
import static org.overrun.real4d.client.gl.GLStateMgr.bindTexture2D;

/**
 * @author squid233
 * @since 0.1.0
 */
public class PlanetRenderer implements PlanetListener {
    public static final int MAX_REBUILDS_PER_FRAME = 8;
    public static final int CHUNK_SIZE = 16;
    private final Real4D client;
    private final Planet planet;
    private final Chunk[] chunks;
    public final int xChunks;
    public final int yChunks;
    public final int zChunks;

    public PlanetRenderer(final Real4D client) {
        this.client = client;
        this.planet = client.planet;
        planet.addListener(this);
        xChunks = planet.width / CHUNK_SIZE;
        yChunks = planet.height / CHUNK_SIZE;
        zChunks = planet.depth / CHUNK_SIZE;
        chunks = new Chunk[xChunks * yChunks * zChunks];
        for (int x = 0; x < xChunks; x++) {
            for (int y = 0; y < yChunks; y++) {
                for (int z = 0; z < zChunks; z++) {
                    int x0 = x * CHUNK_SIZE;
                    int y0 = y * CHUNK_SIZE;
                    int z0 = z * CHUNK_SIZE;
                    int x1 = (x + 1) * CHUNK_SIZE;
                    int y1 = (y + 1) * CHUNK_SIZE;
                    int z1 = (z + 1) * CHUNK_SIZE;
                    if (x1 > planet.width) {
                        x1 = planet.width;
                    }
                    if (y1 > planet.height) {
                        y1 = planet.height;
                    }
                    if (z1 > planet.depth) {
                        z1 = planet.depth;
                    }
                    chunks[getIndex(x, y, z)] = new Chunk(
                        planet,
                        this,
                        x0,
                        y0,
                        z0,
                        x1,
                        y1,
                        z1);
                }
            }
        }
    }

    public ArrayList<Chunk> getAllDirtyChunks() {
        ArrayList<Chunk> dirty = null;
        for (var chunk : chunks) {
            if (chunk.isDirty()) {
                if (dirty == null) {
                    dirty = new ArrayList<>();
                }
                dirty.add(chunk);
            }
        }
        return dirty;
    }

    public void render(int layer) {
        Textures.active(0);
        bindTexture2D(BLOCK_ATLAS.getId());
        var frustum = Frustum.getFrustum();
        for (var chunk : chunks) {
            if (frustum.isVisible(chunk.aabb)) {
                chunk.render(layer);
            }
        }
        bindTexture2D(0);
    }

    public void updateDirtyChunks(Player player) {
        var dirty = getAllDirtyChunks();
        if (dirty != null) {
            dirty.sort(new DirtyChunkSorter(player, Frustum.getFrustum()));
            for (int i = 0; i < MAX_REBUILDS_PER_FRAME && i < dirty.size(); i++) {
                dirty.get(i).rebuild();
            }
        }
    }

    public void renderHit(HitResult h) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        client.blockRenderer.renderFace(
            h.face
        );
        glDisable(GL_BLEND);
    }

    public Chunk getChunk(int x, int y, int z) {
        return inIndex(x, y, z)
            ? chunks[getIndex(x, y, z)]
            : null;
    }

    public int getIndex(int x, int y, int z) {
        return (x + y * xChunks) * zChunks + z;
    }

    public boolean inIndex(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0
            && x < xChunks && y < yChunks && z < zChunks;
    }

    public void markDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
        x0 /= CHUNK_SIZE;
        x1 /= CHUNK_SIZE;
        y0 /= CHUNK_SIZE;
        y1 /= CHUNK_SIZE;
        z0 /= CHUNK_SIZE;
        z1 /= CHUNK_SIZE;

        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (z0 < 0) {
            z0 = 0;
        }
        if (x1 >= xChunks) {
            x1 = xChunks - 1;
        }
        if (y1 >= yChunks) {
            y1 = yChunks - 1;
        }
        if (z1 >= zChunks) {
            z1 = zChunks - 1;
        }

        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                for (int z = z0; z <= z1; ++z) {
                    chunks[getIndex(x, y, z)].markDirty();
                }
            }
        }
    }

    public void free() {
        for (var c : chunks) {
            c.free();
        }
    }

    @Override
    public void blockChanged(int x, int y, int z) {
        markDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    @Override
    public void lightColumnChanged(int x, int z, int y0, int y1) {
        markDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
    }

    @Override
    public void allChanged() {
        markDirty(0, 0, 0, planet.width, planet.height, planet.depth);
    }
}
