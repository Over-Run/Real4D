package org.overrun.real4d.client.gui.render;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.overrun.glutils.tex.Images;
import org.overrun.glutils.tex.TexParam;
import org.overrun.glutils.tex.Textures;
import org.overrun.real4d.util.Identifier;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.toHexString;
import static java.util.Objects.requireNonNull;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.overrun.glutils.gl.ll.GLU.gluBuild2DMipmaps;
import static org.overrun.real4d.asset.AssetManager.makePath;

/**
 * @author squid233
 * @since 0.1.0
 */
public class TextRenderer {
    private static final Map<Character, Glyph> GLYPHS =
        new LinkedHashMap<>();
    private static final int[] ids = new int[1];
    private static int lists;

    public static class Glyph {
        public int x0, y0, x1, y1;

        public Glyph(int x0,
                     int y0,
                     int x1,
                     int y1) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Glyph.class.getSimpleName() + "[", "]")
                .add("x0=" + x0)
                .add("y0=" + y0)
                .add("x1=" + x1)
                .add("y1=" + y1)
                .toString();
        }
    }

    public static void loadFont() {
        int id = Textures.gen();
        Textures.bind2D(id);
        ids[0] = id;
        var cl = TextRenderer.class.getClassLoader();
        try (var stack = MemoryStack.stackPush()) {
            var pw = stack.mallocInt(1);
            var ph = stack.mallocInt(1);
            var pc = stack.mallocInt(1);
            var path = makePath(new Identifier("textures/font/unipage_00.png"));
            ByteBuffer buffer;
            try (var is = requireNonNull(cl.getResourceAsStream(path));
                 var bis = new BufferedInputStream(is)) {
                var bytes = bis.readAllBytes();
                buffer = memAlloc(bytes.length).put(bytes).flip();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            var data = stbi_load_from_memory(buffer,
                pw,
                ph,
                pc,
                STBI_default);
            if (data == null) {
                Images.thrRE(path);
            }
            int w = pw.get(0);
            int h = ph.get(0);
            var channel = pc.get(0);
            if (channel == STBI_rgb) {
                channel = GL_RGB;
            } else {
                channel = GL_RGBA;
            }
            Textures.texParameter2D(TexParam.glNearest());
            if (Textures.hasGenMipmap()) {
                glTexImage2D(GL_TEXTURE_2D,
                    0,
                    channel,
                    w,
                    h,
                    0,
                    channel,
                    GL_UNSIGNED_BYTE,
                    data
                );
                Textures.genMipmap2D();
            } else {
                gluBuild2DMipmaps(GL_TEXTURE_2D,
                    channel,
                    w,
                    h,
                    channel,
                    GL_UNSIGNED_BYTE,
                    data
                );
            }
            memFree(buffer);
            stbi_image_free(data);
        }
        for (int i = 0; i < 0xff; i++) {
            var prop = new Properties(0x100);
            var path = makePath(new Identifier("textures/font/glyph00.properties"));
            try (var is = requireNonNull(
                cl.getResourceAsStream(path)
            ); var bis = new BufferedInputStream(is)) {
                prop.load(bis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            var key = toHexString(i);
            var value = prop.getProperty(key).split(",", 4);
            var glyph = new Glyph(
                parseInt(value[0]),
                parseInt(value[1]),
                parseInt(value[2]),
                parseInt(value[3])
            );
            GLYPHS.put((char) i, glyph);
        }
    }

    public static void buildLists() {
        lists = glGenLists(GLYPHS.size());
        int i = 0;
        for (var glyph : GLYPHS.values()) {
            var u0 = glyph.x0 / 256f;
            var u1 = glyph.x1 / 256f;
            var v0 = glyph.y0 / 256f;
            var v1 = glyph.y1 / 256f;
            var x1 = glyph.x1 - glyph.x0;
            var y1 = glyph.y1 - glyph.y0;
            glNewList(lists + i, GL_COMPILE);
            glBegin(GL_QUADS);
            glTexCoord2f(u0, v0);
            glVertex2f(0, 0);
            glTexCoord2f(u0, v1);
            glVertex2f(0, y1);
            glTexCoord2f(u1, v1);
            glVertex2f(x1, y1);
            glTexCoord2f(u1, v0);
            glVertex2f(x1, 0);
            glEnd();
            glEndList();
            ++i;
        }
    }

    public static void drawText(String text,
                                int x,
                                int y,
                                int z,
                                @Nullable Consumer<Glyph> delegate) {
        int xo = x;
        try (var sc = new Scanner(text)) {
            while (sc.hasNextLine()) {
                var s = sc.nextLine();
                var ca = s.toCharArray();
                int h = getTextMaxHeight(s);
                for (var c : ca) {
                    int w = getGlyphWidth(c);
                    if (delegate != null)
                        delegate.accept(new Glyph(x, y, x + w, y + h));
                    drawChar(c, x, y, z);
                    x += w;
                }
                x = xo;
                y += h;
            }
        }
    }

    public static void drawText(String text,
                                int x,
                                int y,
                                int z) {
        drawText(text, x, y, z, null);
    }

    public static void drawChar(char c,
                                int x,
                                int y,
                                int z) {
        var b = glGetBoolean(GL_TEXTURE_2D);
        if (!b) glEnable(GL_TEXTURE_2D);
        Textures.bind2D(ids[c / 256]);
        glPushMatrix();
        glTranslatef(x, y, z);
        glCallList(lists + c);
        glPopMatrix();
        if (!b) glDisable(GL_TEXTURE_2D);
    }

    public static int getGlyphWidth(char c) {
        var g = GLYPHS.get(c);
        return g.x1 - g.x0;
    }

    public static int getGlyphHeight(char c) {
        var g = GLYPHS.get(c);
        return g.y1 - g.y0;
    }

    public static int getTextWidth(String text) {
        var ca = text.toCharArray();
        int w = 0;
        for (var c : ca) {
            w += getGlyphWidth(c);
        }
        return w;
    }

    public static int getTextMaxHeight(String text) {
        var ca = text.toCharArray();
        int h = 0;
        for (var c : ca) {
            var gh = getGlyphHeight(c);
            if (gh > h) h = gh;
        }
        return h;
    }
}
