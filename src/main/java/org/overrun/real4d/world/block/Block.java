package org.overrun.real4d.world.block;

import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.glutils.light.Direction;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.util.Registry;
import org.overrun.real4d.world.phys.AABBox;
import org.overrun.real4d.world.planet.Planet;

import java.util.Random;

import static java.lang.Math.abs;
import static org.overrun.real4d.client.SpriteAtlases.BLOCK_ATLAS;
import static org.overrun.real4d.client.model.BlockModels.toTexFilePath;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Block {
    public void render(Tesselator t,
                       Planet planet,
                       int layer,
                       int x,
                       int y,
                       int z) {
        float c1 = 1.0f;
        float c2 = 0.8f;
        float c3 = 0.6f;
        for (int i = 0; i < 6; i++) {
            var vec = Direction.getById(i);
            var ax = vec.getAxisX();
            var ay = vec.getAxisY();
            var az = vec.getAxisZ();
            if (shouldRenderFace(planet,
                x + ax,
                y + ay,
                z + az,
                layer)) {
                if (abs(ax) == 1) {
                    t.color(c1, c1, c1);
                } else if (abs(ay) == 1) {
                    t.color(c2, c2, c2);
                } else if (abs(az) == 1) {
                    t.color(c3, c3, c3);
                }
                renderFace(t, x, y, z, vec);
            }
        }
    }

    public boolean shouldRenderFace(Planet planet,
                                    int x,
                                    int y,
                                    int z,
                                    int layer) {
        return !planet.inIndex(x, y, z)
            || (!planet.isSolidBlock(x, y, z)
            && (planet.isLit(x, y, z) ^ (layer == 1)));
    }

    public void renderFace(Tesselator t,
                           int x,
                           int y,
                           int z,
                           Direction face) {
        var texSid = toTexFilePath(getTexSid());
        var u0 = BLOCK_ATLAS.getU0(texSid);
        var u1 = BLOCK_ATLAS.getU1(texSid);
        var v0 = BLOCK_ATLAS.getV0(texSid);
        var v1 = BLOCK_ATLAS.getV1(texSid);
        float u0o, u1o;
        float v0o, v1o;
        if (hasTexSidOverlay()) {
            var texOverlay = toTexFilePath(getTexSidOverlay());
            u0o = BLOCK_ATLAS.getU0(texOverlay);
            u1o = BLOCK_ATLAS.getU1(texOverlay);
            v0o = BLOCK_ATLAS.getV0(texOverlay);
            v1o = BLOCK_ATLAS.getV1(texOverlay);
        } else {
            u0o = 0;
            u1o = 0;
            v0o = 0;
            v1o = 0;
        }
        float x0 = (float) x;
        float x1 = x + 1;
        float y0 = (float) y;
        float y1 = y + 1;
        float z0 = (float) z;
        float z1 = z + 1;
        switch (face) {
            case WEST -> {
                t.vertexUV(x0, y1, z0, u0, v0)
                    .vertexUV(x0, y0, z0, u0, v1)
                    .vertexUV(x0, y0, z1, u1, v1)
                    .vertexUV(x0, y1, z1, u1, v0);
                if (hasTexSidOverlay()) {
                    t.vertexUV(x0, y1, z0, u0o, v0o)
                        .vertexUV(x0, y0, z0, u0o, v1o)
                        .vertexUV(x0, y0, z1, u1o, v1o)
                        .vertexUV(x0, y1, z1, u1o, v0o);
                }
            }
            case EAST -> {
                t.vertexUV(x1, y1, z1, u0, v0)
                    .vertexUV(x1, y0, z1, u0, v1)
                    .vertexUV(x1, y0, z0, u1, v1)
                    .vertexUV(x1, y1, z0, u1, v0);
                if (hasTexSidOverlay()) {
                    t.vertexUV(x1, y1, z1, u0o, v0o)
                        .vertexUV(x1, y0, z1, u0o, v1o)
                        .vertexUV(x1, y0, z0, u1o, v1o)
                        .vertexUV(x1, y1, z0, u1o, v0o);
                }
            }
            case DOWN -> {
                var texBtm = toTexFilePath(getTexBtm());
                u0 = BLOCK_ATLAS.getU0(texBtm);
                u1 = BLOCK_ATLAS.getU1(texBtm);
                v0 = BLOCK_ATLAS.getV0(texBtm);
                v1 = BLOCK_ATLAS.getV1(texBtm);
                t.vertexUV(x0, y0, z1, u0, v0)
                    .vertexUV(x0, y0, z0, u0, v1)
                    .vertexUV(x1, y0, z0, u1, v1)
                    .vertexUV(x1, y0, z1, u1, v0);
            }
            case UP -> {
                var texTop = toTexFilePath(getTexTop());
                u0 = BLOCK_ATLAS.getU0(texTop);
                u1 = BLOCK_ATLAS.getU1(texTop);
                v0 = BLOCK_ATLAS.getV0(texTop);
                v1 = BLOCK_ATLAS.getV1(texTop);
                t.vertexUV(x0, y1, z0, u0, v0)
                    .vertexUV(x0, y1, z1, u0, v1)
                    .vertexUV(x1, y1, z1, u1, v1)
                    .vertexUV(x1, y1, z0, u1, v0);
            }
            case NORTH -> {
                t.vertexUV(x1, y1, z0, u0, v0)
                    .vertexUV(x1, y0, z0, u0, v1)
                    .vertexUV(x0, y0, z0, u1, v1)
                    .vertexUV(x0, y1, z0, u1, v0);
                if (hasTexSidOverlay()) {
                    t.vertexUV(x1, y1, z0, u0o, v0o)
                        .vertexUV(x1, y0, z0, u0o, v1o)
                        .vertexUV(x0, y0, z0, u1o, v1o)
                        .vertexUV(x0, y1, z0, u1o, v0o);
                }
            }
            case SOUTH -> {
                t.vertexUV(x0, y1, z1, u0, v0)
                    .vertexUV(x0, y0, z1, u0, v1)
                    .vertexUV(x1, y0, z1, u1, v1)
                    .vertexUV(x1, y1, z1, u1, v0);
                if (hasTexSidOverlay()) {
                    t.vertexUV(x0, y1, z1, u0o, v0o)
                        .vertexUV(x0, y0, z1, u0o, v1o)
                        .vertexUV(x1, y0, z1, u1o, v1o)
                        .vertexUV(x1, y1, z1, u1o, v0o);
                }
            }
        }
    }

    public void pickOutline(Tesselator t,
                            int x,
                            int y,
                            int z,
                            Direction face) {
        var outline = getOutline().moveNew(x, y, z);
        float x0 = outline.min.x;
        float x1 = outline.max.x;
        float y0 = outline.min.y;
        float y1 = outline.max.y;
        float z0 = outline.min.z;
        float z1 = outline.max.z;
        switch (face) {
            case WEST -> t.vertex(x0, y1, z0)
                .vertex(x0, y0, z0)
                .vertex(x0, y0, z1)
                .vertex(x0, y1, z1);
            case EAST -> t.vertex(x1, y1, z1)
                .vertex(x1, y0, z1)
                .vertex(x1, y0, z0)
                .vertex(x1, y1, z0);
            case DOWN -> t.vertex(x0, y0, z1)
                .vertex(x0, y0, z0)
                .vertex(x1, y0, z0)
                .vertex(x1, y0, z1);
            case UP -> t.vertex(x0, y1, z0)
                .vertex(x0, y1, z1)
                .vertex(x1, y1, z1)
                .vertex(x1, y1, z0);
            case NORTH -> t.vertex(x1, y1, z0)
                .vertex(x1, y0, z0)
                .vertex(x0, y0, z0)
                .vertex(x0, y1, z0);
            case SOUTH -> t.vertex(x0, y1, z1)
                .vertex(x0, y0, z1)
                .vertex(x1, y0, z1)
                .vertex(x1, y1, z1);
        }
        ;
    }

    public void randomTick(Planet planet,
                           int x,
                           int y,
                           int z,
                           Random random) {
    }

    public boolean hasTexSidOverlay() {
        return getTexSidOverlay() != null;
    }

    public Identifier getTexTop() {
        return getId();
    }

    public Identifier getTexSid() {
        return getId();
    }

    public Identifier getTexSidOverlay() {
        return null;
    }

    public Identifier getTexBtm() {
        return getId();
    }

    public boolean isOpaque() {
        return true;
    }

    public boolean isSolid() {
        return true;
    }

    public boolean isAir() {
        return false;
    }

    public AABBox getCollision() {
        return getOutline();
    }

    public AABBox getOutline() {
        return AABBox.fullCube();
    }

    public final Identifier getId() {
        return Registry.BLOCK.getId(this);
    }

    public final int getRawId() {
        return Registry.BLOCK.getRawId(this);
    }
}
