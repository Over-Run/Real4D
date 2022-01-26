package org.overrun.real4d.client.world.chunk;

import org.overrun.real4d.client.Frustum;
import org.overrun.real4d.world.entity.Player;

import java.util.Comparator;

/**
 * @author squid233
 * @since 0.1.0
 */
public class DirtyChunkSorter implements Comparator<Chunk> {
    private final Player player;
    private final Frustum frustum;
    private final long now = System.currentTimeMillis();

    public DirtyChunkSorter(Player player, Frustum frustum) {
        this.player = player;
        this.frustum = frustum;
    }

    @Override
    public int compare(Chunk c0, Chunk c1) {
        boolean i0 = frustum.isVisible(c0.aabb);
        boolean i1 = frustum.isVisible(c1.aabb);
        if (i0 && !i1) return -1;
        if (i1 && !i0) return 1;
        int t0 = (int) ((now - c0.dirtiedTime) / 2000L);
        int t1 = (int) ((now - c1.dirtiedTime) / 2000L);
        if (t0 < t1) return -1;
        if (t0 > t1) return 1;
        return c0.distanceToSqr(player) < c1.distanceToSqr(player) ? -1 : 1;
    }
}
