package org.overrun.real4d.world.block;

import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.world.planet.Planet;

import java.util.Random;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GrassBlock extends Block {
    private static final Identifier TEXTURE_TOP = new Identifier("grass_block_top");
    private static final Identifier TEXTURE_SID = new Identifier("grass_block_side");
    private static final Identifier TEXTURE_SID_OVERLAY = new Identifier("grass_block_side_overlay");

    @Override
    public void randomTick(Planet planet,
                           int x,
                           int y,
                           int z,
                           Random random) {
    }

    @Override
    public Identifier getTexTop() {
        return TEXTURE_TOP;
    }

    @Override
    public Identifier getTexSid() {
        return TEXTURE_SID;
    }

    @Override
    public Identifier getTexSidOverlay() {
        return TEXTURE_SID_OVERLAY;
    }

    @Override
    public Identifier getTexBtm() {
        return Blocks.DIRT.getId();
    }
}
