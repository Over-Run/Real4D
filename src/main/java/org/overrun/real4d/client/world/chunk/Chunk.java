package org.overrun.real4d.client.world.chunk;

import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.real4d.world.entity.Player;
import org.overrun.real4d.world.phys.AABBox;
import org.overrun.real4d.world.planet.Planet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Chunk {
    public AABBox aabb;
    public final Planet planet;
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
    private final int lists;
    public long dirtiedTime = 0L;
    private static final Tesselator t = Tesselator.getInstance();
    public static int updates = 0;
    private static long totalTime = 0L;
    private static int totalUpdates = 0;

    public Chunk(Planet planet,
                 int x0,
                 int y0,
                 int z0,
                 int x1,
                 int y1,
                 int z1) {
        this.planet = planet;
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
        lists = glGenLists(2);
    }

    private void rebuild(int layer) {
        dirty = false;
        ++updates;
        long before = System.nanoTime();
        glNewList(lists + layer, GL_COMPILE);
        t.init();
        int blocks = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    planet.getBlock(x, y, z).render(t, planet, layer, x, y, z);
                    ++blocks;
                }
            }
        }
        t.draw(GL_QUADS);
        glEndList();
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
        glCallList(lists + layer);
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

    public float distanceToSqr(Player player) {
        float xd = player.x - x;
        float yd = player.y - y;
        float zd = player.z - z;
        return xd * xd + yd * yd + zd * zd;
    }
}
