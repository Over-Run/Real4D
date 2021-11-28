package org.overrun.real4d.universe.planet;

import org.overrun.real4d.universe.entity.Player;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Planet {
    public final Player player = new Player(this);
    public final int width;
    public final int height;
    public final int depth;

    public Planet(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public void tick() {
        player.tick();
    }
}
