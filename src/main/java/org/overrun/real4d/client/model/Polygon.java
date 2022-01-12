package org.overrun.real4d.client.model;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Polygon {
    public Vertex[] vertices;
    public int vertexCount;
    public float maxU;
    public float maxV;

    public Polygon(float maxU,
                   float maxV,
                   Vertex... vertices) {
        this.maxU = maxU;
        this.maxV = maxV;
        this.vertices = vertices;
        this.vertexCount = vertices.length;
    }

    public Polygon(float maxU,
                   float maxV,
                   int u0,
                   int v0,
                   int u1,
                   int v1,
                   Vertex... vertices) {
        this(maxU, maxV, vertices);
        vertices[0] = vertices[0].remap(u0, v0);
        vertices[1] = vertices[1].remap(u0, v1);
        vertices[2] = vertices[2].remap(u1, v1);
        vertices[3] = vertices[3].remap(u1, v0);
    }

    public void render() {
        glColor3f(1, 1, 1);
        for (var v : vertices) {
            glTexCoord2f(v.u / maxU, v.v / maxV);
            glVertex3f(v.pos.x, v.pos.y, v.pos.z);
        }
    }
}
