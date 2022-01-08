package org.overrun.real4d.asset;

/**
 * @author squid233
 * @since 0.1.0
 */
public enum AssetType {
    SHADERS("shaders"),
    TEXTURES("textures");

    private final String type;

    AssetType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
