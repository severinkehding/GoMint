/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util;

import io.gomint.plugin.Plugin;

/**
 * @author geNAZt
 * @version 1.0
 */
public class CallerDetectorUtil {

    private static CallerDetector callerDetector;

    static {
        callerDetector = new SecurityManagerCallerDetector();
    }

    /**
     * Get the plugin which called
     *
     * @return class of plugin
     */
    public static Class<? extends Plugin> getCallerPlugin() {
        return callerDetector.getCallerPlugin();
    }

}
