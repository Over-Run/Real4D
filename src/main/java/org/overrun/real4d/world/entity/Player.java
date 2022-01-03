package org.overrun.real4d.world.entity;

import org.overrun.real4d.world.planet.Planet;

import static org.lwjgl.glfw.GLFW.*;
import static org.overrun.glutils.game.GameEngine.input;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Player extends Entity {
    public Player(Planet planet) {
        super(planet);
        eyeHeight = 1.62f;
    }

    @Override
    public void tick() {
        super.tick();
        float xa = 0, za = 0;
        if (input.keyPressed(GLFW_KEY_W)) {
            --za;
        }
        if (input.keyPressed(GLFW_KEY_S)) {
            ++za;
        }
        if (input.keyPressed(GLFW_KEY_A)) {
            --xa;
        }
        if (input.keyPressed(GLFW_KEY_D)) {
            ++xa;
        }
        if (input.keyPressed(GLFW_KEY_SPACE)/*todo && onGround*/) {
            yd = 0.5f;
        }
        moveRelative(xa, za, onGround ? 0.05f : 0.01f);
        yd -= 0.08;
        move(xd, yd, zd);
        xd *= 0.91;
        yd *= 0.98;
        zd *= 0.91;
        if (onGround) {
            xd *= 0.7;
            zd *= 0.7;
        }
    }
}
