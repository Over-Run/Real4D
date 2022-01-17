package org.overrun.real4d.client.gl;

import org.overrun.glutils.SizedObject;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Framebuffer implements SizedObject {
    public int width;
    public int height;
    public int fps;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
