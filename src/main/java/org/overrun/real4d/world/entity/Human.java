package org.overrun.real4d.world.entity;

import org.joml.Vector3f;
import org.overrun.glutils.tex.TexParam;
import org.overrun.glutils.tex.Texture2D;
import org.overrun.glutils.tex.Textures;
import org.overrun.real4d.client.model.HumanModel;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.world.planet.Planet;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import static org.overrun.real4d.asset.AssetManager.makePath;
import static org.overrun.real4d.client.gl.GLStateMgr.disableTexture2D;
import static org.overrun.real4d.client.gl.GLStateMgr.enableTexture2D;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Human extends Entity {
    private static final HumanModel MODEL = new HumanModel();
    public static final Identifier TEXTURE = new Identifier("textures/entity/human.png");
    public static Texture2D texture;
    public float rotf = (float) (random() * PI * 2);
    public float timeOffs = (float) random() * 1239813;
    public float speed = 1;
    public float rotA = (float) (random() + 1) * 0.01f;
    public final Vector3f pPosHolder = new Vector3f();

    public Human(Planet planet,
                 float x,
                 float y,
                 float z) {
        super(planet);
        setPos(x, y, z);
    }

    public void tick() {
        super.tick();

        if (pos.y < -64) remove();

        rot.y += toDegrees(rotA);
        rotA *= 0.99;
        rotA += (random() - random()) * random() * random() * 0.08;

        var rad = toRadians(rot.y);
        var xa = (float) sin(rad);
        var za = (float) cos(rad);

        if (onGround && random() < 0.08) {
            dPos.y = 0.5f;
        }

        var rotYO = rot.y;
        rot.y = 0;
        moveRelative(xa, za, onGround ? 0.1f : 0.02f);
        rot.y = rotYO;
        dPos.y -= 0.08;
        move(dPos);
        dPos.mul(0.91f, 0.98f, 0.91f);
        if (onGround) {
            dPos.mul(0.7f, 1, 0.7f);
        }
    }

    public void render(float delta) {
        enableTexture2D();
        if (texture == null) {
            texture = Textures.load2D(this,
                makePath(TEXTURE),
                TexParam.glNearest(),
                true);
        }
        texture.bind();

        glPushMatrix();
        double time = System.nanoTime() / 1000000000.0 * 10 * speed + timeOffs;
        float size = 1f / 16f;
        float yy = (float) (-Math.abs(sin(time * 0.6662)) * 5 - 13);
        prevPos.lerp(pos, delta, pPosHolder);
        glTranslatef(pPosHolder.x, pPosHolder.y, pPosHolder.z);
        glScalef(size, size, size);
        glRotated(rot.y + 180, 0, 1, 0);
        MODEL.render((float) time);
        glPopMatrix();

        disableTexture2D();
    }

    public static void free() {
        if (texture != null) {
            texture.free();
        }
    }
}
