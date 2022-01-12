package org.overrun.real4d.world.entity;

import org.joml.Vector3f;
import org.overrun.glutils.game.Texture2D;
import org.overrun.glutils.tex.TexParam;
import org.overrun.real4d.client.model.HumanModel;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.world.planet.Planet;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import static org.overrun.real4d.asset.AssetManager.makePath;

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

    @Override
    public void tick() {
        super.tick();

        if (pos.y < -64) remove();

        rotf += rotA;
        rotA *= 0.99;
        rotA += (random() - random()) * random() * random() * 0.07999999821186066;

        float xa = (float) sin(rotf);
        float za = (float) cos(rotf);

        if (onGround && random() < 0.08) {
            dPos.y = 0.5f;
        }

        moveRelative(xa, za, onGround ? 0.1f : 0.02f);
        dPos.y -= 0.08;
        move(dPos);
        dPos.mul(0.91f, 0.98f, 0.91f);
        if (onGround) {
            dPos.mul(0.7f, 1, 0.7f);
        }
    }

    public void render(float delta) {
        glEnable(GL_TEXTURE_2D);
        if (texture == null) {
            texture = new Texture2D(Human.class,
                makePath(TEXTURE),
                TexParam.glNearest());
        }
        texture.bind();
        glPushMatrix();

        double time = System.nanoTime() / 1000000000.0 * 10 * speed + timeOffs;
        float size = 1f / 16f;
        float yy = (float) (-Math.abs(sin(time * 0.6662)) * 5 - 13);
        prevPos.lerp(pos, delta, pPosHolder);
        glTranslatef(pPosHolder.x, pPosHolder.y, pPosHolder.z);
        glScalef(size, size, size);
        //glTranslatef(0, yy, 0);
        glRotated(toDegrees(rotf) + 180, 0, 1, 0);
        MODEL.render((float) time);
        glPopMatrix();
        glDisable(GL_TEXTURE_2D);
    }

    public static void free() {
        if (texture != null) {
            texture.free();
        }
    }
}
