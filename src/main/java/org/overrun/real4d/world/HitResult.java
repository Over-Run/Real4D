package org.overrun.real4d.world;

import org.overrun.glutils.light.Direction;

/**
 * @author squid233
 * @since 0.1.0
 */
public class HitResult {
    public int x;
    public int y;
    public int z;
    public Direction face;

    public HitResult(int x,
                     int y,
                     int z,
                     Direction face) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
    }
}
