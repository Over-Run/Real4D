package org.overrun.real4d.client.gl;

import org.joml.Matrix4fStack;

import static org.joml.Math.toRadians;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLMatrix {
    public static final Matrix4fStack proj = new Matrix4fStack(4);
    public static final Matrix4fStack modelView = new Matrix4fStack(32);
    public static Matrix4fStack matMode = proj;

    public static void setMode(int mode) {
        switch (mode) {
            case GL_PROJECTION -> matMode = proj;
            case GL_MODELVIEW -> matMode = modelView;
        }
    }

    public static void setPerspectived(float fovy, float aspect, float zNear, float zFar) {
        setPerspective(toRadians(fovy), aspect, zNear, zFar);
    }

    public static void setPerspective(float fovy, float aspect, float zNear, float zFar) {
        matMode.setPerspective(fovy, aspect, zNear, zFar);
    }

    public static void setOrtho(float left, float right, float bottom, float top, float zNear, float zFar) {
        matMode.setOrtho(left, right, bottom, top, zNear, zFar);
    }

    public static void translate(float x, float y, float z) {
        matMode.translate(x, y, z);
    }

    public static void translation(float x, float y, float z) {
        matMode.translation(x, y, z);
    }

    public static void rotated(float ang, float x, float y, float z) {
        rotate(toRadians(ang), x, y, z);
    }

    public static void rotate(float ang, float x, float y, float z) {
        matMode.rotate(ang, x, y, z);
    }

    public static void scale(float x, float y, float z) {
        matMode.scale(x, y, z);
    }

    public static void loadIdentity() {
        matMode.identity();
    }

    public static void pushMatrix() {
        matMode.pushMatrix();
    }

    public static void popMatrix() {
        matMode.popMatrix();
    }

    public static Matrix4fStack get() {
        return matMode;
    }
}
