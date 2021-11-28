package org.overrun.real4d.client.renderer;

import org.joml.Matrix4fStack;
import org.overrun.glutils.GLProgram;
import org.overrun.glutils.mesh.Mesh3;
import org.overrun.real4d.client.Real4D;
import org.overrun.real4d.universe.planet.Planet;

import static org.overrun.glutils.ShaderReader.lines;
import static org.overrun.glutils.math.Transform.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public class PlanetRenderer {
    public static final ClassLoader cl = PlanetRenderer.class.getClassLoader();
    private GLProgram program;
    private Mesh3 mesh;
    public Planet planet;

    public PlanetRenderer(Planet planet) {
        this.planet = planet;
    }

    public void init() throws Exception {
        program = new GLProgram();
        program.createVsh(lines(cl, "assets.real4d/shaders/core/block.vsh"));
        program.createFsh(lines(cl, "assets.real4d/shaders/core/block.fsh"));
        program.link();
        mesh = new Mesh3()
            .vertIdx(0)
            .vertices(new float[]{
                0, 0.5f, 0,
                -0.5f, -0.5f, 0,
                0.5f, -0.5f, 0
            })
            .colorIdx(1)
            .colorDim(4)
            .colors(new float[]{
                1, 0, 0, 1,
                0, 1, 0, 1,
                0, 0, 1, 1
            })
            .vertexCount(3)
            .unbindVao();
    }

    public void render(float delta,
                       Matrix4fStack proj,
                       Matrix4fStack modelView) {
        var fb = Real4D.INSTANCE.framebuffer;
        var player = planet.player;
        program.bind();
        program.setUniformMat4("proj",
            setPerspective(proj,
                90,
                fb.getWidth(),
                fb.getHeight(),
                0.05f,
                1000));
        rotateY(
            rotateX(
                modelView.setTranslation(0, 0, -0.3f),
                player.xRot
            ),
            player.yRot
        );
        var x = player.prevX + (player.x - player.prevX) * delta;
        var y = player.prevY + (player.y + player.eyeHeight - player.prevY) * delta;
        var z = player.prevZ + (player.z - player.prevZ) * delta;
        program.setUniformMat4("modelView",
            modelView.translate(x, y, z));
        mesh.render();
        program.unbind();
    }

    public void free() {
        mesh.close();
    }
}
