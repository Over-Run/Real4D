package org.overrun.real4d.client.gl;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLStateMgr {
    public static boolean texture2DEnabled;
    public static int texture2DId;

    ///////////////////////////////////////////////////////////////////////////
    // Texture2D
    ///////////////////////////////////////////////////////////////////////////

    public static void enableTexture2D() {
        if (!texture2DEnabled) {
            glEnable(GL_TEXTURE_2D);
        }
        texture2DEnabled = true;
    }

    public static void bindTexture2D(int id) {
        if (texture2DId != id) {
            texture2DId = id;
            glBindTexture(GL_TEXTURE_2D, id);
        }
    }

    public static void disableTexture2D() {
        if (texture2DEnabled) {
            glDisable(GL_TEXTURE_2D);
        }
        texture2DEnabled = false;
    }
}
