package org.overrun.real4d.client.model;

import org.overrun.real4d.util.Identifier;

import static org.overrun.real4d.asset.AssetManager.makePath;

/**
 * @author squid233
 * @since 0.1.0
 */
public class BlockModels {
    public static void load() {
    }

    public static String toTexFilePath(Identifier id) {
        return makePath(
            new Identifier(
                id.namespace,
                "textures/" + id.path + ".png"
            )
        );
    }

    public static String toTexFilePath(String id) {
        return toTexFilePath(new Identifier(id));
    }
}
