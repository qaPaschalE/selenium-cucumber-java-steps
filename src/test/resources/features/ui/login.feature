# src/test/resources/features/ui/login.feature
Feature: User Login
  Background: Preconditions

  @e2e
  Scenario: Successful login
    Given I go to URL "/opencart"
    Then I see text "Desktops"
    Then I click on link "Desktops"
    When I find element by title ""
