package org.overrun.real4d.client.gl;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLMatrix {
    private static final float[] projf = new float[16];
    private static final float[] modlf = new float[16];
    private static final Matrix4fStack proj = new Matrix4fStack(2);
    private static final Matrix4fStack modelView = new Matrix4fStack(32);

    public static float[] getProjectionf() {
        glGetFloatv(GL_PROJECTION_MATRIX, projf);
        return projf;
    }

    public static float[] getModelviewf() {
        glGetFloatv(GL_MODELVIEW_MATRIX, modlf);
        return modlf;
    }

    public static Matrix4f getProjectionl() {
        return new Matrix4f().set(getProjectionf());
    }

    public static Matrix4f getModelviewl() {
        return new Matrix4f().set(getModelviewf());
    }

    public static Matrix4fStack getProjection() {
        return proj;
    }

    public static Matrix4fStack getModelView() {
        return modelView;
    }
}
