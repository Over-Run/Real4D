package org.overrun.real4d.world.block;

import org.overrun.glutils.light.Direction;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.util.Registry;
import org.overrun.real4d.world.phys.AABBox;
import org.overrun.real4d.world.planet.Planet;

import java.util.Random;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Block {
    public void randomTick(Planet planet,
                           int x,
                           int y,
                           int z,
                           Random random) {
    }

    public boolean hasTexOverlay(Direction dir) {
        return getTexture(dir, true) != null;
    }

    public Identifier getTexture(Direction dir,
                                 boolean overlay) {
        if (overlay) return null;
        var id = getId();
        return new Identifier(id.namespace, "block/" + id.path);
    }

    public boolean isOpaque() {
        return true;
    }

    public boolean isSolid() {
        return true;
    }

    public boolean isAir() {
        return false;
    }

    public AABBox getCollision() {
        return getOutline();
    }

    public AABBox getOutline() {
        return AABBox.fullCube();
    }

    public final Identifier getId() {
        return Registry.BLOCK.getId(this);
    }

    public final int getRawId() {
        return Registry.BLOCK.getRawId(this);
    }
}
