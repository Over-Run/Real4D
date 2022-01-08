package org.overrun.real4d.client;

import org.overrun.glutils.tex.stitch.Sprite;
import org.overrun.glutils.tex.stitch.SpriteAtlas;
import org.overrun.real4d.util.Identifier;

import java.util.HashMap;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Real4DAtlas extends SpriteAtlas<Identifier> {
    public Real4DAtlas(int width,
                       int height,
                       int id,
                       Sprite... spriteArr) {
        super(width, height, id);
        sprites = new HashMap<>(spriteArr.length);
        for (var sprite : spriteArr) {
            sprites.put((Identifier) sprite.id, sprite);
        }
    }
}
