Feature: File Upload

  Scenario: Upload a profile picture
    Given I set base URL to "https://api.example.com"
    And I set header "Authorization" to "Bearer ${authToken}"
    And I set header "Content-Type" to "multipart/form-data"
    When I make a multipart POST request to "/users/${userId}/upload" with file "profile.jpg"
    Then I see response status 200
    And I see response body contains "File uploaded successfully"