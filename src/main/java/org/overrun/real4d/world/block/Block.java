package org.overrun.real4d.world.block;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.glutils.light.Direction;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.util.Registry;
import org.overrun.real4d.world.phys.AABBox;
import org.overrun.real4d.world.planet.Planet;

import java.util.Random;

import static org.overrun.glutils.light.Direction.*;
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
                       Vector3ic pos) {
        float c1 = 1.0f;
        float c2 = 0.8f;
        float c3 = 0.6f;
        for (int i = 0; i < 6; i++) {
            var dir = Direction.getById(i);
            if (shouldRenderFace(planet,
                dir.toVector().add(pos),
                layer)) {
                var abs = dir.toVector().absolute();
                if (abs.x == 1) {
                    t.color(c1, c1, c1);
                } else if (abs.y == 1) {
                    t.color(c2, c2, c2);
                } else if (abs.z == 1) {
                    t.color(c3, c3, c3);
                }
                renderFace(t, pos, dir);
            }
        }
    }

    public boolean shouldRenderFace(Planet planet,
                                    Vector3ic pos,
                                    int layer) {
        return !planet.inIndex(pos)
            || (!planet.isSolidBlock(pos)
            && (planet.isLit(pos) ^ (layer == 1)));
    }

    public void renderFace(Tesselator t,
                           Vector3ic pos,
                           Direction face) {
        var texSid = toTexFilePath(getTexture(NORTH, false));
        var u0 = BLOCK_ATLAS.getU0(texSid);
        var u1 = BLOCK_ATLAS.getU1(texSid);
        var v0 = BLOCK_ATLAS.getV0(texSid);
        var v1 = BLOCK_ATLAS.getV1(texSid);
        float u0o, u1o;
        float v0o, v1o;
        if (hasTexOverlay(NORTH)) {
            var texOverlay = toTexFilePath(getTexture(NORTH, true));
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
        float x0 = pos.x();
        float x1 = pos.x() + 1;
        float y0 = pos.y();
        float y1 = pos.y() + 1;
        float z0 = pos.z();
        float z1 = pos.z() + 1;
        switch (face) {
            case WEST -> {
                t.vertexUV(x0, y1, z0, u0, v0)
                    .vertexUV(x0, y0, z0, u0, v1)
                    .vertexUV(x0, y0, z1, u1, v1)
                    .vertexUV(x0, y1, z1, u1, v0);
                if (hasTexOverlay(WEST)) {
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
                if (hasTexOverlay(EAST)) {
                    t.vertexUV(x1, y1, z1, u0o, v0o)
                        .vertexUV(x1, y0, z1, u0o, v1o)
                        .vertexUV(x1, y0, z0, u1o, v1o)
                        .vertexUV(x1, y1, z0, u1o, v0o);
                }
            }
            case DOWN -> {
                var texBtm = toTexFilePath(getTexture(DOWN, false));
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
                var texTop = toTexFilePath(getTexture(UP, false));
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
                if (hasTexOverlay(NORTH)) {
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
                if (hasTexOverlay(SOUTH)) {
                    t.vertexUV(x0, y1, z1, u0o, v0o)
                        .vertexUV(x0, y0, z1, u0o, v1o)
                        .vertexUV(x1, y0, z1, u1o, v1o)
                        .vertexUV(x1, y1, z1, u1o, v0o);
                }
            }
        }
    }

    public void pickOutline(Tesselator t,
                            Vector3ic pos,
                            Direction face) {
        var outline = getOutline().moveNew(
            pos.x(),
            pos.y(),
            pos.z());
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
    }

    public void randomTick(Planet planet,
                           Vector3i pos,
                           Random random) {
    }

    public boolean hasTexOverlay(Direction dir) {
        return getTexture(dir, true) != null;
    }

    public Identifier getTexture(Direction dir,
                                 boolean overlay) {
        if (overlay) return null;
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
