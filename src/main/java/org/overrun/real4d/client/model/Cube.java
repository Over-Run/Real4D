package org.overrun.real4d.client.model;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Cube {
    private Polygon[] polygons;
    private final int maxU;
    private final int maxV;
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

    public Cube(int maxU,
                int maxV,
                int xTexOffs,
                int yTexOffs) {
        this.maxU = maxU;
        this.maxV = maxV;
        setTexOffs(xTexOffs, yTexOffs);
    }

    public Cube(int xTexOffs,
                int yTexOffs) {
        this(64, 64, xTexOffs, yTexOffs);
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
        var x1 = x0 + w;
        var y1 = y0 + h;
        var z1 = z0 + d;
        // The 1st, 2nd, 3rd, 4th vertex on axis-z 0 or 1
        var z00 = new Vertex(x0, y1, z0, 8, 0);
        var z01 = new Vertex(x0, y0, z0, 8, 8);
        var z02 = new Vertex(x1, y0, z0, 0, 8);
        var z03 = new Vertex(x1, y1, z0, 0, 0);
        var z10 = new Vertex(x0, y1, z1, 0, 0);
        var z11 = new Vertex(x0, y0, z1, 0, 8);
        var z12 = new Vertex(x1, y0, z1, 8, 8);
        var z13 = new Vertex(x1, y1, z1, 8, 0);
        // West
        polygons[0] = new Polygon(
            maxU, maxV,
            xTexOffs,
            yTexOffs + d,
            xTexOffs + d,
            yTexOffs + d + h,
            z00, z01, z11, z10
        );
        // East
        polygons[1] = new Polygon(
            maxU, maxV,
            xTexOffs + d + w,
            yTexOffs + d,
            xTexOffs + d + w + d,
            yTexOffs + d + h,
            z13, z12, z02, z03
        );
        // Down
        polygons[2] = new Polygon(
            maxU, maxV,
            xTexOffs + d + w,
            yTexOffs,
            xTexOffs + d + w + d,
            yTexOffs + d,
            z11, z01, z02, z12
        );
        // Up
        polygons[3] = new Polygon(
            maxU, maxV,
            xTexOffs + d,
            yTexOffs,
            xTexOffs + d + w,
            yTexOffs + d,
            z00, z10, z13, z03
        );
        // North
        polygons[4] = new Polygon(
            maxU, maxV,
            xTexOffs + d,
            yTexOffs + d,
            xTexOffs + d + w,
            yTexOffs + d + h,
            z03, z02, z01, z00
        );
        // South
        polygons[5] = new Polygon(
            maxU, maxV,
            xTexOffs + d + w + d,
            yTexOffs + d,
            xTexOffs + d + w + d + w,
            yTexOffs + d + h,
            z10, z11, z12, z13
        );
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
        glRotated(zRot, 0, 0, 1);
        glRotated(yRot, 0, 1, 0);
        glRotated(xRot, 1, 0, 0);
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
