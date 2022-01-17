package org.overrun.real4d.client.gl;

import org.overrun.glutils.gl.GLProgram;
import org.overrun.glutils.gl.Vao;
import org.overrun.glutils.gl.Vbo;
import org.overrun.glutils.gl.VertexAttrib;
import org.overrun.real4d.util.Identifier;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.overrun.glutils.FilesReader.lines;
import static org.overrun.real4d.asset.AssetManager.makePath;
import static org.overrun.real4d.client.gl.GLStateMgr.texture2DEnabled;

/**
 * @author squid233
 * @since 0.1.0
 */
public class ImmeModeSim {
    private static final ImmeModeSim INSTANCE = new ImmeModeSim();
    public static final int VERTEX_COUNT = 60000;
    public static final int MEMORY_USE = (2 + 4 + 3) * VERTEX_COUNT;
    /**
     * The array layout is:<br>
     * 2 texture coord bytes, 4 color bytes, 3 vertex bytes
     */
    private final float[] array = new float[MEMORY_USE];
    private final FloatBuffer buffer = memAllocFloat(MEMORY_USE);
    private final GLProgram program = new GLProgram();
    private final Vao vao = new Vao();
    private final Vbo vbo = new Vbo(GL_ARRAY_BUFFER);
    private final VertexAttrib aPos = new VertexAttrib(0);
    private final VertexAttrib aColor = new VertexAttrib(1);
    private final VertexAttrib aTexCoords = new VertexAttrib(2);
    private float r, g, b, a, u, v;
    private boolean hasColor, hasTexture;
    private int primitive;
    private int vertices;
    private int pos;
    private boolean isDrawing;

    private ImmeModeSim() {
        program.createVsh(lines(this,
            makePath(new Identifier("shaders/ims.vert"))));
        program.createFsh(lines(this,
            makePath(new Identifier("shaders/ims.frag"))));
        program.link();
        program.bind();
        program.setUniform("texture2D_sampler", 0);
        program.unbind();
        vao.bind();
        vao.unbind();
        vbo.bind();
        vbo.unbind();
    }

    public static ImmeModeSim getInstance() {
        return INSTANCE;
    }

    public void begin(int primitive) {
        this.primitive = primitive;
        isDrawing = true;
        hasColor = false;
        hasTexture = false;
        vertices = 0;
        pos = 0;
        buffer.clear();
    }

    public void color(float r, float g, float b, float a) {
        hasColor = true;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void color(float r, float g, float b) {
        color(r, g, b, 1);
    }

    public void texCoord(float u, float v) {
        hasTexture = true;
        this.u = u;
        this.v = v;
    }

    public void vertex(float x, float y, float z) {
        if (!isDrawing) {
            System.err.println("Don't call vertex(f,f,f) outside of a begin/end pair.");
        }
        if (hasTexture) {
            array[pos++] = u;
            array[pos++] = v;
        }
        if (hasColor) {
            array[pos++] = r;
            array[pos++] = g;
            array[pos++] = b;
            array[pos++] = a;
        }
        array[pos++] = x;
        array[pos++] = y;
        array[pos++] = z;
        ++vertices;
    }

    public void vertex(float x, float y) {
        vertex(x, y, 0);
    }

    public void end() {
        isDrawing = false;
        program.bind();
        program.setUniformMat4("proj", GLMatrix.getProjection());
        program.setUniformMat4("modelView", GLMatrix.getModelView());
        program.setUniform("has_color", hasColor);
        program.setUniform("texture2D_enabled", texture2DEnabled);
        buffer.clear().put(array, 0, pos);
        if (buffer.position() > 0) {
            buffer.flip();
        }
        vao.bind();
        vbo.bind();
        vbo.data(buffer, GL_DYNAMIC_DRAW);

        int stride = ((hasTexture ? 2 : 0) + (hasColor ? 4 : 0) + 3) * Float.BYTES;
        aPos.enable();
        aPos.pointer(3,
            GL_FLOAT,
            false,
            stride,
            hasTexture ? (hasColor ? 6 : 2) : (hasColor ? 4 : 0));
        if (hasColor) {
            aColor.enable();
            aColor.pointer(4,
                GL_FLOAT,
                false,
                stride,
                hasTexture ? 2 : 0);
        } else {
            aColor.disable();
        }
        if (hasTexture) {
            aTexCoords.enable();
            aTexCoords.pointer(2,
                GL_FLOAT,
                false,
                stride,
                0);
        } else {
            aTexCoords.disable();
        }

        vbo.unbind();

        glDrawArrays(primitive, 0, vertices);

        vao.unbind();
        program.unbind();
    }

    public void free() {
        program.free();
        vao.free();
        vbo.free();
        memFree(buffer);
    }
}
