package com.github.qaPaschalE.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    // Static block to load config.properties
    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    /**
     * Fetches a property value from config.properties.
     * If the property is not found, returns the default value.
     *
     * @param key          The property key (e.g., "cucumber.tags").
     * @param defaultValue The default value to return if the key is not found.
     * @return The property value or the default value.
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Fetches the Cucumber tags dynamically.
     * Prioritizes system properties (command-line arguments) over config.properties.
     *
     * @return The Cucumber tags to use for test execution.
     */
    public static String getTags() {
        // Prioritize system property (command-line argument)
        String tags = System.getProperty("cucumber.tags");
        if (tags != null && !tags.isEmpty()) {
            return tags;
        }
        // Fallback to config.properties or default (@smoke)
        return properties.getProperty("cucumber.tags", "@smoke");
    }

    /**
     * Fetches the base URL for UI tests.
     *
     * @return The base URL for UI tests.
     */
    public static String getUiBaseUrl() {
        return properties.getProperty("ui.base.url", "https://example.com"); // Default fallback URL
    }

    /**
     * Fetches the API base URL.
     *
     * @return The base URL for API tests.
     */
    public static String getApiBaseUrl() {
        return properties.getProperty("api.base.url", "https://api.example.com"); // Default fallback URL
    }

    /**
     * Fetches the database connection URL.
     *
     * @return The database connection URL.
     */
    public static String getDbUrl() {
        return properties.getProperty("db.url", "jdbc:mysql://localhost:3306/mydb"); // Default fallback URL
    }
}