package org.overrun.real4d.world.entity;

import org.joml.RoundingMode;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.overrun.real4d.world.phys.AABBox;
import org.overrun.real4d.world.planet.Planet;

import static java.lang.Math.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Entity {
    protected Planet planet;
    public final Vector3f pos = new Vector3f();
    public final Vector3f prevPos = new Vector3f();
    public final Vector2f rot = new Vector2f();
    public final Vector3f posDelta = new Vector3f();
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

    public void resetPos() {
        float x = (float) random() * planet.width;
        float y = planet.height + 10;
        float z = (float) random() * planet.depth;
        setPos(x, y, z);
    }

    public void remove() {
        removed = true;
    }

    protected void setSize(final float w,
                           final float h) {
        bbWidth = w;
        bbHeight = h;
    }

    protected void setPos(final float x,
                          final float y,
                          final float z) {
        pos.set(x, y, z);
        float w = bbWidth / 2.0f;
        float h = bbHeight / 2.0f;
        box = new AABBox(x - w, y - h, z - w, x + w, y + h, z + w);
    }

    public void tick() {
        prevPos.set(pos);
    }

    public void turn(final float xo,
                     final float yo) {
        rot.add(yo * 0.15f, xo * 0.15f);
        if (rot.x < -90) {
            rot.x = -90;
        } else if (rot.x > 90) {
            rot.x = 90;
        }
        if (rot.y > 180) {
            rot.y = -180;
        } else if (rot.y < -180) {
            rot.y = 180;
        }
    }

    public void move(final Vector3f vec) {
        move(vec.x, vec.y, vec.z);
    }

    public void move(float xa,
                     float ya,
                     float za) {
        float xaOrg = xa;
        float yaOrg = ya;
        float zaOrg = za;

        var boxes = planet.getCubes(box.expand(xa, ya, za));
        for (var aabb : boxes) {
            ya = aabb.clipYCollide(box, ya);
        }
        box.move(0, ya, 0);
        for (var aabb : boxes) {
            xa = aabb.clipXCollide(box, xa);
        }
        box.move(xa, 0, 0);
        for (var aabb : boxes) {
            za = aabb.clipZCollide(box, za);
        }
        box.move(0, 0, za);

        onGround = yaOrg != ya && yaOrg < 0;

        if (xaOrg != xa) {
            posDelta.x = 0;
        }
        if (yaOrg != ya) {
            posDelta.y = 0;
        }
        if (zaOrg != za) {
            posDelta.z = 0;
        }

        pos.set((box.min.x + box.max.x) / 2.0f,
            box.min.y + eyeHeight,
            (box.min.z + box.max.z) / 2.0f);
    }

    public void moveRelative(float xa,
                             float za,
                             final float speed) {
        var dist = xa * xa + za * za;
        if (dist >= 0.01) {
            dist = speed / (float) sqrt(dist);
            xa *= dist;
            za *= dist;
            var rad = toRadians(rot.y);
            var sin = sin(rad);
            var cos = cos(rad);
            posDelta.add((float) (xa * cos - za * sin),
                0,
                (float) (za * cos + xa * sin));
        }
    }

    public Vector3i getBlockPos() {
        return new Vector3i(pos, RoundingMode.FLOOR);
    }

    public boolean isLit() {
        var p = getBlockPos();
        return planet.isLit(p);
    }
}
