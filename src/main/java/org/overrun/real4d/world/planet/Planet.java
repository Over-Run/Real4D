package org.overrun.real4d.world.planet;

import org.overrun.real4d.world.phys.AABBox;
import org.overrun.real4d.world.planet.block.Block;
import org.overrun.real4d.world.planet.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.overrun.real4d.util.Registry.BLOCK;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Planet {
    private static final int BLOCK_UPDATE_INTERVAL = 400;
    public final int width;
    public final int height;
    public final int depth;
    private final int[] blocks;
    private final List<PlanetListener> listeners = new ArrayList<>();
    private final Random random = new Random();
    private int unprocessed = 0;

    public Planet(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        blocks = new int[width * height * depth];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                blocks[getIndex(x, 0, z)] = BLOCK.getRawId(Blocks.BEDROCK);
            }
        }
        generateMap();
    }

    private void generateMap() {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                int id = random.nextInt(1, BLOCK.size());
                int y = random.nextInt(1, 6);
                blocks[getIndex(x, y, z)] = id;
            }
        }
    }

    public void addListener(PlanetListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlanetListener listener) {
        listeners.remove(listener);
    }

    public boolean isLightBlocker(int x, int y, int z) {
        return getBlock(x, y, z).isOpaque();
    }

    public List<AABBox> getCubes(AABBox origin) {
        var boxes = new ArrayList<AABBox>();
        int x0 = (int) origin.x0;
        int x1 = (int) (origin.x1 + 1);
        int y0 = (int) origin.y0;
        int y1 = (int) (origin.y1 + 1);
        int z0 = (int) origin.z0;
        int z1 = (int) (origin.z1 + 1);

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

        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    var block = getBlock(x, y, z);
                    var aabb = block.getCollision();
                    if (aabb != AABBox.empty()) {
                        boxes.add(aabb.moveNew(x, y, z));
                    }
                }
            }
        }

        return boxes;
    }

    public void setBlock(int x, int y, int z, Block block) {
        if (x >= 0 && y >= 0 && z >= 0
            && x < width && y < height && z < depth) {
            int i = getIndex(x, y, z);
            int rid = BLOCK.getRawId(block);
            if (rid == blocks[i]) {
                return;
            }
            blocks[i] = rid;
            for (var listener : listeners) {
                listener.blockChanged(x, y, z);
            }
        }
    }

    public Block getBlock(int x, int y, int z) {
        return (x >= 0 && y >= 0 && z >= 0
            && x < width && y < height && z < depth)
            ? BLOCK.get(blocks[getIndex(x, y, z)])
            : Blocks.AIR;
    }

    public int getIndex(int x, int y, int z) {
        return (y * depth + z) * width + x;
    }

    public boolean isSolidBlock(int x, int y, int z) {
        return getBlock(x, y, z).isSolid();
    }

    public void tick() {
        unprocessed += width * height * depth;
        int ticks = unprocessed / BLOCK_UPDATE_INTERVAL;
        unprocessed -= ticks * BLOCK_UPDATE_INTERVAL;
        for (int i = 0; i < ticks; ++i) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int z = random.nextInt(depth);
            var block = getBlock(x, y, z);
            block.randomTick(this, x, y, z, random);
        }
    }
}
