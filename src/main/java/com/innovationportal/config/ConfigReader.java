package com.innovationportal.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Reads {@code config/config.properties} from the test classpath
 * (src/test/resources/config/config.properties). Every key can be overridden
 * by a system property with the same name, e.g. {@code -Dbrowser=firefox}.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config/config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    public static String browser() {
        return required("browser");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(required("headless"));
    }

    public static String env() {
        return required("env");
    }

    /** {@code baseUrl} if set (property or -DbaseUrl), otherwise {@code <env>.baseUrl}. */
    public static String baseUrl() {
        String baseUrl = get("baseUrl");
        if (!baseUrl.isEmpty()) {
            return baseUrl;
        }
        String envKey = env() + ".baseUrl";
        String envBaseUrl = get(envKey);
        if (envBaseUrl.isEmpty()) {
            throw new IllegalStateException(
                    "No base URL configured: set 'baseUrl' or '" + envKey + "' in " + CONFIG_FILE);
        }
        return envBaseUrl;
    }

    public static int explicitWaitSeconds() {
        return Integer.parseInt(required("explicitWaitSeconds"));
    }

    public static int pageLoadTimeoutSeconds() {
        return Integer.parseInt(required("pageLoadTimeoutSeconds"));
    }

    /** Window size as {@code <width>,<height>}, e.g. {@code 1920,1080}. */
    public static String windowSize() {
        return required("windowSize");
    }

    /** System property first, then config.properties; empty string when neither is set. */
    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = PROPERTIES.getProperty(key, "");
        }
        return value.trim();
    }

    private static String required(String key) {
        String value = get(key);
        if (value.isEmpty()) {
            throw new IllegalStateException("Missing configuration key '" + key + "' in " + CONFIG_FILE);
        }
        return value;
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException(CONFIG_FILE + " not found on the classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + CONFIG_FILE, e);
        }
        return properties;
    }
}
