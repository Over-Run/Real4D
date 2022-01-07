package org.overrun.real4d.client.model;

import org.overrun.real4d.asset.AssetManager;
import org.overrun.real4d.asset.AssetType;
import org.overrun.real4d.util.Identifier;

/**
 * @author squid233
 * @since 0.1.0
 */
public class BlockModels {
    public static String toTexFilePath(Identifier id) {
        return AssetManager.makePath(AssetType.TEXTURES,
            new Identifier(id.namespace, "block/" + id.path + ".png"));
    }
}
