package com.github.qaPaschalE.util;

import groovy.lang.MissingPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Properties properties;

    static {
        Properties tempProperties = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            tempProperties.load(input);
            logger.info("Successfully loaded config.properties");
        } catch (IOException e) {
            logger.error("Failed to load config.properties", e);
            throw new RuntimeException("Failed to load config.properties", e);
        }
        properties = new Properties(tempProperties); // Immutable copy
    }

    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        logger.debug("Fetched property: {} = {}", key, value);
        return value;
    }

    public static String getTags() {
        String tags = System.getProperty("cucumber.tags");
        if (tags != null && !tags.isEmpty()) {
            return tags;
        }
        return properties.getProperty("cucumber.tags", "@smoke");
    }

    public static String getUiBaseUrl() {
        String baseUrl = properties.getProperty("ui.base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new MissingPropertyException("Missing or empty 'ui.base.url' in config.properties");
        }
        return baseUrl;
    }

    public static String getApiBaseUrl() {
        String baseUrl = properties.getProperty("api.base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new MissingPropertyException("Missing or empty 'api.base.url' in config.properties");
        }
        return baseUrl;
    }

    public static String getDbUrl() {
        return properties.getProperty("db.url", "jdbc:mysql://localhost:3306/mydb");
    }

    public static String getDbUsername() {
        return properties.getProperty("db.username", "root");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password", "password");
    }

    public static String getBrowserType() {
        return properties.getProperty("browser.type", "chrome");
    }

    public static int getImplicitWaitTime() {
        String waitTime = properties.getProperty("webdriver.implicit.wait", "10");
        try {
            return Integer.parseInt(waitTime);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid value for 'webdriver.implicit.wait' in config.properties", e);
        }
    }

    public static int getPageLoadTimeout() {
        String timeout = properties.getProperty("webdriver.page.load.timeout", "30");
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid value for 'webdriver.page.load.timeout' in config.properties", e);
        }
    }

    public static String loadJsonFile(String fileName) {
        String directory = ConfigLoader.getProperty("json.file.directory", "src/test/resources/json/");
        if (!directory.endsWith("/")) {
            directory += "/";
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(directory + fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON file: " + fileName, e);
        }
        return content.toString();
    }

    public static void reloadProperties() {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.clear();
            properties.load(input);
            logger.info("Reloaded config.properties");
        } catch (IOException e) {
            logger.error("Failed to reload config.properties", e);
            throw new RuntimeException("Failed to reload config.properties", e);
        }
    }
}