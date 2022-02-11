package com.silibrina.tecnova.commons.conf;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigLoader {

    private static ClassLoader classLoader;

    public static void setUp() {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    public static Config load() {
        if (classLoader != null) {
            return ConfigFactory.load(classLoader);
        }

        return ConfigFactory.load();
    }
}
