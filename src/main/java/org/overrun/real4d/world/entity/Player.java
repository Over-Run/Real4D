package org.overrun.real4d.world.entity;

import org.overrun.real4d.client.Camera;
import org.overrun.real4d.world.block.Block;
import org.overrun.real4d.world.block.Blocks;
import org.overrun.real4d.world.planet.Planet;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Player extends Entity {
    public final Block[] hotBar = new Block[10];
    public final Camera camera = new Camera();
    public int select = 0;
    public boolean isSneaking;
    public boolean isRunning;

    public Player(Planet planet) {
        super(planet);
        eyeHeight = 1.62f;
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
    public void move(float xa,
                     float ya,
                     float za) {
        super.move(xa, ya, za);
        camera.pos.set((box.min.x + box.max.x) / 2.0f,
            box.min.y + eyeHeight,
            (box.min.z + box.max.z) / 2.0f);
    }

    @Override
    public void tick() {
        super.tick();
        camera.prevPos.set(camera.pos);
        float xa = 0, za = 0;
        if (client.input.keyPressed(GLFW_KEY_R)) {
            resetPos();
        }
        if (client.input.keyPressed(GLFW_KEY_W)) {
            --za;
        }
        if (client.input.keyPressed(GLFW_KEY_S)) {
            ++za;
        }
        if (client.input.keyPressed(GLFW_KEY_A)) {
            --xa;
        }
        if (client.input.keyPressed(GLFW_KEY_D)) {
            ++xa;
        }
        if (client.input.keyPressed(GLFW_KEY_SPACE)/*todo && onGround*/) {
            dPos.y = 0.5f;
        }
        if (client.input.keyPressed(GLFW_KEY_LEFT_SHIFT)
            || client.input.keyPressed(GLFW_KEY_RIGHT_SHIFT)) {
            isSneaking = true;
            bbHeight = 1.5f;
            eyeHeight = 1.32f;
        } else {
            isSneaking = false;
            bbHeight = 1.8f;
            eyeHeight = 1.62f;
        }
        isRunning = (client.input.keyPressed(GLFW_KEY_LEFT_CONTROL)
            || client.input.keyPressed(GLFW_KEY_RIGHT_CONTROL))
            && (xa * xa + za * za >= 0.01f);
        float speed;
        if (onGround) {
            if (isSneaking) {
                speed = 0.05f;
            } else if (isRunning) {
                speed = 0.2f;
            } else {
                speed = 0.1f;
            }
        } else {
            if (isRunning) {
                speed = 0.04f;
            } else {
                speed = 0.02f;
            }
        }
        moveRelative(xa, za, speed);
        dPos.y -= 0.08;
        move(dPos);
        dPos.mul(0.91f, 0.98f, 0.91f);
        if (onGround) {
            dPos.mul(0.7f, 1, 0.7f);
        }
    }

    @Override
    public void turn(final float xo,
                     final float yo) {
        super.turn(yo * 0.15f, xo * 0.15f);
        if (rot.x < -90) {
            rot.x = -90;
        } else if (rot.x > 90) {
            rot.x = 90;
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
