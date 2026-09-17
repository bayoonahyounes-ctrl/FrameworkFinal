package config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public final class Config {

    private static final Properties PROPERTIES = load();

    private Config() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on the classpath");
            }
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("could not read config.properties", e);
        }
    }

    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("missing config key: " + key);
        }
        return value;
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static boolean screenshotOnSuccess() {
        return getBoolean("screenshot.on.success");
    }

    public static boolean headless() {
        return getBoolean("headless");
    }

    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout")));
    }

    public static double taxRate() {
        return Double.parseDouble(get("tax.rate"));
    }
}
