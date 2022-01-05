package org.overrun.real4d.world.phys;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.StringJoiner;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author squid233
 * @since 0.1.0
 */
public class AABBox {
    private static final AABBox FULL_CUBE = new AABBox(0, 0, 0, 1, 1, 1);
    private static final AABBox EMPTY = new AABBox(0, 0, 0, 0, 0, 0);
    private final float epsilon = 0;
    public final Vector3f min;
    public final Vector3f max;

    public AABBox(final Vector3fc min,
                  final Vector3fc max) {
        this.min = new Vector3f(min);
        this.max = new Vector3f(max);
    }

    public AABBox(final float x0,
                  final float y0,
                  final float z0,
                  final float x1,
                  final float y1,
                  final float z1) {
        min = new Vector3f(x0, y0, z0);
        max = new Vector3f(x1, y1, z1);
    }

    public static AABBox fullCube() {
        return FULL_CUBE;
    }

    public static AABBox empty() {
        return EMPTY;
    }

    public AABBox expand(final float xa,
                         final float ya,
                         final float za) {
        return new AABBox(
            new Vector3f(min).add(min(xa, 0), min(ya, 0), min(za, 0)),
            new Vector3f(max).add(max(xa, 0), max(ya, 0), max(za, 0)));
    }

    public AABBox grow(final float xa,
                       final float ya,
                       final float za) {
        return new AABBox(
            new Vector3f(min).sub(xa, ya, za),
            new Vector3f(max).add(xa, ya, za)
        );
    }

    public float clipXCollide(final AABBox c,
                              float xa) {
        // Check if intersected, if false, then return xa
        if (c.max.y <= min.y
            || c.min.y >= max.y
            || c.max.z <= min.z
            || c.min.z >= max.z) {
            return xa;
        }
        float maxA;
        if (xa > 0 && c.max.x <= min.x) {
            maxA = min.x - c.max.x - epsilon;
            if (maxA < xa) {
                xa = maxA;
            }
        }
        if (xa < 0 && c.min.x >= max.x) {
            maxA = max.x - c.min.x + epsilon;
            if (maxA > xa) {
                xa = maxA;
            }
        }
        return xa;
    }

    public float clipYCollide(final AABBox c,
                              float ya) {
        // Check if intersected, if false, then return ya
        if (c.max.x <= min.x
            || c.min.x >= max.x
            || c.max.z <= min.z
            || c.min.z >= max.z) {
            return ya;
        }
        float maxA;
        if (ya > 0 && c.max.y <= min.y) {
            maxA = min.y - c.max.y - epsilon;
            if (maxA < ya) {
                ya = maxA;
            }
        }
        if (ya < 0 && c.min.y >= max.y) {
            maxA = max.y - c.min.y + epsilon;
            if (maxA > ya) {
                ya = maxA;
            }
        }
        return ya;
    }

    public float clipZCollide(final AABBox c,
                              float za) {
        // Check if intersected, if false, then return za
        if (c.max.x <= min.x
            || c.min.x >= max.x
            || c.max.y <= min.y
            || c.min.y >= max.y) {
            return za;
        }
        float maxA;
        if (za > 0 && c.max.z <= min.z) {
            maxA = min.z - c.max.z - epsilon;
            if (maxA < za) {
                za = maxA;
            }
        }
        if (za < 0 && c.min.z >= max.z) {
            maxA = max.z - c.min.z + epsilon;
            if (maxA > za) {
                za = maxA;
            }
        }
        return za;
    }

    public void move(final float xa,
                     final float ya,
                     final float za) {
        min.add(xa, ya, za);
        max.add(xa, ya, za);
    }

    public AABBox moveNew(final float xa,
                          final float ya,
                          final float za) {
        return new AABBox(
            new Vector3f(min).add(xa, ya, za),
            new Vector3f(max).add(xa, ya, za)
        );
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AABBox.class.getSimpleName() + "[", "]")
            .add("min=" + min)
            .add("max=" + max)
            .toString();
    }
}
