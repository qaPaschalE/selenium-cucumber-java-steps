Feature: Create and Retrieve User

  Scenario: Create a new user
    Given I set base URL to "https://api.example.com"
    And I set header "Content-Type" to "application/json"
    And I set header "Authorization" to "Bearer ${authToken}"
    When I make a POST request to "/users" with body:
      """
      {
        "name": "John Doe",
        "email": "john.doe@example.com"
      }
      """
    Then I see response status 201
    And I see JSON path "id" is not null
    And I store the response field "id" as "userId"

  Scenario: Retrieve the created user
    Given I set base URL to "https://api.example.com"
    And I set header "Authorization" to "Bearer ${authToken}"
    When I make a GET request to "/users/${userId}"
    Then I see response status 200
    And I see JSON path "name" equals "John Doe"
    And I see JSON path "email" equals "john.doe@example.com"


  Scenario: Create a user with inline JSON body
    Given I set base URL to "https://api.example.com"
    And I set header "Content-Type" to "application/json"
    When I make a POST request to "/users" with body from "inline":
    """
    {
      "username": "{testuser}",
      "email": "{testemail}"
    }
    """
    Then I see response status 201
    And I see JSON path "username" equals "testuser123"
    And I see JSON path "email" equals "testuser@example.com"