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
    /**
     * Minimum pos of the box
     */
    public final Vector3f min;
    /**
     * Maximum pos of the box
     */
    public final Vector3f max;

    /**
     * Construct
     *
     * @param min {@link #min}
     * @param max {@link #max}
     */
    public AABBox(final Vector3fc min,
                  final Vector3fc max) {
        this.min = new Vector3f(min);
        this.max = new Vector3f(max);
    }

    /**
     * Construct
     *
     * @param x0 min x
     * @param y0 min y
     * @param z0 min z
     * @param x1 max x
     * @param y1 max y
     * @param z1 max z
     */
    public AABBox(final float x0,
                  final float y0,
                  final float z0,
                  final float x1,
                  final float y1,
                  final float z1) {
        min = new Vector3f(x0, y0, z0);
        max = new Vector3f(x1, y1, z1);
    }

    /**
     * Full cube box
     *
     * @return the box
     */
    public static AABBox fullCube() {
        return FULL_CUBE;
    }

    /**
     * Empty box
     *
     * @return the box
     */
    public static AABBox empty() {
        return EMPTY;
    }

    /**
     * Expand box
     *
     * @param xa x length
     * @param ya y length
     * @param za z length
     * @return new box
     */
    public AABBox expand(final float xa,
                         final float ya,
                         final float za) {
        return new AABBox(
            new Vector3f(min).add(min(xa, 0), min(ya, 0), min(za, 0)),
            new Vector3f(max).add(max(xa, 0), max(ya, 0), max(za, 0)));
    }

    /**
     * Grow box
     *
     * @param xa x length
     * @param ya y length
     * @param za z length
     * @return new box
     */
    public AABBox grow(final float xa,
                       final float ya,
                       final float za) {
        return new AABBox(
            new Vector3f(min).sub(xa, ya, za),
            new Vector3f(max).add(xa, ya, za)
        );
    }

    /**
     * Clip x collide to minimum value
     *
     * @param c  the R box
     * @param xa x move value
     * @return the new value
     */
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

    /**
     * Clip y collide to minimum value
     *
     * @param c  the R box
     * @param ya y move value
     * @return the new value
     */
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

    /**
     * Clip z collide to minimum value
     *
     * @param c  the R box
     * @param za z move value
     * @return the new value
     */
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

    /**
     * Move the box
     *
     * @param xa offset x
     * @param ya offset y
     * @param za offset z
     */
    public void move(final float xa,
                     final float ya,
                     final float za) {
        min.add(xa, ya, za);
        max.add(xa, ya, za);
    }

    /**
     * Move the box
     *
     * @param xa offset x
     * @param ya offset y
     * @param za offset z
     * @return the new box
     */
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
