package org.overrun.real4d.client.gl;

import static org.lwjgl.opengl.GL11.*;
import static org.overrun.glutils.FilesReader.lines;
import static org.overrun.real4d.asset.AssetManager.makePath;

/**
 * @author squid233
 * @since 0.1.0
 */
public class GLStateMgr {
    public static boolean texture2DEnabled;
    public static int texture2DId;
    public static TextGLProgram textGLProgram;

    public static void initPrograms() {
        textGLProgram = new TextGLProgram();
        textGLProgram.createVsh(lines(
            GLStateMgr.class,
            makePath(TextGLProgram.VERT_SHADER)
        ));
        textGLProgram.createFsh(lines(
            GLStateMgr.class,
            makePath(TextGLProgram.FRAG_SHADER)
        ));
        textGLProgram.link();
        textGLProgram.bind();
        textGLProgram.setUniform("texture2D_sampler", 0);
        textGLProgram.unbind();
    }

    public static void init() {
        enableTexture2D();
        glClearColor(0.4f, 0.6f, 0.9f, 0.0f);
        glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.5f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        initPrograms();
    }

    public static void free() {
        if (textGLProgram != null) {
            textGLProgram.free();
        }
    }

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
