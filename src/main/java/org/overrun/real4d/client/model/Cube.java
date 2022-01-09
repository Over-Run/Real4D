package org.overrun.real4d.client.model;

import static java.lang.Math.toDegrees;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Cube {
    private Polygon[] polygons;
    private int xTexOffs;
    private int yTexOffs;
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    private boolean compiled = false;
    private int list = 0;

    public Cube(int xTexOffs,
                int yTexOffs) {
        setTexOffs(xTexOffs, yTexOffs);
    }

    public void setTexOffs(int xTexOffs, int yTexOffs) {
        this.xTexOffs = xTexOffs;
        this.yTexOffs = yTexOffs;
    }

    public void addBox(float x0,
                       float y0,
                       float z0,
                       int w,
                       int h,
                       int d) {
        polygons = new Polygon[6];
        float x1 = x0 + (float) w;
        float y1 = y0 + (float) h;
        float z1 = z0 + (float) d;
        var u0 = new Vertex(x0, y0, z0, 0, 0);
        var u1 = new Vertex(x1, y0, z0, 0, 8);
        var u2 = new Vertex(x1, y1, z0, 8, 8);
        var u3 = new Vertex(x0, y1, z0, 8, 0);
        var l0 = new Vertex(x0, y0, z1, 0, 0);
        var l1 = new Vertex(x1, y0, z1, 0, 8);
        var l2 = new Vertex(x1, y1, z1, 8, 8);
        var l3 = new Vertex(x0, y1, z1, 8, 0);
        polygons[0] = new Polygon(xTexOffs + d + w, yTexOffs + d, xTexOffs + d + w + d, yTexOffs + d + h, l1, u1, u2, l2);
        polygons[1] = new Polygon(xTexOffs, yTexOffs + d, xTexOffs + d, yTexOffs + d + h, u0, l0, l3, u3);
        polygons[2] = new Polygon(xTexOffs + d, yTexOffs, xTexOffs + d + w, yTexOffs + d, l1, l0, u0, u1);
        polygons[3] = new Polygon(xTexOffs + d + w, yTexOffs, xTexOffs + d + w + w, yTexOffs + d, u2, u3, l3, l2);
        polygons[4] = new Polygon(xTexOffs + d, yTexOffs + d, xTexOffs + d + w, yTexOffs + d + h, u1, u0, u3, u2);
        polygons[5] = new Polygon(xTexOffs + d + w + d, yTexOffs + d, xTexOffs + d + w + d + w, yTexOffs + d + h, l0, l1, l2, l3);
    }

    public void setPos(float x,
                       float y,
                       float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void render() {
        if (!compiled) {
            compile();
        }

        glPushMatrix();
        glTranslatef(x, y, z);
        glRotated(toDegrees(zRot), 0, 0, 1);
        glRotated(toDegrees(yRot), 0, 1, 0);
        glRotated(toDegrees(xRot), 1, 0, 0);
        glCallList(list);
        glPopMatrix();
    }

    private void compile() {
        list = glGenLists(1);
        glNewList(list, GL_COMPILE);
        glBegin(GL_QUADS);
        for (var polygon : polygons) {
            polygon.render();
        }
        glEnd();
        glEndList();
        compiled = true;
    }
}
