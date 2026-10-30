package com.dyad.hrms.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Properties;

/**
 * Resolves settings in this order: -D system property, environment variable, config.properties.
 * The environment variable name is the key in upper case with dots replaced by underscores,
 * for example HEADLESS for headless.
 */
public final class Config {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties FILE_PROPERTIES = loadFile();

    private Config() {
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String browser() {
        return get("browser");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout.seconds")));
    }

    public static String adminUsername() {
        return get("admin.username");
    }

    public static String adminPassword() {
        return get("admin.password");
    }

    private static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        if (value == null) {
            value = FILE_PROPERTIES.getProperty(key);
        }
        if (value == null) {
            throw new IllegalStateException("Missing configuration value: " + key);
        }
        return value;
    }

    private static Properties loadFile() {
        try (InputStream stream = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new IllegalStateException(CONFIG_FILE + " not found on the classpath");
            }
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + CONFIG_FILE, e);
        }
    }
}
