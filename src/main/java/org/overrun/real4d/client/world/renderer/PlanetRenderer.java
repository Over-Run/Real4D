package org.overrun.real4d.client.world.renderer;

import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.real4d.client.Frustum;
import org.overrun.real4d.client.world.chunk.Chunk;
import org.overrun.real4d.world.entity.Player;
import org.overrun.real4d.world.planet.DirtyChunkSorter;
import org.overrun.real4d.world.planet.HitResult;
import org.overrun.real4d.world.planet.Planet;
import org.overrun.real4d.world.planet.PlanetListener;
import org.overrun.real4d.world.planet.block.Blocks;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.overrun.real4d.client.SpriteAtlases.BLOCK_ATLAS;

/**
 * @author squid233
 * @since 0.1.0
 */
public class PlanetRenderer implements PlanetListener {
    public static final int MAX_REBUILDS_PER_FRAME = 8;
    public static final int CHUNK_SIZE = 16;
    private final Planet planet;
    private final Chunk[] chunks;
    private final int xChunks;
    private final int yChunks;
    private final int zChunks;

    public PlanetRenderer(Planet planet) {
        this.planet = planet;
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
                    chunks[(x + y * xChunks) * zChunks + z] = new Chunk(planet, x0, y0, z0, x1, y1, z1);
                }
            }
        }
    }

    public List<Chunk> getAllDirtyChunks() {
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
        glEnable(GL_TEXTURE_2D);
        BLOCK_ATLAS.bind();
        Frustum frustum = Frustum.getFrustum();
        for (var chunk : chunks) {
            if (frustum.isVisible(chunk.aabb)) {
                chunk.render(layer);
            }
        }
        glDisable(GL_TEXTURE_2D);
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

    public void pick(Player player, Frustum frustum) {
        var t = Tesselator.getInstance();
        float r = 5;
        var box = player.box.grow(r, r, r);
        int x0 = (int) box.x0;
        int x1 = (int) (box.x1 + 1.0f);
        int y0 = (int) box.y0;
        int y1 = (int) (box.y1 + 1.0f);
        int z0 = (int) box.z0;
        int z1 = (int) (box.z1 + 1.0f);

        glInitNames();
        glPushName(0);
        glPushName(0);
        for (int x = x0; x < x1; x++) {
            glLoadName(x);
            glPushName(0);
            for (int y = y0; y < y1; y++) {
                glLoadName(y);
                glPushName(0);
                for (int z = z0; z < z1; z++) {
                    var block = planet.getBlock(x, y, z);
                    if (!block.isAir()
                        && frustum.isVisible(block.getOutline().moveNew(x, y, z))) {
                        glLoadName(z);
                        glPushName(0);
                        for (int i = 0; i < 6; i++) {
                            glLoadName(i);
                            t.init();
                            block.pickOutline(t, x, y, z, i);
                            t.draw(GL_QUADS);
                        }
                        glPopName();
                    }
                }
                glPopName();
            }
            glPopName();
        }
        glPopName();
        glPopName();
    }

    public void renderHit(HitResult h) {
        Tesselator t = Tesselator.getInstance();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glColor4f(1,
            1,
            1,
            (float) (Math.sin(System.currentTimeMillis() / 100.0) * 0.2 + 0.4) * 0.5f);
        Blocks.STONE.renderFace(t.init(), h.x, h.y, h.z, h.face);
        t.draw(GL_QUADS);
        glDisable(GL_BLEND);
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
                    chunks[(x + y * xChunks) * zChunks + z].markDirty();
                }
            }
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
