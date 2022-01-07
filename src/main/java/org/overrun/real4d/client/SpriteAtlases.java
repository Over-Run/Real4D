package org.overrun.real4d.client;

import org.overrun.glutils.game.Texture2D;
import org.overrun.glutils.tex.TexParam;
import org.overrun.glutils.tex.stitch.Block;
import org.overrun.glutils.tex.stitch.Sprite;
import org.overrun.glutils.tex.stitch.SpriteAtlas;
import org.overrun.glutils.tex.stitch.Stitcher;
import org.overrun.real4d.asset.AssetManager;
import org.overrun.real4d.asset.AssetType;
import org.overrun.real4d.client.gui.Widgets;
import org.overrun.real4d.client.model.BlockModels;
import org.overrun.real4d.util.Identifier;
import org.overrun.real4d.util.Registry;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import static org.overrun.real4d.client.gui.Widgets.HOT_BAR;
import static org.overrun.real4d.client.gui.Widgets.HOT_BAR_SELECT;

/**
 * @author squid233
 * @since 0.1.0
 */
public class SpriteAtlases {
    public static SpriteAtlas BLOCK_ATLAS;
    public static Real4DAtlas WIDGETS_ATLAS;

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
        var widgets = new Texture2D(SpriteAtlases.class,
            AssetManager.makePath(AssetType.TEXTURES,
                Widgets.WIDGETS_TEXTURE),
            TexParam.glNearest());
        WIDGETS_ATLAS = new Real4DAtlas(widgets.width(),
            widgets.height(),
            widgets.getId(),
            Map.ofEntries(
                new SimpleImmutableEntry<>(HOT_BAR, new Sprite(HOT_BAR.toString(),
                    Block.of(0, 0, 202, 22),
                    null)),
                new SimpleImmutableEntry<>(HOT_BAR_SELECT, new Sprite(HOT_BAR_SELECT.toString(),
                    Block.of(0, 22, 24, 24),
                    null))
            ));
    }

    public static void free() {
        BLOCK_ATLAS.free();
        WIDGETS_ATLAS.free();
    }
}
