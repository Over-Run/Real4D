package org.overrun.real4d.client.gl;

import org.overrun.glutils.gl.Vao;
import org.overrun.glutils.gl.Vbo;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Mesh {
    public Vao vao;
    public Vbo vbo;
    public Vbo ibo;

    public void init() {
        vao = new Vao();
        vao.bind();
        vbo = new Vbo(GL_ARRAY_BUFFER);
        vbo.bind();
        vbo.unbind();
        vao.unbind();
    }

    public void free() {
        vao.free();
        vbo.free();
        if (ibo != null) {
            ibo.free();
        }
    }
}
