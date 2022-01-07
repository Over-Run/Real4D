package org.overrun.real4d.world.entity;

import org.overrun.real4d.world.block.Block;
import org.overrun.real4d.world.block.Blocks;
import org.overrun.real4d.world.planet.Planet;

import static org.lwjgl.glfw.GLFW.*;
import static org.overrun.glutils.game.GameEngine.input;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Player extends Entity {
    public final Block[] hotBar = new Block[10];
    public int select = 0;

    public Player(Planet planet) {
        super(planet);
        eyeHeight = bbHeight - 0.18f;
        hotBar[0] = Blocks.GRASS_BLOCK;
        hotBar[1] = Blocks.DIRT;
        hotBar[2] = Blocks.STONE;
        hotBar[3] = Blocks.COBBLESTONE;
        hotBar[4] = Blocks.BEDROCK;
        for (int i = 5; i < hotBar.length; i++) {
            hotBar[i] = Blocks.AIR;
        }
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
        if (input.keyPressed(GLFW_KEY_LEFT_SHIFT)
            || input.keyPressed(GLFW_KEY_RIGHT_SHIFT)) {
            bbHeight = 1.5f;
        } else {
            bbHeight = 1.8f;
        }
        eyeHeight = bbHeight - 0.18f;
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

    public void mouseWheel(int xo, int yo) {
        select -= yo;
        if (select > 9) {
            select = 0;
        } else if (select < 0) {
            select = 9;
        }
    }
}
