package org.overrun.real4d.util;

import org.jetbrains.annotations.NotNull;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Identifier implements CharSequence {
    public static final String REAL4D = "real4d";
    public final String namespace;
    public final String path;

    protected Identifier(String[] id) {
        if (id.length < 2) {
            namespace = REAL4D;
            path = id[0];
        } else {
            namespace = id[0];
            path = id[1];
        }
    }

    public Identifier(String id) {
        this(id.split(":"));
    }

    public Identifier(String namespace,
                      String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    public String asset() {
        return "assets." + namespace + "/" + path;
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}
