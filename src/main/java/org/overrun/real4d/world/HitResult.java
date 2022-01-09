package org.overrun.real4d.world;

import org.joml.Vector3i;
import org.overrun.glutils.light.Direction;

/**
 * @author squid233
 * @since 0.1.0
 */
public class HitResult {
    public Vector3i pos;
    public Direction face;

    public HitResult(int x,
                     int y,
                     int z,
                     Direction face) {
        pos = new Vector3i(x, y, z);
        this.face = face;
    }
}
