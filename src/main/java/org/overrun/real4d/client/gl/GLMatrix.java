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
        return new Matrix4f().set(getProjectionf());
    }

    public static Matrix4f getModelview() {
        return new Matrix4f().set(getModelviewf());
    }

    public static Matrix4f getTexture() {
        return new Matrix4f().set(getTexturef());
    }
}
