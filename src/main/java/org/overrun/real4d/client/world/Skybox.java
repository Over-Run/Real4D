package org.overrun.real4d.client.world;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.overrun.glutils.gl.GLProgram;
import org.overrun.glutils.gl.Vao;
import org.overrun.glutils.gl.Vbo;
import org.overrun.glutils.gl.VertexAttrib;
import org.overrun.glutils.tex.Images;
import org.overrun.glutils.tex.Textures;
import org.overrun.real4d.asset.AssetManager;
import org.overrun.real4d.asset.AssetType;
import org.overrun.real4d.client.gl.GLMatrix;
import org.overrun.real4d.util.Identifier;

import java.io.BufferedInputStream;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Skybox {
    private static final float[] skyboxVertices = {
        // positions
        -1.0f, 1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,

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
    private static final Matrix4f matrix = new Matrix4f();
    public final int id = Textures.gen();
    public final Vao vao = new Vao();
    public final Vbo vbo = new Vbo(GL_ARRAY_BUFFER);
    public final VertexAttrib vertexAttrib = new VertexAttrib(0);
    public final GLProgram program = new GLProgram();

    public Skybox() {
        program.createVsh("""
            #version 330
            layout (location = 0) in vec3 aPos;
            out vec3 TexCoords;
            uniform mat4 proj, view;
            void main() {
                TexCoords = aPos;
                vec4 pos = proj * view * vec4(aPos, 1.0);
                gl_Position = pos.xyww;
            }""");
        program.createFsh("""
            #version 330
            out vec4 FragColor;
            in vec3 TexCoords;
            uniform samplerCube skybox;
            void main() {
                FragColor = texture(skybox, TexCoords);
            }""");
        program.link();
        vao.bind();
        vbo.bind();
        vbo.data(skyboxVertices, GL_STATIC_DRAW);
        vertexAttrib.enable();
        vertexAttrib.pointer(3,
            GL_FLOAT,
            false,
            0,
            0);
        vbo.unbind();
        vao.unbind();
        program.bind();
        program.setUniform("skybox", 0);
        program.unbind();
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        var faces = new Identifier[]{
            RIGHT,
            LEFT,
            TOP,
            BOTTOM,
            FRONT,
            BACK
        };
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
                    var arr = bis.readAllBytes();
                    var bb = memAlloc(arr.length).put(arr).flip();
                    var data = stbi_load_from_memory(bb,
                        pw,
                        ph,
                        pc,
                        STBI_rgb);
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
                    memFree(bb);
                }
                ++i;
            }
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
            Textures.genMipmapCubeMap();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void render() {
        program.bind();
        program.setUniformMat4("proj", GLMatrix.getProjection());
        var view = GLMatrix.getModelview();
        program.setUniformMat4("view", matrix.set(
            view.m00(), view.m01(), view.m02(), 0,
            view.m10(), view.m11(), view.m12(), 0,
            view.m20(), view.m21(), view.m22(), 0,
            0, 0, 0, 0
        ));
        vao.bind();
        Textures.active(0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        vao.unbind();
        program.unbind();
    }

    public void free() {
        vao.free();
        vbo.free();
        program.free();
        glDeleteTextures(id);
    }
}
