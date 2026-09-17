package com.booker.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Static-holder singleton exposing the resolved {@link EnvironmentConfig}.
 * Loads {@code config.properties} from the test classpath once; any of its
 * keys can be overridden per-run with a matching {@code -D} system property
 * (e.g. {@code -Dbase.uri=https://staging.example.com}).
 */
public final class ConfigProvider {

    private static final EnvironmentConfig INSTANCE = load();

    private ConfigProvider() {
    }

    public static EnvironmentConfig get() {
        return INSTANCE;
    }

    private static EnvironmentConfig load() {
        Properties properties = new Properties();
        try (InputStream in = ConfigProvider.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on the test classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }

        return new EnvironmentConfig(
                resolve(properties, "base.uri"),
                resolve(properties, "auth.username"),
                resolve(properties, "auth.password"),
                Integer.parseInt(resolve(properties, "request.timeout.ms"))
        );
    }

    private static String resolve(Properties properties, String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config property: " + key);
        }
        return value;
    }
}
