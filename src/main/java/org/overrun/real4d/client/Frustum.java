package org.overrun.real4d.client;

import org.joml.FrustumIntersection;
import org.overrun.real4d.client.gl.GLMatrix;
import org.overrun.real4d.world.phys.AABBox;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Frustum {
    private static final Frustum instance = new Frustum();
    public final FrustumIntersection frustum = new FrustumIntersection();

    private Frustum() {
    }

    public static Frustum getFrustum() {
        instance.calculateFrustum();
        return instance;
    }

    private void calculateFrustum() {
        frustum.set(GLMatrix.getProjectionl().mul(GLMatrix.getModelviewl()));
    }

    public boolean pointInFrustum(float x,
                                  float y,
                                  float z) {
        return frustum.testPoint(x, y, z);
    }

    public boolean sphereInFrustum(float x,
                                   float y,
                                   float z,
                                   float radius) {
        return frustum.testSphere(x, y, z, radius);
    }

    public boolean cubeInFrustum(float x1,
                                 float y1,
                                 float z1,
                                 float x2,
                                 float y2,
                                 float z2) {
        return frustum.testAab(x1, y1, z1, x2, y2, z2);
    }

    public boolean isVisible(AABBox aabb) {
        return frustum.testAab(aabb.min, aabb.max);
    }
}
