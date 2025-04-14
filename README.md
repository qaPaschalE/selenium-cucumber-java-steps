

# Selenium Cucumber Java Steps

<table style="margin-left: auto; margin-right: auto;">
  <tr>
    <td style="text-align: center;">
      <img src="https://github.com/qaPaschalE/cypress-plugins/blob/main/assets/paschal%20logo%20(2).png?raw=true" alt="paschal Logo" style="max-width:120px; margin-top:15px;" />
    </td>
  </tr>
</table>

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/qaPaschalE/selenium-cucumber-java-steps/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.qaPaschalE/selenium-cucumber-java-steps.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.github.qaPaschalE/selenium-cucumber-java-steps)
[![GitHub Issues](https://img.shields.io/github/issues/qaPaschalE/selenium-cucumber-java-steps.svg)](https://github.com/qaPaschalE/selenium-cucumber-java-steps/issues)
[![Contributors](https://img.shields.io/github/contributors/qaPaschalE/selenium-cucumber-java-steps.svg)](https://github.com/qaPaschalE/selenium-cucumber-java-steps/graphs/contributors)

A reusable Selenium and Cucumber integration library for automating web applications with step definitions for common actions like clicking, typing, finding elements, and more.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Prerequisites](#prerequisites)
4. [Installation](#installation)
5. [Usage](#usage)
6. [Step Definitions](#step-definitions)
7. [Contributing](#contributing)
8. [License](#license)

---

## Overview

This project provides a collection of reusable step definitions for automating web applications using **Selenium**, **Cucumber**, and **Java**. It simplifies common tasks like interacting with buttons, links, text fields, dropdowns, and more, while also supporting advanced actions like double-clicking, right-clicking, and handling browser navigation.

The library is designed to be modular, making it easy to integrate into your existing automation projects or use as a standalone framework.

---

## Features

- **Reusable Step Definitions**: Predefined steps for common actions such as clicking, typing, finding elements, and navigating.
- **Element Locators**: Supports CSS selectors, XPath, and text-based searches for locating elements.
- **Browser Actions**: Includes actions like navigating back/forward, refreshing the page, and handling cookies/local storage.
- **Assertions**: Built-in assertions to validate element attributes, visibility, and text content.
- **Advanced Interactions**: Supports double-click, right-click, focus, blur, and other advanced interactions.
- **TestNG Integration**: Seamlessly integrates with TestNG for test execution and reporting.
- **Logging**: Uses SLF4J for clean and structured logging during test execution.
- **Extensible**: Easily extendable to add custom steps or modify existing ones.

---

## Prerequisites

Before using this library, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 8 or higher.
- **Maven**: For building and managing dependencies.
- **WebDriverManager**: Automatically manages browser drivers (e.g., ChromeDriver, GeckoDriver).
- **IDE**: IntelliJ IDEA, Eclipse, or any Java-compatible IDE for development.

---

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/qaPaschalE/selenium-cucumber-java-steps.git
cd selenium-cucumber-java-steps
```

### 2. Build the Project
Run the following Maven command to build the project:
```bash
mvn clean install
```

### 3. Add Dependency
If you’re publishing this package to a repository (e.g., GitHub Packages or Maven Central), include it in your project’s `pom.xml`:
```xml
<dependency>
    <groupId>com.github.qaPaschalE</groupId>
    <artifactId>selenium-cucumber-java-steps</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

---

## Usage

### 1. Configure WebDriver
Ensure that WebDriverManager is set up to download and manage browser drivers automatically:
```java
WebDriverManager.chromedriver().setup();
WebDriver driver = new ChromeDriver();
```

### 2. Write Feature Files
Create `.feature` files in your `src/test/resources` directory. Example:
```gherkin
Feature: Login functionality

  Scenario: Successful login
    Given I go to URL "https://example.com/login"
    And I get element by selector "#userName"
    And I type "test"
    And I click on button "Login"
    Then I see text "Welcome, User!"
```

### 3. Run Tests
Use Maven to execute your tests:
```bash
mvn test
```

---

## Step Definitions

Below are some of the predefined step definitions included in this library:

### Browser Navigation
- `Given I go to URL "<url>"`
- `When I go back`
- `When I go forward`
- `When I reload the page`

### Element Interaction
- `When I click on button "<selector>"`
- `When I click on link "<text>"`
- `When I click on xpath "selector"`
- `When I click on selector "selector"`

### Assertions
- `Then I see text "<text>"`
- `Then I see element exists "<selector>"`
- `Then Then I see button "<buttonText>"`
- `Then I see button "<text>"`

### Advanced Actions
- `When I double click on "<selector>"`
- `When I right click on "<selector>"`
- `When I type "selector" into the "nth" element`
- `When I get nth element "selector" at index 1 "<selector>"`


---

## Contributing

We welcome contributions! If you’d like to contribute to this project, follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and ensure all tests pass (`mvn test`).
4. Submit a pull request with a detailed description of your changes.

---

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## Contact

For questions or feedback, feel free to reach out:

- **GitHub**: [@qaPaschalE](https://github.com/qaPaschalE)
- **Email**: paschal.enyimiri@gmail.com
- **LinkedIn**: https://www.linkedin.com/in/chetachi-enyimiri-05237a144/



