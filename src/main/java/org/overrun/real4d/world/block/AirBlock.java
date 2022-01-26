package org.overrun.real4d.world.block;

import org.overrun.glutils.light.Direction;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.world.phys.AABBox;

/**
 * @author squid233
 * @since 0.1.0
 */
public class AirBlock extends Block {
    @Override
    public Identifier getTexture(Direction dir,
                                 boolean overlay) {
        return null;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isAir() {
        return true;
    }

    @Override
    public AABBox getOutline() {
        return AABBox.empty();
    }
}
