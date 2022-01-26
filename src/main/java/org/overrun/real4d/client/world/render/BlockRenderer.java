package org.overrun.real4d.client.world.render;

import org.overrun.glutils.light.Direction;
import org.overrun.real4d.client.Real4D;
import org.overrun.real4d.world.block.Block;
import org.overrun.real4d.world.planet.Planet;

import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.overrun.glutils.light.Direction.*;
import static org.overrun.real4d.client.SpriteAtlases.BLOCK_ATLAS;
import static org.overrun.real4d.client.model.BlockModels.toTexFilePath;
import static org.overrun.real4d.util.ArrayUtil.append;

/**
 * @author squid233
 * @since 0.1.0
 */
public class BlockRenderer {
    protected final Real4D client;

    public BlockRenderer(final Real4D client) {
        this.client = client;
    }

    /**
     * This array layout is:
     * 3 vertex bytes, 2 tex bytes
     *
     * @return the data
     */
    public static float[] getData(
        Block block,
        Direction dir,
        int x0,
        int y0,
        int z0
    ) {
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;
        return switch (dir) {
            case WEST -> {
                var west = toTexFilePath(block.getTexture(WEST, false));
                var wu0 = BLOCK_ATLAS.getU0(west);
                var wv0 = BLOCK_ATLAS.getV0(west);
                var wu1 = BLOCK_ATLAS.getU1(west);
                var wv1 = BLOCK_ATLAS.getV1(west);
                yield new float[]{
                    x0, y1, z0, wu0, wv0,
                    x0, y0, z0, wu0, wv1,
                    x0, y1, z1, wu1, wv0,
                    x0, y1, z1, wu1, wv0,
                    x0, y0, z0, wu0, wv1,
                    x0, y0, z1, wu1, wv1
                };
            }
            case EAST -> {
                var east = toTexFilePath(block.getTexture(EAST, false));
                var eu0 = BLOCK_ATLAS.getU0(east);
                var ev0 = BLOCK_ATLAS.getV0(east);
                var eu1 = BLOCK_ATLAS.getU1(east);
                var ev1 = BLOCK_ATLAS.getV1(east);
                yield new float[]{
                    x1, y1, z1, eu0, ev0,
                    x1, y0, z1, eu0, ev1,
                    x1, y1, z0, eu1, ev0,
                    x1, y1, z0, eu1, ev0,
                    x1, y0, z1, eu0, ev1,
                    x1, y0, z0, eu1, ev1
                };
            }
            case DOWN -> {
                var down = toTexFilePath(block.getTexture(DOWN, false));
                var du0 = BLOCK_ATLAS.getU0(down);
                var dv0 = BLOCK_ATLAS.getV0(down);
                var du1 = BLOCK_ATLAS.getU1(down);
                var dv1 = BLOCK_ATLAS.getV1(down);
                yield new float[]{
                    x0, y0, z1, du0, dv0,
                    x0, y0, z0, du0, dv1,
                    x1, y0, z1, du1, dv0,
                    x1, y0, z1, du1, dv0,
                    x0, y0, z0, du0, dv1,
                    x1, y0, z0, du1, dv1
                };
            }
            case UP -> {
                var up = toTexFilePath(block.getTexture(UP, false));
                var uu0 = BLOCK_ATLAS.getU0(up);
                var uv0 = BLOCK_ATLAS.getV0(up);
                var uu1 = BLOCK_ATLAS.getU1(up);
                var uv1 = BLOCK_ATLAS.getV1(up);
                yield new float[]{
                    x0, y1, z0, uu0, uv0,
                    x0, y1, z1, uu0, uv1,
                    x1, y1, z0, uu1, uv0,
                    x1, y1, z0, uu1, uv0,
                    x0, y1, z1, uu0, uv1,
                    x1, y1, z1, uu1, uv1
                };
            }
            case NORTH -> {
                var north = toTexFilePath(block.getTexture(NORTH, false));
                var nu0 = BLOCK_ATLAS.getU0(north);
                var nv0 = BLOCK_ATLAS.getV0(north);
                var nu1 = BLOCK_ATLAS.getU1(north);
                var nv1 = BLOCK_ATLAS.getV1(north);
                yield new float[]{
                    x1, y1, z0, nu0, nv0,
                    x1, y0, z0, nu0, nv1,
                    x0, y1, z0, nu1, nv0,
                    x0, y1, z0, nu1, nv0,
                    x1, y0, z0, nu0, nv1,
                    x0, y0, z0, nu1, nv1
                };
            }
            default -> {
                var south = toTexFilePath(block.getTexture(SOUTH, false));
                var su0 = BLOCK_ATLAS.getU0(south);
                var sv0 = BLOCK_ATLAS.getV0(south);
                var su1 = BLOCK_ATLAS.getU1(south);
                var sv1 = BLOCK_ATLAS.getV1(south);
                yield new float[]{
                    x0, y1, z1, su0, sv0,
                    x0, y0, z1, su0, sv1,
                    x1, y1, z1, su1, sv0,
                    x1, y1, z1, su1, sv0,
                    x0, y0, z1, su0, sv1,
                    x1, y0, z1, su1, sv1
                };
            }
        };
    }

    public static float[] getData(
        Planet planet,
        Block block,
        int x,
        int y,
        int z,
        int layer
    ) {
        float[] dst = {};
        for (int i = 0; i < 6; i++) {
            var dir = Direction.getById(i);
            if (shouldRenderFace(
                planet,
                x + dir.getAxisX(),
                y + dir.getAxisY(),
                z + dir.getAxisZ(),
                layer
            )) {
                dst = append(dst,
                    getData(block,
                        dir,
                        x,
                        y,
                        z));
            }
        }
        return dst;
    }

    public static boolean shouldRenderFace(
        Planet planet,
        int x,
        int y,
        int z,
        int layer
    ) {
        return !planet.inIndex(x, y, z)
            || (!planet.isSolidBlock(x, y, z)
            && (planet.isLit(x, y, z) ^ (layer == 1)));
    }

    public void renderFace(Direction dir) {
        glDrawArrays(GL_TRIANGLE_STRIP, dir.getId() * 4, 4);
    }

    public void render(Predicate<Direction> condition) {
        for (int i = 0; i < 6; i++) {
            var dir = Direction.getById(i);
            if (condition.test(dir))
                glDrawArrays(GL_TRIANGLE_STRIP, dir.getId() * 4, 4);
        }
    }

    public void render(Planet planet,
                       int x,
                       int y,
                       int z,
                       int layer) {
        render(
            dir -> shouldRenderFace(planet,
                x + dir.getAxisX(),
                y + dir.getAxisY(),
                z + dir.getAxisZ(),
                layer)
        );
    }
}
