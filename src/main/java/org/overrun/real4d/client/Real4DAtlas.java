package org.overrun.real4d.client;

import org.overrun.glutils.tex.stitch.Sprite;
import org.overrun.glutils.tex.stitch.SpriteAtlas;
import org.overrun.real4d.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Real4DAtlas extends SpriteAtlas {
    public Real4DAtlas(int width,
                       int height,
                       int id,
                       Map<Identifier, Sprite> sprites) {
        super(width, height, id);
        this.sprites = new HashMap<>(sprites.size());
        sprites.forEach((identifier, sprite) ->
            this.sprites.put(identifier.toString(), sprite));
    }

    public float getU0(Identifier id) {
        return getU0(id.toString());
    }

    public float getV0(Identifier id) {
        return getV0(id.toString());
    }

    public float getU1(Identifier id) {
        return getU1(id.toString());
    }

    public float getV1(Identifier id) {
        return getV1(id.toString());
    }
}
