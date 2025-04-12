package org.example.util;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class hooks {

    @Before
    public void setUp() {
        // Initialize WebDriver before each scenario
        WebDriverUtils.setup();
        System.out.println("WebDriver initialized for the scenario.");
    }

    @After
    public void tearDown(Scenario scenario) {
        // Take a screenshot if the scenario fails
        if (scenario.isFailed()) {
            System.out.println("Scenario failed! Taking screenshot...");
            try {
                final byte[] screenshot = ((TakesScreenshot) WebDriverUtils.getDriver())
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "failure-screenshot");
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }

        // Quit WebDriver after each scenario
        try {
            WebDriverUtils.quitDriver();
            System.out.println("WebDriver quit successfully.");
        } catch (Exception e) {
            System.err.println("Failed to quit WebDriver: " + e.getMessage());
        }
    }
}