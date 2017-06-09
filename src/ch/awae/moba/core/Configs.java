package ch.awae.moba.core;

import java.io.IOException;
import java.util.Properties;

import ch.awae.moba.core.util.Utils;

public final class Configs {

    private Configs() {
        throw new AssertionError("unreachable");
    }

    // ==== CONFIGS CLASS ====

    private static final Properties configs;

    static {
        try {
            configs = Utils.getProperties("configurations.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a Properties file indirectly by loading the file indicated at the
     * passed key in the root configuration file.
     * 
     * @param key
     * @return
     * @throws IOException
     *             if an I/O exception occurs while reading the properties
     */
    public static Properties getProperties(String key) throws IOException {
        return Utils.getProperties(configs.getProperty(key));
    }

}
