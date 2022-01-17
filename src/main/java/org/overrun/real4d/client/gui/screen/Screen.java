package org.overrun.real4d.client.gui.screen;

import org.overrun.real4d.client.Real4D;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Screen {
    protected Screen parent;
    protected Real4D client;
    protected int width;
    protected int height;

    public Screen(Screen parent) {
        this.parent = parent;
    }

    protected void init() {
    }

    public void init(final Real4D client,
                     final int width,
                     final int height) {
        this.client = client;
        this.width = width;
        this.height = height;
        init();
    }

    public void render() {
    }

    public void keyPressed(int key,
                           int scancode,
                           int mods) {
    }

    public void cursorPosCb(int x,
                            int z) {
    }

    public void mouseWheel(double xo,
                           double yo) {
    }

    public void close() {
    }

    public Screen getParent() {
        return parent;
    }
}
