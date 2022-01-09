package org.overrun.real4d.util;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard format: {@code namespace:package.Class.method(Param1;Param2)Type;suffix}
 *
 * @author squid233
 * @since 0.1.0
 */
public class VectorPool {
    private static final Map<String, Vector3i> VECTOR3I_MAP =
        new HashMap<>();
    private static final Map<String, Vector3f> VECTOR3F_MAP =
        new HashMap<>();

    public static Vector3i vec3AllocInt(String id) {
        return VECTOR3I_MAP.computeIfAbsent(id, k -> new Vector3i());
    }

    public static Vector3f vec3AllocFloat(String id) {
        return VECTOR3F_MAP.computeIfAbsent(id, k -> new Vector3f());
    }
}
