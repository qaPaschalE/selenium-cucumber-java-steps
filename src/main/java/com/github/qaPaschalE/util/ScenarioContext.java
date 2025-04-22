package com.github.qaPaschalE.util;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScenarioContext {
    private static final Logger logger = LoggerFactory.getLogger(ScenarioContext.class);
    private final Map<String, Object> context = new HashMap<>();

    /**
     * Store any type of data in the context.
     *
     * @param key   The key to store the value under. Must not be null or empty.
     * @param value The value to store. Can be null.
     * @throws IllegalArgumentException if the key is null or empty.
     */
    public void setContext(String key, Object value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        context.put(key, value);
        logger.debug("Stored context value for key '{}': {}", key, value);
    }

    /**
     * Retrieve any type of data from the context.
     *
     * @param key The key to retrieve the value for. Must not be null or empty.
     * @param <T> The type of the value being retrieved.
     * @return The value associated with the key, cast to the expected type.
     * @throws IllegalArgumentException if the key is null or empty.
     * @throws NullPointerException     if the key does not exist in the context.
     */
    @SuppressWarnings("unchecked")
    public <T> T getContext(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        if (!context.containsKey(key)) {
            throw new NullPointerException("No value found in context for key: " + key);
        }
        T value = (T) context.get(key);
        logger.debug("Retrieved context value for key '{}': {}", key, value);
        return value;
    }

    /**
     * Check if a key exists in the context.
     *
     * @param key The key to check. Must not be null or empty.
     * @return True if the key exists, false otherwise.
     * @throws IllegalArgumentException if the key is null or empty.
     */
    public boolean contains(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty.");
        }
        boolean exists = context.containsKey(key);
        logger.debug("Checked existence of key '{}' in context: {}", key, exists);
        return exists;
    }

    /**
     * Clear the context (optional, for cleanup).
     */
    public void clearContext() {
        context.clear();
        logger.info("Cleared all context data.");
    }

    /**
     * Store the API response in the context.
     *
     * @param response The API response to store. Must not be null.
     * @throws IllegalArgumentException if the response is null.
     */
    public void setResponse(Response response) {
        if (response == null) {
            throw new IllegalArgumentException("API response must not be null.");
        }
        context.put("lastApiResponse", response);
        logger.debug("Stored API response in context.");
    }

    /**
     * Retrieve the last stored API response.
     *
     * @return The last stored API response.
     * @throws NullPointerException if no API response is stored in the context.
     */
    public Response getResponse() {
        Response response = getContext("lastApiResponse");
        if (response == null) {
            throw new NullPointerException("No API response found in context.");
        }
        return response;
    }
}