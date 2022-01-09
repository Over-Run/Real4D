package org.overrun.real4d.client.model;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Polygon {
    public Vertex[] vertices;
    public int vertexCount;

    public Polygon(Vertex... vertices) {
        this.vertices = vertices;
        this.vertexCount = vertices.length;
    }

    public Polygon(int u0,
                   int v0,
                   int u1,
                   int v1,
                   Vertex... vertices) {
        this(vertices);
        vertices[0] = vertices[0].remap(u1, v0);
        vertices[1] = vertices[1].remap(u0, v0);
        vertices[2] = vertices[2].remap(u0, v1);
        vertices[3] = vertices[3].remap(u1, v1);
    }

    public void render() {
        glColor3f(1, 1, 1);
        for (int i = 3; i >= 0; --i) {
            var v = vertices[i];
            glTexCoord2f(v.u / 64f, v.v / 64f);
            glVertex3f(v.pos.x, v.pos.y, v.pos.z);
        }
    }
}
