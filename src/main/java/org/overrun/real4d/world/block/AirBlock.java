package org.overrun.real4d.world.block;

import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.glutils.light.Direction;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.world.phys.AABBox;
import org.overrun.real4d.world.planet.Planet;

/**
 * @author squid233
 * @since 0.1.0
 */
public class AirBlock extends Block {
    @Override
    public void render(Tesselator t,
                       Planet planet,
                       int layer,
                       int x,
                       int y,
                       int z) {
    }

    @Override
    public void renderFace(Tesselator t,
                           int x,
                           int y,
                           int z,
                           Direction face) {
    }

    @Override
    public void pickOutline(Tesselator t,
                            int x,
                            int y,
                            int z,
                            Direction face) {
    }

    @Override
    public Identifier getTexTop() {
        return null;
    }

    @Override
    public Identifier getTexSid() {
        return null;
    }

    @Override
    public Identifier getTexBtm() {
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
