package com.github.qaPaschalE.stepDefinitions.db;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DbStepDefinitions {

    private Connection connection;
    private ResultSet resultSet;
    private final Map<String, Object> queryParameters = new HashMap<>();

    // region SETUP & TEARDOWN
    @Before("@db")
    public void setUp() throws SQLException {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new RuntimeException("Missing database environment variables");
        }

        connection = DriverManager.getConnection(url, user, password);
    }

    @After("@db")
    public void tearDown() throws SQLException {
        if (resultSet != null)
            resultSet.close();
        if (connection != null)
            connection.close();
    }
    // endregion

    // region QUERY BUILDERS
    @When("I set DB query parameter {string} to {string}")
    public void stringParameterSet(String key, String value) {
        queryParameters.put(key, value);
    }

    @When("I set DB query parameter {string} to {int}")
    public void setIntParameter(String key, int value) {
        queryParameters.put(key, value);
    }
    // endregion

    // region VALIDATION STEPS
    @Then("the database table {string} should have {int} rows")
    public void validateRowCount(String tableName, int expectedCount) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            resultSet = stmt.executeQuery("SELECT COUNT(*) FROM " + sanitizeTable(tableName));
            resultSet.next();
            int actualCount = resultSet.getInt(1);
            Assert.assertEquals(
                    actualCount,
                    expectedCount, "Row count mismatch for table: " + tableName);
        }
    }

    @Then("the database table {string} should contain a record where {string} = {string}")
    public void validateRecordExists(String tableName, String column, String value) throws SQLException {
        String sql = "SELECT * FROM " + sanitizeTable(tableName) + " WHERE " + column + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, value);
            resultSet = pstmt.executeQuery();
            Assert.assertTrue(resultSet.next(), "No record found where " + column + " = " + value);
        }
    }

    @Then("the database table {string} should have {int} records matching:")
    public void validateRecordCountWithParams(String tableName, int expectedCount, String parameters)
            throws SQLException {
        String sql = buildParameterizedQuery(tableName, parameters);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParameters(pstmt, parameters);
            resultSet = pstmt.executeQuery();
            resultSet.last();
            Assert.assertEquals(
                    resultSet.getRow(),
                    expectedCount,
                    "Record count mismatch");
        }
    }
    // endregion

    // region HELPER METHODS
    private String sanitizeTable(String tableName) {
        if (!tableName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        return tableName;
    }

    private String buildParameterizedQuery(String tableName, String parameters) {
        String[] conditions = parameters.split(",");
        StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(tableName)
                .append(" WHERE ");

        for (int i = 0; i < conditions.length; i++) {
            String[] parts = conditions[i].trim().split("=");
            sql.append(parts[0]).append(" = ?");
            if (i < conditions.length - 1)
                sql.append(" AND ");
        }

        return sql.toString();
    }

    private void setParameters(PreparedStatement pstmt, String parameters) throws SQLException {
        String[] conditions = parameters.split(",");
        for (int i = 0; i < conditions.length; i++) {
            String[] parts = conditions[i].trim().split("=");
            String key = parts[0];
            String value = parts[1];

            if (queryParameters.containsKey(value)) {
                value = queryParameters.get(value).toString();
            }

            pstmt.setString(i + 1, value);
        }
    }
    // endregion
}
