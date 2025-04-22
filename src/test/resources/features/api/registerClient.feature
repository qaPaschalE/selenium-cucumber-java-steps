Feature: Register Client

  Background:
    Given I set base URL to "/"

  @api
  Scenario: Unsuccessful Register a client
    When I make a POST request to "/api-clients" with body from "Inline":
      """
      {
        "clientName": "{{randomName}}",
        "clientEmail": "$$clientEmail$$"
      }
      """
    Then I see response status 409
    Then I see JSON path "error" equals "API client already registered. Try a different email."

  @api
  Scenario: Successful Register a client
    When I make a POST request to "/api-clients" with body from "Inline":
      """
      {
        "clientName": "{{randomName}}",
        "clientEmail": "{{randomEmail}}"
      }
      """
    Then I see response status 201
    Then I see response body contains "accessToken"

  @api
  Scenario:  Get API status
    When I make a GET request to "/status"
    Then I see response status 200