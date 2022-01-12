package org.overrun.real4d.client.model;

/**
 * @author squid233
 * @since 0.1.0
 */
public class HumanModel {
    public Cube head = new Cube(0, 0);
    public Cube body = new Cube(16, 16);
    public Cube armR = new Cube(40, 16);
    public Cube armL = new Cube(32, 48);
    public Cube legR = new Cube(0, 16);
    public Cube legL = new Cube(16, 48);

    public HumanModel() {
        head.addBox(-4, 24, -4, 8, 8, 8);
        body.addBox(-4, 12, -2, 8, 12, 4);
        //body.addBox(-4, 0, -2, 8, 12, 4);
        armR.addBox(-8, 12, -2, 4, 12, 4);
        armL.addBox(4, 12, -2, 4, 12, 4);
        legR.addBox(-4, 0, -2, 4, 12, 4);
        legL.addBox(0, 0, -2, 4, 12, 4);
    }

    public void render(float time) {
        /*head.yRot = (float) sin(time * 0.83);
        head.xRot = (float) sin(time) * 0.8f;
        armR.xRot = (float) sin(time * 0.6662 + PI) * 2;
        armR.zRot = (float) (sin(time * 0.2312) + 1);
        armL.xRot = (float) sin(time * 0.6662) * 2;
        armL.zRot = (float) (sin(time * 0.2812) - 1);
        legR.xRot = (float) sin(time * 0.6662) * 1.4f;
        legL.xRot = (float) sin(time * 0.6662 + PI) * 1.4f;*/
        head.render();
        body.render();
        armR.render();
        armL.render();
        legR.render();
        legL.render();
    }
}
