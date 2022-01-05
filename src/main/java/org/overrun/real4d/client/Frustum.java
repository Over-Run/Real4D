package org.overrun.real4d.client;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.overrun.real4d.world.phys.AABBox;

import static org.joml.FrustumIntersection.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Frustum {
    public static final int LEFT = PLANE_NX;
    public static final int RIGHT = PLANE_PX;
    public static final int BOTTOM = PLANE_NY;
    public static final int TOP = PLANE_PY;
    public static final int BACK = PLANE_NZ;
    public static final int FRONT = PLANE_PZ;
    private static final Frustum frustum = new Frustum();
    private final float[] _proj = new float[16];
    private final float[] _modl = new float[16];
    private final Matrix4f proj = new Matrix4f();
    private final Matrix4f modl = new Matrix4f();
    public final FrustumIntersection m_Frustum = new FrustumIntersection();

    private Frustum() {
    }

    public static Frustum getFrustum() {
        frustum.calculateFrustum();
        return frustum;
    }

    private void calculateFrustum() {
        glGetFloatv(GL_PROJECTION_MATRIX, _proj);
        glGetFloatv(GL_MODELVIEW_MATRIX, _modl);
        proj.set(_proj);
        modl.set(_modl);
        m_Frustum.set(proj.mul(modl));
    }

    public boolean pointInFrustum(float x, float y, float z) {
        return m_Frustum.testPoint(x, y, z);
    }

    public boolean sphereInFrustum(float x, float y, float z, float radius) {
        return m_Frustum.testSphere(x, y, z, radius);
    }

    public boolean cubeInFrustum(float x1, float y1, float z1, float x2, float y2, float z2) {
        return m_Frustum.testAab(x1, y1, z1, x2, y2, z2);
    }

    public boolean isVisible(AABBox aabb) {
        return m_Frustum.testAab(aabb.min, aabb.max);
    }
}
