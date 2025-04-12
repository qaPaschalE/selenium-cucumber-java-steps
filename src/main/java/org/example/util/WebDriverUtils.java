package org.example.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

public class WebDriverUtils {

    private static WebDriver driver;

    /**
     * Initializes the WebDriver instance.
     */
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Set a global implicit wait (e.g., 60 seconds)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
    }

    /**
     * Returns the WebDriver instance.
     *
     * @return The WebDriver instance.
     */
    public static WebDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been initialized. Call WebDriverUtils.setup() first.");
        }
        return driver;
    }

    /**
     * Quits the WebDriver instance.
     */
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null; // Reset the driver to null after quitting
        }
    }
}