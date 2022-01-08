package org.overrun.real4d.client.gl;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLMatrix {
    private static final float[] projf = new float[16];
    private static final float[] modlf = new float[16];
    private static final float[] texrf = new float[16];
    private static final Matrix4f proj = new Matrix4f();
    private static final Matrix4f modl = new Matrix4f();
    private static final Matrix4f texr = new Matrix4f();

    public static float[] getProjectionf() {
        glGetFloatv(GL_PROJECTION_MATRIX, projf);
        return projf;
    }

    public static float[] getModelviewf() {
        glGetFloatv(GL_MODELVIEW_MATRIX, modlf);
        return modlf;
    }

    public static float[] getTexturef() {
        glGetFloatv(GL_TEXTURE_MATRIX, texrf);
        return texrf;
    }

    public static Matrix4f getProjection() {
        return proj.set(getProjectionf());
    }

    public static Matrix4f getModelview() {
        return modl.set(getModelviewf());
    }

    public static Matrix4f getTexture() {
        return texr.set(getTexturef());
    }
}
