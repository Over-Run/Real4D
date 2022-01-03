package org.overrun.real4d.client;

import org.overrun.glutils.tex.TexParam;
import org.overrun.glutils.tex.stitch.SpriteAtlas;
import org.overrun.glutils.tex.stitch.Stitcher;
import org.overrun.real4d.client.model.BlockModels;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.util.Registry;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author squid233
 * @since 0.1.0
 */
public class SpriteAtlases {
    public static SpriteAtlas BLOCK_ATLAS;

    public static void load() {
        var set = new HashSet<Identifier>();
        var list = new ArrayList<String>();
        for (var e : Registry.BLOCK) {
            var block = e.getValue();
            set.add(block.getTexTop());
            set.add(block.getTexSid());
            set.add(block.getTexSidOverlay());
            set.add(block.getTexBtm());
        }
        for (var id : set) {
            if (id == null) {
                continue;
            }
            list.add(BlockModels.toTexFilePath(id));
        }
        BLOCK_ATLAS = Stitcher.stitchStb(SpriteAtlases.class,
            TexParam.glNearest(),
            list.toArray(new String[0]));
    }

    public static void free() {
        BLOCK_ATLAS.free();
    }
}
