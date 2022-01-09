package org.overrun.real4d.asset;

import org.overrun.real4d.util.Identifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public class AssetManager {
    public static String makePath(Identifier id) {
        return "assets." + id.namespace + "/" + id.path;
    }
}
