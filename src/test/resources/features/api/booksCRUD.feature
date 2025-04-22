@books
Feature: CRUD on Book Orders
Background:
  Given I set base URL to "/"


  @getAllBooks
  Scenario: Get all Books
    When I make a POST request to "/api-clients" with body from "Inline":
      """
      {
        "clientName": "{{randomName}}",
        "clientEmail": "{{randomEmail}}"
      }
      """
    Then I see response status 201
    Then I see response body contains "accessToken"
    And I store the response field "accessToken" as "accessToken" in JSON file "src/test/resources/json/token.json"
    When I optionally retrieve the field "accessToken" from JSON file "src/test/resources/json/token.json" and store as "accessToken"
    When I make a GET request to "/books" with the stored token as "accessToken"
    Then I see response status 200
    And I see JSON path "[1].id" equals "2"
    And I store the response field "[1].id" as "bookID"

  @placeABookOrder
  Scenario: Place a book order by book ID
    When I optionally retrieve the field "accessToken" from JSON file "src/test/resources/json/token.json" and store as "accessToken"
    When I make a GET request to "/books" with the stored token as "accessToken"
    Then I see response status 200
    And I see JSON path "[1].id" equals "2"
    And I store the response field "[0].id" as "bookID"
    When I make a POST request to "/orders/" with the stored token as "accessToken" and body from "Inline":
  """
  {
    "bookId": <bookID>,
    "customerName": "Testify Academy"
  }
  """
    Then I see response status 201
    Then I see JSON path "created" equals "true"


  @updateABookOrder
  Scenario: Update an existing book order
    When I optionally retrieve the field "accessToken" from JSON file "src/test/resources/json/token.json" and store as "accessToken"
    When I make a GET request to "/orders" with the stored token as "accessToken"
    Then I see response status 200
    And I store the response field "[0].bookId" as "bookID"
    And I store the response field "[0].customerName" as "customerName"
    When I make a POST request to "/orders" with the stored token as "accessToken" and body from "Inline":
    """
    {
      "bookId": <bookID>,
      "customerName": "<customerName>"
    }
    """
    Then I see response status 201
    And I see JSON path "created" equals "true"
    And I store the response field "orderId" as "orderId" in JSON file "src/test/resources/json/orderId.json"


  @deleteABookOrder
  Scenario: Delete an existing book order
    When I optionally retrieve the field "accessToken" from JSON file "src/test/resources/json/token.json" and store as "accessToken"
    When I optionally retrieve the field "orderId" from JSON file "src/test/resources/json/orderId.json" and store as "orderId"
    When I make a DELETE request to "/orders/<orderId>" with stored token "accessToken"
    Then I see response status 204
