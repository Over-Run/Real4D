package org.overrun.real4d.client;

import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getProperty;

/**
 * @author squid233
 * @since 0.1.0
 */
public class JVMArgs {
    public static final String DEBUGGING = "Real4D.isDebugging";

    public static boolean isDebugging() {
        return parseBoolean(getProperty(DEBUGGING, "false"));
    }
}
