package org.overrun.real4d.client.gui.screen;

import org.overrun.glutils.gl.ll.Tesselator;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class PausingScreen extends Screen {
    /**
     * Construct the screen with a parent.
     *
     * @param parent The parent screen.
     */
    public PausingScreen(Screen parent) {
        super(parent);
    }

    @Override
    public void render() {
        var t = Tesselator.getInstance();
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(0, 0, 0, 0.5f);
        t.init(GL_QUADS)
            .vertex(0, 0, 0)
            .vertex(0, height, 0)
            .vertex(width, height, 0)
            .vertex(width, 0, 0)
            .draw();
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_DEPTH_TEST);
    }
}
