Feature: User Authentication

  Background:
    When I make a POST request to "/api-clients" with body from "Inline":
      """
      {
        "clientName": "{{randomName}}",
        "clientEmail": "$$password$$"
      }
      """
    Then I see response status 200
    And I store the response field "accessToken" as "accessToken"

  @smoke
  Scenario: Successful login returns a token
    When I make a POST request to "/Account/v1/Login" with body from "Inline":
      """
      {
        "userName": "userG",
        "password": "$$password$$"
      }
      """
    Then I see response status 200
    And I store the response field "token" as "authToken"

  @smoke
  Scenario: Generate a token
    And I set header "Authorization" to "Bearer ${authToken}"
    When I make a POST request to "/Account/v1/GenerateToken" with body from "Inline":
      """
      {
        "userName": "userG",
        "password": "$$password$$"
      }
      """
    Then I see response status 200

  Scenario: Use stored token to access a protected endpoint
    And I set header "Authorization" to "Bearer ${authToken}"
    When I make a GET request to "/users/me"
    Then I see response status 200
    And I see JSON path "username" equals "testuser"