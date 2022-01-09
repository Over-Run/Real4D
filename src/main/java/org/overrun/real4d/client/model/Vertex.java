package org.overrun.real4d.client.model;

import org.joml.Vector3f;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Vertex {
    public Vector3f pos;
    public float u;
    public float v;

    public Vertex(float x,
                  float y,
                  float z,
                  float u,
                  float v) {
        this(new Vector3f(x, y, z), u, v);
    }

    public Vertex(Vertex vertex,
                  float u,
                  float v) {
        this(vertex.pos, u, v);
    }

    public Vertex(Vector3f pos,
                  float u,
                  float v) {
        this.pos = pos;
        this.u = u;
        this.v = v;
    }

    public Vertex remap(float u,
                        float v) {
        return new Vertex(this, u, v);
    }
}
