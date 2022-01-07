package org.overrun.real4d.client.gui;

import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.real4d.client.Real4DAtlas;
import org.overrun.real4d.util.Identifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public class DrawableHelper {
    public static void draw(Tesselator t,
                            Real4DAtlas atlas,
                            Identifier id,
                            int x,
                            int y,
                            int w,
                            int h) {
        var u0 = atlas.getU0(id);
        var u1 = atlas.getU1(id);
        var v0 = atlas.getV0(id);
        var v1 = atlas.getV1(id);
        var x1 = x + w;
        var y1 = y + h;
        t.vertexUV(x, y, 0, u0, v0)
            .vertexUV(x, y1, 0, u0, v1)
            .vertexUV(x1, y1, 0, u1, v1)
            .vertexUV(x1, y, 0, u1, v0);
    }
}
