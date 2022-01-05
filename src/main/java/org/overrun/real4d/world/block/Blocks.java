package org.overrun.real4d.world.block;

import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.util.Registry;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Blocks {
    public static final Block AIR = register(0, "air", new AirBlock());
    public static final Block GRASS_BLOCK = register(1, "grass_block", new GrassBlock());
    public static final Block DIRT = register(2, "dirt", new Block());
    public static final Block STONE = register(3, "stone", new Block());
    public static final Block COBBLESTONE = register(4, "cobblestone", new Block());
    public static final Block BEDROCK = register(5, "bedrock", new Block());

    public static void register() {
        Registry.BLOCK.defaultEntry = AIR;
    }

    private static <T extends Block> T register(int rawId,
                                                String id,
                                                T block) {
        return Registry.BLOCK.set(
            new Identifier(id),
            block,
            rawId);
    }
}
