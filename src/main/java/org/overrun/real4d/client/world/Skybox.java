package org.overrun.real4d.client.world;

import org.overrun.glutils.gl.ll.Tesselator;
import org.overrun.glutils.tex.TexParam;
import org.overrun.glutils.tex.stitch.Stitcher;
import org.overrun.glutils.tex.stitch.StrSpriteAtlas;
import org.overrun.real4d.util.Identifier;

import static org.lwjgl.opengl.GL12.*;
import static org.overrun.real4d.asset.AssetManager.makePath;
import static org.overrun.real4d.asset.AssetType.TEXTURES;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Skybox {
    public static final int MAP_WIDTH = 2048;
    public static final int CELL_WIDTH = 16;
    public static final int MAP = MAP_WIDTH * CELL_WIDTH / 2;
    private static final float[] skyboxVertices = {
        // positions
        -1.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 1.0f,

        -1.0f, -1.0f, 1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, 1.0f,

        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,

        -1.0f, -1.0f, 1.0f,
        -1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        -1.0f, -1.0f, 1.0f,

        -1.0f, 1.0f, -1.0f,
        1.0f, 1.0f, -1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, -1.0f,

        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f
    };
    public static final Identifier
        RIGHT = new Identifier("skybox/right.jpg"),
        LEFT = new Identifier("skybox/left.jpg"),
        TOP = new Identifier("skybox/top.jpg"),
        BOTTOM = new Identifier("skybox/bottom.jpg"),
        FRONT = new Identifier("skybox/front.jpg"),
        BACK = new Identifier("skybox/back.jpg");
    //public final int id = Textures.gen();
    public final StrSpriteAtlas atlas;

    public Skybox() {
        //glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        atlas = Stitcher.stitchStb(Skybox.class,
            new TexParam()
                .minFilter(GL_LINEAR_MIPMAP_NEAREST)
                .magFilter(GL_LINEAR)
                .wrapS(GL_CLAMP_TO_EDGE)
                .wrapT(GL_CLAMP_TO_EDGE),
            makePath(TEXTURES, RIGHT),
            makePath(TEXTURES, LEFT),
            makePath(TEXTURES, TOP),
            makePath(TEXTURES, BOTTOM),
            makePath(TEXTURES, FRONT),
            makePath(TEXTURES, BACK));
        /*var bytes = new ArrayList<Byte>();
        try (var stack = MemoryStack.stackPush()) {
            var pw = stack.mallocInt(1);
            var ph = stack.mallocInt(1);
            var pc = stack.mallocInt(1);
            int i = 0;
            for (var faceID : faces) {
                var face = AssetManager.makePath(
                    AssetType.TEXTURES,
                    faceID
                );
                try (var is = requireNonNull(
                    Skybox.class.getClassLoader()
                        .getResourceAsStream(face)
                ); var bis = new BufferedInputStream(is)) {
                    int read;
                    while ((read = bis.read()) != -1) {
                        bytes.add((byte) read);
                    }
                    var arr = new byte[bytes.size()];
                    for (int j = 0; j < arr.length; j++) {
                        arr[j] = bytes.get(j);
                    }
                    var bb = MemoryUtil.memAlloc(arr.length).put(arr).flip();
                    var data = stbi_load_from_memory(bb,
                        pw,
                        ph,
                        pc,
                        STBI_rgb);
                    bytes.clear();
                    if (data == null) {
                        Images.thrRE(face);
                    }
                    glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                        0,
                        GL_RGB,
                        pw.get(0),
                        ph.get(0),
                        0,
                        GL_RGB,
                        GL_UNSIGNED_BYTE,
                        data);
                    stbi_image_free(data);
                }
                if (hasGenMipmap()) {
                    glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
                }
                ++i;
            }
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        } catch (IOException e) {
            throw new RuntimeException();
        }*/
    }

    public void render(float x,
                       float y,
                       float z,
                       float bw,
                       float bh,
                       float bd) {
        boolean isLit = glGetBoolean(GL_LIGHTING);

        float w = MAP * bw / (MAP_WIDTH / 128f);
        float h = MAP * bh / (MAP_WIDTH / 128f);
        float d = MAP * bd / (MAP_WIDTH / 128f);

        x += MAP / 8f - w / 2;
        y += MAP / 24f - h / 2;
        z += MAP / 8f - d / 2;

        if (isLit) glDisable(GL_LIGHTING);

        glPushMatrix();
        glTranslatef(-x, -y, -z);
        /*x=0;
        y=0;
        z=0;
        w=1;
        h=1;
        d=1;*/

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        atlas.bind();
        var right = makePath(TEXTURES, RIGHT);
        var ru0 = atlas.getU0(right);
        var rv0 = atlas.getV0(right);
        var ru1 = atlas.getU1(right);
        var rv1 = atlas.getV1(right);
        var back = makePath(TEXTURES, BACK);
        var bu0 = atlas.getU0(back);
        var bv0 = atlas.getV0(back);
        var bu1 = atlas.getU1(back);
        var bv1 = atlas.getV1(back);
        Tesselator.getInstance().init()
            /*.vertexUV(x + w, y, z, ru0, rv1)
            .vertexUV(x + w, y, z + d, ru1, rv1)
            .vertexUV(x + w, y + h, z + d, ru1, rv0)
            .vertexUV(x + w, y + h, z, ru0, rv0)*/
            .vertexUV(x+w, y+h, z, ru0, rv0)
            .vertexUV(x+w, y, z, ru0, rv1)
            .vertexUV(x+w, y, z+d, ru1, rv1)
            .vertexUV(x+w, y+h, z+d, ru1, rv0)
            .draw(GL_QUADS);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);

        glPopMatrix();

        if (isLit) glEnable(GL_LIGHTING);

        /*glDepthMask(false);
        glEnable(GL_TEXTURE_CUBE_MAP);
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        glBegin(GL_TRIANGLES);
        for (int i = 0; i < skyboxVertices.length; i += 3) {
            var x = skyboxVertices[i];
            var y = skyboxVertices[i + 1];
            var z = skyboxVertices[i + 2];
            glTexCoord3f(x, y, z);
            glVertex3f(x, y, z);
        }
        glEnd();
        glDisable(GL_TEXTURE_CUBE_MAP);
        glDepthMask(true);*/
    }

    public void free() {
        atlas.free();
    }
}
