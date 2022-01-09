package org.overrun.real4d.world.planet;

import org.joml.Vector3ic;
import org.overrun.real4d.world.block.Block;
import org.overrun.real4d.world.phys.AABBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.overrun.real4d.util.VectorPool.vec3AllocInt;
import static org.overrun.real4d.world.block.Blocks.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Planet {
    private static final int BLOCK_UPDATE_INTERVAL = 400;
    public final int width;
    public final int height;
    public final int depth;
    private final Block[] blocks;
    private final int[] lightDepths;
    private final List<PlanetListener> listeners = new ArrayList<>();
    private final Random random = new Random();
    private int unprocessed = 0;

    public Planet(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        blocks = new Block[width * height * depth];
        lightDepths = new int[width * depth];
        var vec = vec3AllocInt("real4d:world.planet.Planet.<init>(I;I;I)V");
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                vec.set(x, 0, z);
                blocks[getIndex(vec)] = BEDROCK;
                for (int y = 1; y < height; y++) {
                    vec.y = y;
                    blocks[getIndex(vec)] = AIR;
                }
            }
        }
        generateMap();
        calcLightDepths(0, 0, width, depth);
    }

    private void generateMap() {
        var vec = vec3AllocInt("real4d:world.planet.Planet.generateMap()V");
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                int by = random.nextInt(1, 5);
                boolean genBedrock = random.nextBoolean();
                if (genBedrock) {
                    vec.set(x, by, z);
                    blocks[getIndex(vec)] = BEDROCK;
                }
                for (int y = 1; y < 5; y++) {
                    if (!genBedrock || y != by) {
                        vec.set(x, y, z);
                        blocks[getIndex(vec)] = STONE;
                    }
                }
            }
        }
    }

    public void calcLightDepths(int x0, int z0, int x1, int z1) {
        var vec = vec3AllocInt("real4d:world.planet.Planet.calcLightDepths(I;I;I;I)V");
        for (int x = x0, mx = x0 + x1; x < mx; x++) {
            for (int z = z0, mz = z0 + z1; z < mz; z++) {
                int oldDepth = lightDepths[x + z * width];
                int y = height - 1;
                while (y > 0 && !isLightBlocker(vec.set(x, y, z))) {
                    --y;
                }
                lightDepths[x + z * width] = y;
                if (oldDepth != y) {
                    // Y-level
                    int yl0 = min(oldDepth, y);
                    int yl1 = max(oldDepth, y);
                    for (var listener : listeners) {
                        listener.lightColumnChanged(x, z, yl0, yl1);
                    }
                }
            }
        }
    }

    public void addListener(PlanetListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlanetListener listener) {
        listeners.remove(listener);
    }

    public boolean isLightBlocker(Vector3ic pos) {
        return getBlock(pos).isOpaque();
    }

    public List<AABBox> getCubes(AABBox origin) {
        var boxes = new ArrayList<AABBox>();
        int x0 = (int) origin.min.x;
        int x1 = (int) (origin.max.x + 1);
        int y0 = (int) origin.min.y;
        int y1 = (int) (origin.max.y + 1);
        int z0 = (int) origin.min.z;
        int z1 = (int) (origin.max.z + 1);

        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (z0 < 0) {
            z0 = 0;
        }
        if (x1 > width) {
            x1 = width;
        }
        if (y1 > height) {
            y1 = height;
        }
        if (z1 > depth) {
            z1 = depth;
        }
        var pos = vec3AllocInt("real4d:world.planet.Planet.getCubes(AABBox)List");
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    pos.set(x, y, z);
                    var block = getBlock(pos);
                    var aabb = block.getCollision();
                    if (aabb != AABBox.empty()) {
                        boxes.add(aabb.moveNew(x, y, z));
                    }
                }
            }
        }

        return boxes;
    }

    public boolean setBlock(Vector3ic pos,
                            Block block) {
        if (!inIndex(pos)) {
            return false;
        }
        int i = getIndex(pos);
        if (blocks[i] == block) {
            return false;
        }
        blocks[i] = block;
        calcLightDepths(pos.x(), pos.z(), 1, 1);
        for (var listener : listeners) {
            listener.blockChanged(pos);
        }
        return true;
    }

    public Block getBlock(Vector3ic pos) {
        return inIndex(pos)
            ? blocks[getIndex(pos)]
            : AIR;
    }

    public int getIndex(Vector3ic pos) {
        int x = pos.x();
        int y = pos.y();
        int z = pos.z();
        return (y * depth + z) * width + x;
    }

    public boolean inIndex(Vector3ic pos) {
        int x = pos.x();
        int y = pos.y();
        int z = pos.z();
        return x >= 0 && y >= 0 && z >= 0
            && x < width && y < height && z < depth;
    }

    public boolean isLit(Vector3ic pos) {
        int x = pos.x();
        int y = pos.y();
        int z = pos.z();
        return !inIndex(pos) || y >= lightDepths[x + z * width];
    }

    public boolean isSolidBlock(Vector3ic pos) {
        return getBlock(pos).isSolid();
    }

    public void tick() {
        unprocessed += width * height * depth;
        int ticks = unprocessed / BLOCK_UPDATE_INTERVAL;
        unprocessed -= ticks * BLOCK_UPDATE_INTERVAL;
        for (int i = 0; i < ticks; ++i) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int z = random.nextInt(depth);
            var pos = vec3AllocInt("real4d:world.planet.Planet.tick()V")
                .set(x, y, z);
            var block = getBlock(pos);
            if (!block.isAir()) {
                block.randomTick(this, pos, random);
            }
        }
    }
}
