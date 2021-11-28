package org.overrun.real4d.universe.entity;

import org.overrun.real4d.universe.phys.AABBox;
import org.overrun.real4d.universe.planet.Planet;

import static java.lang.Math.*;
import static org.overrun.real4d.client.input.Mouse.sensitivity;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Entity {
    protected Planet planet;
    public float x, y, z;
    public float prevX, prevY, prevZ;
    public float xRot, yRot;
    public float xd, yd, zd;
    public AABBox box;
    public boolean onGround;
    public boolean removed;
    public float eyeHeight;
    protected float bbWidth = 0.6f;
    protected float bbHeight = 1.8f;

    public Entity(Planet planet) {
        this.planet = planet;
        resetPos();
    }

    protected void resetPos() {
        float x = (float) random() * planet.width;
        float y = planet.height + 10;
        float z = (float) random() * planet.depth;
        setPos(x, y, z);
    }

    public void remove() {
        removed = true;
    }

    public void kill() {
        remove();
    }

    protected void setSize(final float w,
                           final float h) {
        bbWidth = w;
        bbHeight = h;
    }

    protected void setPos(final float x,
                          final float y,
                          final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        float w = bbWidth / 2.0f;
        float h = bbHeight / 2.0f;
        box = new AABBox(x - w, y - h, z - w, x + w, y + h, z + w);
    }

    public void tick() {
        prevX = x;
        prevY = y;
        prevZ = z;
    }

    public void turn(final float xo,
                     final float yo) {
        yRot += xo * sensitivity;
        xRot -= yo * sensitivity;
        if (xRot < -90) {
            xRot = -90;
        }
        if (xRot > 90) {
            xRot = 90;
        }
        if (yRot > 360) {
            yRot = 0;
        }
    }

    public void move(float xa,
                     float ya,
                     float za) {
        float xaOrg = xa;
        float yaOrg = ya;
        float zaOrg = za;

        box.move(0, ya, 0);
        box.move(xa, 0, 0);
        box.move(0, 0, za);

        onGround = yaOrg != ya && yaOrg < 0;

        if (xaOrg != xa) {
            xd = 0;
        }
        if (yaOrg != ya) {
            yd = 0;
        }
        if (zaOrg != za) {
            zd = 0;
        }

        x = (box.x0 + box.x1) / 2.0f;
        y = box.y0;
        z = (box.z0 + box.z1) / 2.0f;
    }

    public void moveRelative(float xa,
                             float za,
                             final float speed) {
        float dist = xa * xa + za * za;
        if (dist >= 0.01) {
            dist = speed / (float) sqrt(speed);
            xa *= dist;
            za *= dist;
            float sin = (float) sin(toRadians(yRot));
            float cos = (float) cos(toRadians(yRot));
            xd += xa * cos - za * sin;
            zd += za * cos + xa * sin;
        }
    }
}
