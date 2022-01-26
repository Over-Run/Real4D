package org.overrun.real4d.client.gl;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLStateMgr {
    public static void init() {
        enableTexture2D();
        glClearColor(0.4f, 0.6f, 0.9f, 1.0f);
        glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        enableAlphaTest();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Texture2D
    ///////////////////////////////////////////////////////////////////////////

    public static boolean texture2DEnabled;
    public static int texture2DId;

    public static void enableTexture2D() {
        texture2DEnabled = true;
    }

    public static void bindTexture2D(int id) {
        if (texture2DId != id) {
            texture2DId = id;
            glBindTexture(GL_TEXTURE_2D, id);
        }
    }

    public static void disableTexture2D() {
        texture2DEnabled = false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Alpha Test
    ///////////////////////////////////////////////////////////////////////////

    public static boolean alphaTestEnabled;

    public static void enableAlphaTest() {
        alphaTestEnabled = true;
    }

    public static void disableAlphaTest() {
        alphaTestEnabled = false;
    }
}
