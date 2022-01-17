package org.overrun.real4d.client.gl;

import org.overrun.glutils.gl.GLProgram;
import org.overrun.real4d.util.Identifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public class TextGLProgram extends GLProgram {
    public static final Identifier
        VERT_SHADER = new Identifier("shaders/gui/text.vert"),
        FRAG_SHADER = new Identifier("shaders/gui/text.frag");
    public float r = 1;
    public float g = 1;
    public float b = 1;
    public float a = 1;

    public void color(float r, float g, float b, float a) {
        color(r, g, b);
        this.a = a;
    }

    public void color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public float[] colorsData() {
        return new float[]{
            r,
            g,
            b,
            a
        };
    }
}
