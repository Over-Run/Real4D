package org.overrun.real4d.universe.entity;

import org.overrun.real4d.universe.planet.Planet;

import static org.lwjgl.glfw.GLFW.*;
import static org.overrun.real4d.client.input.Keyboard.isKeyDown;

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
        if (isKeyDown(GLFW_KEY_W)) {
            --za;
        }
        if (isKeyDown(GLFW_KEY_S)) {
            ++za;
        }
        if (isKeyDown(GLFW_KEY_A)) {
            --xa;
        }
        if (isKeyDown(GLFW_KEY_D)) {
            ++xa;
        }
        if (isKeyDown(GLFW_KEY_SPACE)
            //&& onGround
        ) {
            yd = 0.5f;
        }
        moveRelative(xa, za, onGround ? 0.1f : 0.02f);
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
