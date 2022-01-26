package org.overrun.real4d.world.block;

import org.overrun.glutils.light.Direction;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.world.planet.Planet;

import java.util.Random;

import static org.overrun.glutils.light.Direction.DOWN;
import static org.overrun.glutils.light.Direction.UP;
import static org.overrun.real4d.world.block.Blocks.DIRT;
import static org.overrun.real4d.world.block.Blocks.GRASS_BLOCK;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GrassBlock extends Block {
    private static final Identifier TEXTURE_TOP = new Identifier("block/grass_block_top");
    private static final Identifier TEXTURE_SID = new Identifier("block/grass_block_side");
    private static final Identifier TEXTURE_SID_OVERLAY = new Identifier("block/grass_block_side_overlay");

    @Override
    public void randomTick(Planet planet,
                           int x,
                           int y,
                           int z,
                           Random random) {
        if (!planet.isLit(x, y, z)) {
            planet.setBlock(x, y, z, DIRT);
        } else {
            for (int i = 0; i < 4; i++) {
                int xb = random.nextInt(3) - 1;
                int yb = random.nextInt(5) - 3;
                int zb = random.nextInt(3) - 1;
                if (planet.getBlock(xb, yb, zb) == DIRT
                    && planet.isLit(xb, yb, zb)) {
                    planet.setBlock(xb, yb, zb, GRASS_BLOCK);
                }
            }
        }
    }

    @Override
    public Identifier getTexture(Direction dir,
                                 boolean overlay) {
        if (overlay && dir != UP && dir != DOWN)
            return TEXTURE_SID_OVERLAY;
        return switch (dir) {
            case UP -> TEXTURE_TOP;
            case DOWN -> DIRT.getTexture(dir, overlay);
            default -> TEXTURE_SID;
        };
    }
}
