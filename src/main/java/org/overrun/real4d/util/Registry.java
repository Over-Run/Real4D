package org.overrun.real4d.util;

import org.jetbrains.annotations.NotNull;
import org.overrun.real4d.world.block.Block;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author squid233
 * @since 0.1.0
 */
public class Registry<T> implements Iterable<Map.Entry<Identifier, T>> {
    public static final Registry<Block> BLOCK = new Registry<>();
    public final Map<Identifier, T> id2entry = new HashMap<>();
    public final Map<Integer, T> rawId2entry = new HashMap<>();
    public final Map<T, Identifier> entry2Id = new HashMap<>();
    public final Map<T, Integer> entry2rawId = new HashMap<>();
    public T defaultEntry;
    private int nextId;

    public static <T, R extends T> R register(Registry<T> registry,
                                              Identifier id,
                                              R entry) {
        return registry.add(id, entry);
    }

    public static <T, R extends T> R register(Registry<T> registry,
                                              String id,
                                              R entry) {
        return register(registry, new Identifier(id), entry);
    }

    public Registry(T defaultEntry) {
        this.defaultEntry = defaultEntry;
    }

    public Registry() {
        this(null);
    }

    public <R extends T> R add(Identifier id,
                               R entry) {
        var r = set(id, entry, nextId);
        ++nextId;
        return r;
    }

    public <R extends T> R set(Identifier id,
                               R entry,
                               int rawId) {
        id2entry.put(id, entry);
        rawId2entry.put(rawId, entry);
        entry2Id.put(entry, id);
        entry2rawId.put(entry, rawId);
        nextId = rawId;
        return entry;
    }

    public T get(int rawId) {
        return rawId2entry.getOrDefault(rawId, defaultEntry);
    }

    public Identifier getId(T entry) {
        return entry2Id.getOrDefault(entry, entry2Id.getOrDefault(defaultEntry, null));
    }

    public int getRawId(T entry) {
        return entry2rawId.getOrDefault(entry, entry2rawId.getOrDefault(defaultEntry, -1));
    }

    public int size() {
        return id2entry.size();
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<Identifier, T>> iterator() {
        return id2entry.entrySet().iterator();
    }
}
