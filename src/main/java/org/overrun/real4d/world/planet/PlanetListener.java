package org.overrun.real4d.world.planet;

import org.joml.Vector3ic;

/**
 * @author squid233
 * @since 0.1.0
 */
public interface PlanetListener {
    void blockChanged(Vector3ic pos);

    void lightColumnChanged(int x, int z, int y0, int y1);

    void allChanged();
}
