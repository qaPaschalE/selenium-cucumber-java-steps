# src/test/resources/features/ui/login.feature
Feature: User Login

  Background: Preconditions

  @e2e
  Scenario: Successful login
    Given I go to URL "/"
    # Then I see text "Forms"
    And I click on xpath "//h5[contains(text(), 'Elements')]"
    And I click on text "Text Box"
    And I get element by selector "#userName"
    And I type "test"
    # And I get nth element "/" at index 120
    And I wait 120 seconds
