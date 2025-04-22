package com.github.qaPaschalE.stepDefinitions.api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.github.qaPaschalE.util.ConfigLoader;
import com.github.qaPaschalE.util.ScenarioContext;
import org.testng.Assert;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ApiStepDefinitions {

    private Response response;
    private RequestSpecification requestSpec;
    private final Map<String, Object> queryParams = new HashMap<>();
    private final Map<String, Object> pathParams = new HashMap<>();
    private final ScenarioContext scenarioContext;

    private static final String YELLOW_COLOR = "\033[0;33m";
    private static final String GREEN_COLOR = "\033[0;32m";
    private static final String BLUE_COLOR = "\033[0;34m";
    private static final String RESET_COLOR = "\033[0m";

    public ApiStepDefinitions(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;

        String baseUrl = ConfigLoader.getApiBaseUrl();
        if (baseUrl == null || baseUrl.isEmpty()) {
            System.err.println(YELLOW_COLOR + "Base URL is not set in config.properties" + RESET_COLOR);
            throw new IllegalStateException("Base URL is not set in config.properties");
        }
        RestAssured.baseURI = baseUrl;

        // Initialize request specification
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    // region REQUEST SETUP

    /**
     * Sets the base URL for the API.
     *
     * @param url The base URL to set.
     */
    @Given("I set base URL to {string}")
    public void setBaseUrl(String url) {
        if (url.startsWith("/")) {
            // Fetch the base URL from config.properties
            String baseUrl = ConfigLoader.getApiBaseUrl();
            url = baseUrl + url; // Combine base URL and relative path
        }
        RestAssured.baseURI = url;
        System.out.println(YELLOW_COLOR + "Base URL set to: " + url + RESET_COLOR);
    }

    /**
     * Adds a header to the request specification.
     *
     * @param header The header name.
     * @param value  The header value.
     */
    @Given("I set header {string} to {string}")
    public void setHeader(String header, String value) {
        requestSpec = requestSpec.header(header, value);
    }

    /**
     * Adds a query parameter to the request.
     *
     * @param param The query parameter name.
     * @param value The query parameter value.
     */
    @Given("I set API query parameter {string} to {string}")
    public void setQueryParam(String param, String value) {
        System.out.println(YELLOW_COLOR + "Setting query parameter: " + param + " = " + value + RESET_COLOR);
        queryParams.put(param, value);
    }
    /**
     * Stores a specific field from the API response into the scenario context.
     *
     * @param jsonPath The JSON path of the field to extract from the response.
     * @param key      The key to store the value under in the scenario context.
     */
    @When("I store the response field {string} as {string}")
    public void storeResponseField(String jsonPath, String key) {
        // Log the entire response body
        System.out.println(YELLOW_COLOR + "Full Response Body in storeResponseField: " + scenarioContext.getResponse().getBody().asString() + RESET_COLOR);

        // Extract the value from the response using the JSON path
        String value = scenarioContext.getResponse().jsonPath().getString(jsonPath);

        // Validate that the value is not null or empty
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("No value found in response for JSON path: " + jsonPath);
        }

        // Store the value in the scenario context
        scenarioContext.setContext(key, value);

        // Log the stored value for debugging
        System.out.println(YELLOW_COLOR + "Stored response field '" + jsonPath + "' as '" + key + "': " + value + RESET_COLOR);
    }
    /**
     * Adds a path parameter to the request.
     *
     * @param param The path parameter name.
     * @param value The path parameter value.
     */
    @Given("I set API path parameter {string} to {string}")
    public void setPathParam(String param, String value) {
        pathParams.put(param, value);
    }

    /**
     * Adds a cookie to the request specification.
     *
     * @param name  The cookie name.
     * @param value The cookie value.
     */
    @Given("I set cookie {string} to {string}")
    public void setCookie(String name, String value) {
        requestSpec = requestSpec.cookie(name, value);
    }

    /**
     * Sets a Bearer token for authentication.
     *
     * @param token The Bearer token.
     */
    @Given("I set bearer token {string}")
    public void setBearerToken(String token) {
        System.out.println(YELLOW_COLOR + "Setting Bearer token" + RESET_COLOR);
        requestSpec = requestSpec.auth().oauth2(token);
    }

    // endregion

    // region REQUEST METHODS

    /**
     * Sends a GET request to the specified endpoint.
     *
     * @param endpoint The API endpoint.
     */
    @When("I make a GET request to {string}")
    public void makeGetRequest(String endpoint) {
        System.out.println(YELLOW_COLOR + "Making GET request to endpoint: " + endpoint + RESET_COLOR);
        // Resolve placeholders in the endpoint
        String resolvedEndpoint = replacePlaceholders(endpoint);
        System.out.println(YELLOW_COLOR + "Resolved endpoint: " + resolvedEndpoint + RESET_COLOR);
        response = RestAssured.given()
                .spec(requestSpec)
                .queryParams(queryParams)
                .pathParams(pathParams)
                .get(resolvedEndpoint);
        scenarioContext.setResponse(response);
        System.out.println(YELLOW_COLOR + "Response Body after storing in ScenarioContext: " + scenarioContext.getResponse().getBody().asString() + RESET_COLOR); // Add this line
        resetRequest();
    }

    /**
     * Sends a POST request to the specified endpoint with a dynamic request body.
     *
     * @param endpoint The API endpoint.
     * @param source   The source of the request body:
     * - "config": Load from config.properties.
     * - "file": Load from a .json file.
     * - "inline": Use the provided body directly.
     * @param requestBody    The key (for config/file) or the inline body content.
     */
    @When("I make a POST request to {string} with body from {string}:")
    public void makePostRequest(String endpoint, String source, String requestBody) {
        System.out.println(YELLOW_COLOR + "Making POST request to endpoint: " + endpoint + RESET_COLOR);
        // Resolve placeholders in the endpoint
        String resolvedEndpoint = replacePlaceholders(endpoint);
        System.out.println(YELLOW_COLOR + "Resolved endpoint: " + resolvedEndpoint + RESET_COLOR);

        // Resolve placeholders in the request body
        String resolvedBody = resolveRequestBody(source, requestBody);
        System.out.println(YELLOW_COLOR + "Resolved request body: " + resolvedBody + RESET_COLOR);

        // Send the POST request
        response = RestAssured.given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(resolvedBody)
                .post(resolvedEndpoint);

        // Log the response body for debugging
        System.out.println(YELLOW_COLOR + "Response body: " + response.getBody().asString() + RESET_COLOR);

        // Store the response in the scenario context
        scenarioContext.setResponse(response);

        // Reset the request state
        resetRequest();
    }
    @When("I make a POST request to {string} with body:")
    public void makePostRequestWithBody(String endpoint, String requestBody) {
        System.out.println(YELLOW_COLOR + "Making POST request to endpoint: " + endpoint + RESET_COLOR);
        // Resolve placeholders in the endpoint
        String resolvedEndpoint = replacePlaceholders(endpoint);
        System.out.println(YELLOW_COLOR + "Resolved endpoint: " + resolvedEndpoint + RESET_COLOR);

        // Resolve placeholders in the request body (source is implicitly "Inline" here)
        String resolvedBody = resolveRequestBody("Inline", requestBody);
        System.out.println(YELLOW_COLOR + "Resolved request body: " + resolvedBody + RESET_COLOR);

        // Send the POST request
        response = RestAssured.given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(resolvedBody)
                .post(resolvedEndpoint);

        // Log the response body for debugging
        System.out.println(YELLOW_COLOR + "Response body: " + response.getBody().asString() + RESET_COLOR);

        // Store the response in the scenario context
        scenarioContext.setResponse(response);

        // Reset the request state
        resetRequest();
    }
    /**
     * Sends a PUT request to the specified endpoint with a dynamic request body.
     *
     * @param endpoint The API endpoint.
     * @param source   The source of the request body:
     * - "config": Load from config.properties.
     * - "file": Load from a .json file.
     * - "inline": Use the provided body directly.
     * @param value    The key (for config/file) or the inline body content.
     */
    @When("I make a PUT request to {string} with body from {string}: {string}")
    public void makePutRequest(String endpoint, String source, String value) {
        System.out.println(YELLOW_COLOR + "Making PUT request to endpoint: " + endpoint + RESET_COLOR);
        // Resolve placeholders in the endpoint
        String resolvedEndpoint = replacePlaceholders(endpoint);
        System.out.println(YELLOW_COLOR + "Resolved endpoint: " + resolvedEndpoint + RESET_COLOR);
        String requestBody = resolveRequestBody(source, value);

        response = RestAssured.given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .put(resolvedEndpoint);
        scenarioContext.setResponse(response);
        resetRequest();
    }
    @When("I make a POST request to {string} with the stored token as {string} and body from {string}:")
    public void makePostRequestWithTokenAndBody(String endpoint, String tokenKey, String source, String requestBody) {
        System.out.println(YELLOW_COLOR + "Making POST request to endpoint: " + endpoint + " with stored token as " + tokenKey + RESET_COLOR);
        // Resolve placeholders in the endpoint
        String resolvedEndpoint = replacePlaceholders(endpoint);
        System.out.println(YELLOW_COLOR + "Resolved endpoint: " + resolvedEndpoint + RESET_COLOR);

        // Retrieve the token from the scenario context using the dynamic key
        String token = scenarioContext.getContext(tokenKey);

        // Validate that the token is not null or empty
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Stored token '" + tokenKey + "' is null or empty.");
        }

        // Resolve placeholders in the request body
        String resolvedBody = resolveRequestBody(source, requestBody);
        System.out.println(YELLOW_COLOR + "Resolved request body: " + resolvedBody + RESET_COLOR);

        // Send the POST request with the token in the Authorization header
        response = RestAssured.given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(resolvedBody)
                .post(resolvedEndpoint);

        // Log the response body for debugging
        System.out.println(YELLOW_COLOR + "Response body: " + response.getBody().asString() + RESET_COLOR);

        // Store the response in the scenario context
        scenarioContext.setResponse(response);

        // Reset the request state
        resetRequest();
    }
    @When("I make a GET request to {string} with the stored token as {string}")
    public void makeGetRequestWithStoredToken(String endpoint, String tokenKey) {
        // Retrieve the token from the scenario context using the dynamic key
        String token = scenarioContext.getContext(tokenKey);
        System.out.println(BLUE_COLOR + "Making GET request to endpoint: " + endpoint + " with stored token from key: " + tokenKey + RESET_COLOR);
        // Resolve placeholders in the endpoint
        String resolvedEndpoint = replacePlaceholders(endpoint);
        System.out.println(BLUE_COLOR + "Resolved endpoint: " + resolvedEndpoint + RESET_COLOR);

        // Add the token to the request headers
        response = RestAssured.given()
                .spec(requestSpec)
                .auth().oauth2(token) // Use the token for authentication
                .get(resolvedEndpoint);

        // Store the response in the scenario context
        scenarioContext.setResponse(response);
        System.out.println(YELLOW_COLOR + "Response Body after storing in ScenarioContext (with Token): " + scenarioContext.getResponse().getBody().asString() + RESET_COLOR); // Add this line
    }
    /**
     * Sends a DELETE request to the specified endpoint.
     *
     * @param endpoint The API endpoint.
     */
    @When("I make a DELETE request to {string}")
    public void makeDeleteRequest(String endpoint) {
        System.out.println(BLUE_COLOR + "Making DELETE request to endpoint: " + endpoint + RESET_COLOR);
        // Resolve placeholders in the endpoint
        String resolvedEndpoint = replacePlaceholders(endpoint);
        System.out.println(BLUE_COLOR + "Resolved endpoint: " + resolvedEndpoint + RESET_COLOR);
        response = RestAssured.given()
                .spec(requestSpec)
                .delete(resolvedEndpoint);
        scenarioContext.setResponse(response);
        resetRequest();
    }
    @When("I make a DELETE request to {string} with stored token {string}")
    public void makeDeleteRequestWithStoredToken(String endpoint, String tokenKey) {
        System.out.println(BLUE_COLOR + "Making DELETE request to endpoint: " + endpoint + " with stored token from key: " + tokenKey + RESET_COLOR);

        // Resolve placeholders in the endpoint
        String resolvedEndpoint = replacePlaceholders(endpoint);
        System.out.println(BLUE_COLOR + "Resolved endpoint: " + resolvedEndpoint + RESET_COLOR);

        // Retrieve the token from the scenario context using the dynamic key
        String token = scenarioContext.getContext(tokenKey);

        // Validate that the token is not null or empty
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Stored token '" + tokenKey + "' is null or empty.");
        }

        // Add the token to the request headers and make the DELETE request
        response = RestAssured.given()
                .spec(requestSpec)
                .auth().oauth2(token) // Use the token for authentication
                .delete(resolvedEndpoint);

        // Store the response in the scenario context
        scenarioContext.setResponse(response);

        // Reset the request state
        resetRequest();
    }
    /**
     * Sends a multipart POST request to upload a file.
     *
     * @param endpoint The API endpoint.
     * @param filePath The path to the file to upload.
     */
    @When("I make a multipart POST request to {string} with file {string}")
    public void makeMultipartPostRequest(String endpoint, String filePath) {
        System.out.println(BLUE_COLOR + "Making a multipart POST request to endpoint: " + endpoint + " with file: " + filePath + RESET_COLOR);

        // Resolve placeholders in the endpoint
        String resolvedEndpoint = replacePlaceholders(endpoint);
        System.out.println(BLUE_COLOR + "Resolved endpoint: " + resolvedEndpoint + RESET_COLOR);

        File file = new File(filePath);

        response = RestAssured.given()
                .spec(requestSpec)
                .multiPart("file", file)
                .post(resolvedEndpoint);
        scenarioContext.setResponse(response);
        resetRequest();
    }
    @When("I optionally retrieve the field {string} from JSON file {string} and store as {string}")
    public void optionallyRetrieveFieldFromJsonFile(String fieldName, String filePath, String keyToStore) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filePath);

        if (file.exists()) {
            JsonNode rootNode = mapper.readTree(file);
            if (rootNode.has(fieldName)) {
                String value = rootNode.get(fieldName).asText();
                scenarioContext.setContext(keyToStore, value);
                System.out.println(YELLOW_COLOR + "Retrieved field '" + fieldName + "' (value: '" + value + "') from JSON file: " + filePath + " and stored as '" + keyToStore + "'" + RESET_COLOR);
            } else {
                System.out.println(YELLOW_COLOR + "Field '" + fieldName + "' not found in JSON file: " + filePath + RESET_COLOR);
            }
        } else {
            System.out.println(YELLOW_COLOR + "JSON file not found at: " + filePath + RESET_COLOR);
        }
    }
    @When("I store the response field {string} as {string} in JSON file {string}")
    public void storeResponseFieldInJsonFile(String jsonPath, String key, String filePath) throws IOException {
        // Extract the value from the response using the JSON path
        String value = scenarioContext.getResponse().jsonPath().getString(jsonPath);

        // Validate that the value is not null or empty
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("No value found in response for JSON path: " + jsonPath);
        }

        // Create a map to store the key-value pair
        Map<String, String> data = new HashMap<>();
        data.put(key, value);

        // Use ObjectMapper to write the map to a JSON file
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(filePath), data);

        System.out.println(YELLOW_COLOR + "Stored response field '" + jsonPath + "' (value: '" + value + "') as key '" + key + "' in JSON file: " + filePath + RESET_COLOR);
    }
    // endregion

    // region RESPONSE VALIDATIONS

    /**
     * Validates the HTTP status code of the response.
     *
     * @param statusCode The expected status code.
     */
    @Then("I see response status {int}")
    public void validateStatus(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode,
                "Unexpected status code: " + response.getStatusCode());
    }

    /**
     * Validates that the response body contains the specified text.
     *
     * @param text The text to check for in the response body.
     */
    @Then("I see response body contains {string}")
    public void validateResponseBodyContains(String text) {
        Assert.assertTrue(response.getBody().asString().contains(text),
                "Response does not contain: " + text);
    }
    /**
     * Validates that the JSON path "id" in the response body is not null.
     */
    @Then("I see JSON path \"id\" is not null")
    public void validateJsonPathIdIsNotNull() {
        Object id = scenarioContext.getResponse().jsonPath().get("id");
        Assert.assertNotNull(id, "JSON path 'id' should not be null");
    }
    /**
     * Validates the value of a specific response header.
     *
     * @param header        The header name.
     * @param expectedValue The expected header value.
     */
    @Then("I see response header {string} is {string}")
    public void validateHeader(String header, String expectedValue) {
        Assert.assertEquals(response.getHeader(header), expectedValue,
                "Header validation failed for: " + header);
    }
    /**
     * Validates that the response body is a JSON array with a specific length and contains objects.
     *
     * @param expectedLength The expected number of elements in the JSON array.
     */
    @Then("I see response body is a JSON array with length {int}")
    public void validateResponseBodyIsJsonArrayWithLength(int expectedLength) {
        ResponseBody body = scenarioContext.getResponse().getBody();
        List<Map<String, Object>> jsonArray = body.jsonPath().getList("$");
        Assert.assertNotNull(jsonArray, "Response body is not a JSON array");
        Assert.assertEquals(jsonArray.size(), expectedLength,
                "JSON array length is not equal to " + expectedLength + ". Actual length: " + jsonArray.size());
    }
    /**
     * Validates that the response body is a JSON array with a specific length and contains strings.
     *
     * @param expectedLength The expected number of elements in the JSON array.
     */
    @Then("I see response body is a JSON array of strings with length {int}")
    public void validateResponseBodyIsJsonArrayOfStringsWithLength(int expectedLength) {
        ResponseBody body = scenarioContext.getResponse().getBody();
        List<String> jsonArray = body.jsonPath().getList("$");
        Assert.assertNotNull(jsonArray, "Response body is not a JSON array");
        Assert.assertEquals(jsonArray.size(), expectedLength,
                "JSON array length is not equal to " + expectedLength + ". Actual length: " + jsonArray.size());
    }

    /**
     * Validates that the response body is a JSON array with a specific length and contains integers.
     *
     * @param expectedLength The expected number of elements in the JSON array.
     */
    @Then("I see response body is a JSON array of integers with length {int}")
    public void validateResponseBodyIsJsonArrayOfIntegersWithLength(int expectedLength) {
        ResponseBody body = scenarioContext.getResponse().getBody();
        List<Integer> jsonArray = body.jsonPath().getList("$");
        Assert.assertNotNull(jsonArray, "Response body is not a JSON array");
        Assert.assertEquals(jsonArray.size(), expectedLength,
                "JSON array length is not equal to " + expectedLength + ". Actual length: " + jsonArray.size());
    }
    /**
     * Validates that the value of a specific response header is greater than a given value.
     *
     * @param headerName    The name of the response header to check.
     * @param expectedValue The value that the header value should be greater than.
     */
    @Then("I see response header {string} is greater than {int}")
    public void validateResponseHeaderGreaterThan(String headerName, int expectedValue) {
        String headerValue = scenarioContext.getResponse().getHeader(headerName);
        Assert.assertNotNull(headerValue, "Response header '" + headerName + "' is not present");
        try {
            int actualValue = Integer.parseInt(headerValue);
            Assert.assertTrue(actualValue > expectedValue,
                    "Response header '" + headerName + "' value (" + actualValue + ") is not greater than " + expectedValue);
        } catch (NumberFormatException e) {
            Assert.fail("Response header '" + headerName + "' value ('" + headerValue + "') is not a valid integer");
        }
    }
    /**
     * Validates the value of a specific response cookie.
     *
     * @param cookieName    The cookie name.
     * @param expectedValue The expected cookie value.
     */
    @Then("I see response cookie {string} is {string}")
    public void validateCookie(String cookieName, String expectedValue) {
        Assert.assertEquals(response.getCookie(cookieName), expectedValue,
                "Cookie validation failed for: " + cookieName);
    }

    /**
     * Validates that the response time is under the specified threshold.
     *
     * @param maxTime The maximum allowed response time in milliseconds.
     */
    @Then("I see response time is under {int}ms")
    public void validateResponseTime(int maxTime) {
        System.out.println(YELLOW_COLOR + "Validating response time is under: " + maxTime + "ms" + RESET_COLOR);
        Assert.assertTrue(response.getTime() < maxTime,
                "Response time exceeded " + maxTime + "ms");
    }

    /**
     * Validates the value of a specific JSON path in the response.
     *
     * @param jsonPath      The JSON path to validate.
     * @param expectedValue The expected value at the JSON path.
     */
    @Then("I see JSON path {string} equals {string}")
    public void validateJsonPath(String jsonPath, String expectedValue) {
        System.out.println(YELLOW_COLOR + "Validating JSON path: " + jsonPath + " = " + expectedValue + RESET_COLOR);
        Assert.assertEquals(response.jsonPath().getString(jsonPath), expectedValue,
                "JSON path validation failed: " + jsonPath);
    }

    // endregion
    private String replacePlaceholders(String body) {
        System.out.println("Input body before resolving placeholders: " + body);

        StringBuffer resolvedBody = new StringBuffer(body); // Initialize with the original body

        // Handle config.properties placeholders ($$)
        Pattern configPattern = Pattern.compile("\\$\\$(.*?)\\$\\$");
        Matcher configMatcher = configPattern.matcher(resolvedBody);
        StringBuffer tempBody = new StringBuffer();

        while (configMatcher.find()) {
            String key = configMatcher.group(1).trim();
            String replacement = ConfigLoader.getProperty(key, "");
            System.out.println("Value fetched from config.properties for key '" + key + "': " + replacement);
            if (replacement.isEmpty()) {
                throw new IllegalArgumentException("No value found in config.properties for key: " + key);
            }
            configMatcher.appendReplacement(tempBody, replacement);
        }
        configMatcher.appendTail(tempBody);
        resolvedBody.setLength(0);
        resolvedBody.append(tempBody);
        tempBody.setLength(0);

        // Handle scenario context placeholders (<>)
        Pattern contextPattern = Pattern.compile("<(.*?)>");
        Matcher contextMatcher = contextPattern.matcher(resolvedBody);

        while (contextMatcher.find()) {
            String key = contextMatcher.group(1).trim();
            Object value = scenarioContext.getContext(key);
            if (value != null) {
                System.out.println("Value fetched from scenario context for key '" + key + "': " + value.toString());
                contextMatcher.appendReplacement(tempBody, value.toString());
            } else {
                System.out.println("No value found in scenario context for key: " + key);
                contextMatcher.appendReplacement(tempBody, "<" + key + ">"); // Keep the placeholder if not found
            }
        }
        contextMatcher.appendTail(tempBody);
        resolvedBody.setLength(0);
        resolvedBody.append(tempBody);
        tempBody.setLength(0);

        // Handle dynamic data placeholders ({{}})
        Pattern dynamicPattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher dynamicMatcher = dynamicPattern.matcher(resolvedBody);

        while (dynamicMatcher.find()) {
            String key = dynamicMatcher.group(1).trim();
            String replacement = "";

            switch (key.toLowerCase()) {
                case "randomemail":
                    replacement = "test" + java.util.UUID.randomUUID().toString().replace("-", "") + "@yopmail.com";
                    System.out.println("Generated random email: " + replacement);
                    break;
                case "randomnumber":
                    replacement = String.valueOf(new java.util.Random().nextInt(1000)); // Example for a random number
                    System.out.println("Generated random number: " + replacement);
                    break;
                case "randomname":
                    int length = 10;
                    java.util.Random random = new java.util.Random();
                    StringBuilder randomName = new StringBuilder(length);
                    for (int i = 0; i < length; i++) {
                        char c = (char) ('a' + random.nextInt(26)); // Generate lowercase letters
                        randomName.append(c);
                    }
                    replacement = randomName.toString();
                    System.out.println("Generated random name: " + replacement);
                    break;
                // Add more cases here for other dynamic inputs
                default:
                    replacement = "{{" + key + "}}"; // If no match, keep the original placeholder
                    break;
            }
            dynamicMatcher.appendReplacement(tempBody, replacement);
        }
        dynamicMatcher.appendTail(tempBody);
        resolvedBody.setLength(0);
        resolvedBody.append(tempBody);

        System.out.println("Resolved body after replacing placeholders: " + resolvedBody.toString());
        return resolvedBody.toString();
    }
    // region UTILITIES

    /**
     * Resolves the request body based on the source.
     *
     * @param source The source of the request body ("config", "file", or "inline").
     * @param requestBody  The key (for config/file) or the inline body content.
     * @return The resolved request body as a string.
     */
    private String resolveRequestBody(String source, String requestBody) {
        String resolvedBody;
        switch (source.toLowerCase()) {
            case "inline":
                resolvedBody = replacePlaceholders(requestBody);
                break;

            default:
                throw new IllegalArgumentException("Unsupported source: " + source);
        }

        // Validate JSON
        try {
            new ObjectMapper().readTree(resolvedBody); // Throws exception if invalid JSON
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON: " + resolvedBody, e);
        }

        return resolvedBody;
    }

    /**
     * Resets the request state after each request.
     */
    private void resetRequest() {
        requestSpec = new RequestSpecBuilder().build();
        queryParams.clear();
        pathParams.clear();
//        RestAssured.reset(); // Clears global settings like base URI, cookies, etc.
    }

    // endregion
}