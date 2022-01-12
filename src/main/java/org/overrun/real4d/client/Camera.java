package org.overrun.real4d.client;

import org.joml.Vector3f;
import org.overrun.real4d.world.entity.Player;

import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Camera {
    public final Vector3f pos = new Vector3f();
    public final Vector3f prevPos = new Vector3f();
    public final Vector3f pPosHolder = new Vector3f();

    public void moveToPlayer(Player player,
                             float delta) {
        glTranslatef(0, 0, -0.3f);
        glRotatef(player.rot.x, 1, 0, 0);
        glRotatef(player.rot.y, 0, 1, 0);
        var p = prevPos.lerp(pos, delta, pPosHolder);
        float x = p.x;
        float y = p.y;
        float z = p.z;
        glTranslatef(-x, -y, -z);
    }
}
