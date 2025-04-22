Feature: List Users with Pagination

  Scenario: Retrieve first page of users
    Given I set base URL to "https://api.example.com"
    And I set header "Authorization" to "Bearer ${authToken}"
    When I make a GET request to "/users?page=1&per_page=10"
    Then I see response status 200
    And I see response body is a JSON array with length 10
    And I see response header "X-Total-Count" is greater than 0