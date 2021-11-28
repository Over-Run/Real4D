package org.overrun.real4d.universe.phys;

/**
 * @author squid233
 * @since 0.1.0
 */
public class AABBox {
    private final float epsilon = 0;
    public float x0;
    public float y0;
    public float z0;
    public float x1;
    public float y1;
    public float z1;

    public AABBox(final float x0,
                  final float y0,
                  final float z0,
                  final float x1,
                  final float y1,
                  final float z1) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }

    public AABBox expand(final float xa,
                         final float ya,
                         final float za) {
        float _x0 = x0;
        float _y0 = y0;
        float _z0 = z0;
        float _x1 = x1;
        float _y1 = y1;
        float _z1 = z1;
        if (xa < 0) {
            _x0 += xa;
        }
        if (xa > 0) {
            _x1 += xa;
        }
        if (ya < 0) {
            _y0 += ya;
        }
        if (ya > 0) {
            _y1 += ya;
        }
        if (za < 0) {
            _z0 += za;
        }
        if (za > 0) {
            _z1 += za;
        }
        return new AABBox(_x0, _y0, _z0, _x1, _y1, _z1);
    }

    public AABBox grow(final float xa,
                       final float ya,
                       final float za) {
        float _x0 = x0 - xa;
        float _y0 = y0 - ya;
        float _z0 = z0 - za;
        float _x1 = x1 + xa;
        float _y1 = y1 + ya;
        float _z1 = z1 + za;
        return new AABBox(_x0, _y0, _z0, _x1, _y1, _z1);
    }

    public float clipXCollide(final AABBox c,
                              float xa) {
        // Check if intersected, if false, then return xa
        if (c.y1 <= y0 || c.y0 >= y1 || c.z1 <= z0 || c.z0 >= z1) {
            return xa;
        }
        float max;
        if (xa > 0 && c.x1 <= x0) {
            max = x0 - c.x1 - epsilon;
            if (max < xa) {
                xa = max;
            }
        }
        if (xa < 0 && c.x0 >= x1) {
            max = x1 - c.x0 + epsilon;
            if (max > xa) {
                xa = max;
            }
        }
        return xa;
    }

    public float clipYCollide(final AABBox c,
                              float ya) {
        // Check if intersected, if false, then return ya
        if (c.x1 <= x0 || c.x0 >= x1 || c.z1 <= z0 || c.z0 >= z1) {
            return ya;
        }
        float max;
        if (ya > 0 && c.y1 <= y0) {
            max = y0 - c.y1 - epsilon;
            if (max < ya) {
                ya = max;
            }
        }
        if (ya < 0 && c.y0 >= y1) {
            max = y1 - c.y0 + epsilon;
            if (max > ya) {
                ya = max;
            }
        }
        return ya;
    }

    public float clipZCollide(final AABBox c,
                              float za) {
        // Check if intersected, if false, then return za
        if (c.x1 <= x0 || c.x0 >= x1 || c.y1 <= y0 || c.y0 >= y1) {
            return za;
        }
        float max;
        if (za > 0 && c.z1 <= z0) {
            max = z0 - c.z1 - epsilon;
            if (max < za) {
                za = max;
            }
        }
        if (za < 0 && c.z0 >= z1) {
            max = z1 - c.z0 + epsilon;
            if (max > za) {
                za = max;
            }
        }
        return za;
    }

    public boolean intersects(final AABBox c) {
        return c.x1 > x0 && c.x0 < x1
            && c.y1 > y0 && c.y0 < y1
            && c.z1 > z0 && c.z0 < z1;
    }

    public void move(final float xa,
                     final float ya,
                     final float za) {
        x0 += xa;
        y0 += ya;
        z0 += za;
        x1 += xa;
        y1 += ya;
        z1 += za;
    }
}
