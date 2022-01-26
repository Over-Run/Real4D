package org.overrun.real4d.client.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joml.Vector3i;
import org.overrun.glutils.light.Direction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.overrun.glutils.light.Direction.*;
import static org.overrun.real4d.client.SpriteAtlases.BLOCK_ATLAS;
import static org.overrun.real4d.client.model.BlockModels.toTexFilePath;

/**
 * @author squid233
 * @since 0.1.0
 */
public class BlockModel {
    public final Map<Direction, float[]> vertices = new HashMap<>();
    public final Map<Direction, String> textures = new HashMap<>();

    public BlockModel(Map<Direction, float[]> vertices,
                      Map<Direction, String> textures) {
        this.vertices.putAll(vertices);
        this.textures.putAll(textures);
    }

    public BlockModel() {
    }

    public static class Serializer extends TypeAdapter<BlockModel> {
        @Override
        public void write(JsonWriter out,
                          BlockModel value) throws IOException {
        }

        @Override
        public BlockModel read(JsonReader in) throws IOException {
            var model = new BlockModel();
            var from = new Vector3i();
            var to = new Vector3i();
            in.beginObject();
            while (in.hasNext()) {
                in.nextName();
                in.beginArray();
                in.beginObject();
                while (in.hasNext()) {
                    switch (in.nextName()) {
                        case "from" -> {
                            in.beginArray();
                            from.x = in.nextInt();
                            from.y = in.nextInt();
                            from.z = in.nextInt();
                            in.endArray();
                        }
                        case "to" -> {
                            in.beginArray();
                            to.x = in.nextInt();
                            to.y = in.nextInt();
                            to.z = in.nextInt();
                            in.endArray();
                        }
                        case "faces" -> {
                            in.beginObject();
                            while (in.hasNext()) {
                                var face = in.nextName();
                                in.beginObject();
                                while (in.hasNext()) {
                                    String tex = null, cullFace = null;
                                    switch (in.nextName()) {
                                        case "texture" -> tex = in.nextString();
                                        case "cullFace" -> cullFace = in.nextString();
                                    }
                                    var path = toTexFilePath(tex);
                                    var x0 = from.x / 16f;
                                    var y0 = from.y / 16f;
                                    var z0 = from.z / 16f;
                                    var x1 = to.x / 16f;
                                    var y1 = to.y / 16f;
                                    var z1 = to.z / 16f;
                                    var u0 = BLOCK_ATLAS.getU0(path);
                                    var v0 = BLOCK_ATLAS.getV0(path);
                                    var u1 = BLOCK_ATLAS.getU1(path);
                                    var v1 = BLOCK_ATLAS.getV1(path);
                                    switch (face) {
                                        case "west" -> {
                                            model.vertices.put(WEST, new float[]{
                                                x0, y1, z0, u0, v0,
                                                x0, y0, z0, u0, v1,
                                                x0, y1, z1, u1, v0,
                                                x0, y0, z1, u1, v1,
                                            });
                                            model.textures.put(WEST, tex);
                                        }
                                        case "east" -> {
                                            model.vertices.put(EAST, new float[]{
                                                x1, y1, z1, u0, v0,
                                                x1, y0, z1, u0, v1,
                                                x1, y1, z0, u1, v0,
                                                x1, y0, z0, u1, v1,
                                            });
                                            model.textures.put(EAST, tex);
                                        }
                                        case "down" -> {
                                            model.vertices.put(DOWN, new float[]{
                                                x0, y0, z1, u0, v0,
                                                x0, y0, z0, u0, v1,
                                                x1, y0, z1, u1, v0,
                                                x1, y0, z0, u1, v1
                                            });
                                            model.textures.put(DOWN, tex);
                                        }
                                        case "up" -> {
                                            model.vertices.put(UP, new float[]{
                                                x0, y1, z0, u0, v0,
                                                x0, y1, z1, u0, v1,
                                                x1, y1, z0, u1, v0,
                                                x1, y1, z1, u1, v1
                                            });
                                            model.textures.put(UP, tex);
                                        }
                                        case "north" -> {
                                            model.vertices.put(NORTH, new float[]{
                                                x1, y1, z0, u0, v0,
                                                x1, y0, z0, u0, v1,
                                                x0, y1, z0, u1, v0,
                                                x0, y0, z0, u1, v1
                                            });
                                            model.textures.put(NORTH, tex);
                                        }
                                        case "south" -> {
                                            model.vertices.put(SOUTH, new float[]{
                                                x0, y1, z1, u0, v0,
                                                x0, y0, z1, u0, v1,
                                                x1, y1, z1, u1, v0,
                                                x1, y0, z1, u1, v1
                                            });
                                            model.textures.put(SOUTH, tex);
                                        }
                                    }
                                }
                                in.endObject();
                            }
                            in.endObject();
                        }
                    }
                }
                in.endObject();
                in.endArray();
            }
            in.endObject();
            return model;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BlockModel.class.getSimpleName() + "[", "]")
            .add("vertices=" + vertices)
            .add("textures=" + textures)
            .toString();
    }
}
