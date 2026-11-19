package com.qa.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Properties;


public final class TestConfig {

    private static final Properties DEFAULTS = new Properties();

    static {
        try (InputStream in = TestConfig.class.getResourceAsStream("/config.properties")) {
            DEFAULTS.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private TestConfig() {
    }

    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key.toUpperCase(Locale.ROOT).replace('.', '_'));
        }
        if (value == null || value.isBlank()) {
            value = DEFAULTS.getProperty(key, "");
        }
        return value.trim();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("ui.headless"));
    }

    public static long uiTimeoutMs() {
        return Long.parseLong(get("ui.timeout.ms"));
    }
}
