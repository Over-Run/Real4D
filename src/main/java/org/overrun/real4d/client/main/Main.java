package org.overrun.real4d.client.main;

import org.overrun.glutils.game.GameApp;
import org.overrun.glutils.game.GameConfig;
import org.overrun.real4d.client.Real4D;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Main {
    public static void main(String[] args) {
        GameConfig config = new GameConfig();
        config.width = 854;
        config.height = 480;
        config.title = "Real4D " + Real4D.VERSION;
        new GameApp(Real4D.INSTANCE, config);
    }
}
