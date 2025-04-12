package org.example.stepDefinitions.api;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

public class ApiStepDefinitions {

    private Response response;
    private RequestSpecification requestSpec;
    private final Map<String, Object> requestParams = new HashMap<>();

    // region REQUEST SETUP
    @When("I set base URL to {string}")
    public void setBaseUrl(String baseUrl) {
        RestAssured.baseURI = baseUrl;
        requestSpec = new RequestSpecBuilder().build();
    }

    @When("I set header {string} to {string}")
    public void setHeader(String header, String value) {
        requestSpec = requestSpec.header(header, value);
    }

    @When("I set query parameter {string} to {string}")
    public void setQueryParam(String param, String value) {
        requestParams.put(param, value);
    }

    @When("I set path parameter {string} to {string}")
    public void setPathParam(String param, String value) {
        requestParams.put(param, value);
    }

    @When("I set cookie {string} to {string}")
    public void setCookie(String name, String value) {
        requestSpec = requestSpec.cookie(name, value);
    }

    @When("I set bearer token {string}")
    public void setBearerToken(String token) {
        requestSpec = requestSpec.auth().oauth2(token);
    }
    // endregion

    // region REQUEST METHODS
    @When("I make a GET request to {string}")
    public void makeGetRequest(String endpoint) {
        response = RestAssured.given()
                .spec(requestSpec)
                .queryParams(requestParams)
                .get(endpoint);
        resetRequest();
    }

    @When("I make a POST request to {string} with body:")
    public void makePostRequest(String endpoint, String body) {
        response = RestAssured.given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(body)
                .post(endpoint);
        resetRequest();
    }

    @When("I make a PUT request to {string} with body:")
    public void makePutRequest(String endpoint, String body) {
        response = RestAssured.given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(body)
                .put(endpoint);
        resetRequest();
    }

    @When("I make a DELETE request to {string}")
    public void makeDeleteRequest(String endpoint) {
        response = RestAssured.given()
                .spec(requestSpec)
                .delete(endpoint);
        resetRequest();
    }
    // endregion

    // region RESPONSE VALIDATIONS
    @Then("I see response status {int}")
    public void validateStatus(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode,
                "Unexpected status code: " + response.getStatusCode());
    }

    @Then("I see response body contains {string}")
    public void validateResponseBodyContains(String text) {
        Assert.assertTrue(response.getBody().asString().contains(text),
                "Response does not contain: " + text);
    }

    @Then("I see response header {string} is {string}")
    public void validateHeader(String header, String expectedValue) {
        Assert.assertEquals(response.getHeader(header), expectedValue,
                "Header validation failed for: " + header);
    }

    @Then("I see response cookie {string} is {string}")
    public void validateCookie(String cookieName, String expectedValue) {
        Assert.assertEquals(response.getCookie(cookieName), expectedValue,
                "Cookie validation failed for: " + cookieName);
    }

    @Then("I see response time is under {int}ms")
    public void validateResponseTime(int maxTime) {
        Assert.assertTrue(response.getTime() < maxTime,
                "Response time exceeded " + maxTime + "ms");
    }

    @Then("I see JSON path {string} equals {string}")
    public void validateJsonPath(String jsonPath, String expectedValue) {
        Assert.assertEquals(response.jsonPath().getString(jsonPath), expectedValue,
                "JSON path validation failed: " + jsonPath);
    }
    // endregion

    // region UTILITIES
    private void resetRequest() {
        requestSpec = new RequestSpecBuilder().build();
        requestParams.clear();
    }
    // endregion
}
