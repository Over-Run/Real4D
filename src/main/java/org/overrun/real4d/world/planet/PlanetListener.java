package org.overrun.real4d.world.planet;

/**
 * @author squid233
 * @since 0.1.0
 */
public interface PlanetListener {
    void blockChanged(int x, int y, int z);

    void lightColumnChanged(int x, int z, int y0, int y1);

    void allChanged();
}
