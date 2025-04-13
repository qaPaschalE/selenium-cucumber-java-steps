package com.github.qaPaschalE.stepDefinitions.ui;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.github.qaPaschalE.util.ConfigLoader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.github.qaPaschalE.util.WebDriverUtils;
import java.time.Duration;
import java.util.List;
import org.testng.Assert;
import static org.testng.Assert.*; // Optional, for static imports

public class UiStepDefinitions {

    private final WebDriver driver = WebDriverUtils.getDriver();
    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    private By getLocator(String selector) {
        if (selector.startsWith("xpath:")) {
            return By.xpath(selector.substring(6).trim());
        } else if (selector.startsWith("//") || selector.startsWith("./") || selector.contains("/")) {
            // Basic check for common XPath patterns
            return By.xpath(selector.trim());
        } else {
            return By.cssSelector(selector);
        }
    }

    private By getLocatorForText(String text) {
        if (text.startsWith("xpath:")) {
            // Extract the XPath expression after "xpath:"
            return By.xpath(text.substring(6).trim());
        } else {
            // Default to XPath for text-based searches
            return By.xpath("//*[contains(text(), '" + text + "')]");
        }
    }

    private WebElement getNthElement(int index) {
        Assert.assertNotNull(lastFoundElements, "No elements found.");
        Assert.assertTrue(index > 0, "Index must be greater than 0.");
        int zeroBasedIndex = index - 1; // Convert to zero-based index
        Assert.assertTrue(zeroBasedIndex < lastFoundElements.size(), "Element at index " + index + " not found.");
        return lastFoundElements.get(zeroBasedIndex);
    }

    private int parseOrdinal(String ordinal) {
        // Remove non-numeric characters and parse the number
        String numericPart = ordinal.replaceAll("[^0-9]", "");
        return Integer.parseInt(numericPart);
    }

    private WebElement lastFoundElement; // For single element
    private List<WebElement> lastFoundElements; // For multiple elements
    // --- "Find Element By..." Steps ---

    private WebElement findElement(By locator) {
        try {
            return WebDriverUtils.getDriver().findElement(locator);
        } catch (NoSuchElementException e) {
            throw new AssertionError("Element not found: " + locator, e);
        }
    }

    @When("I find button by text {string}")
    public void iFindButtonByText(String text) {
        lastFoundElement = findElement(getLocator("button:contains('" + text + "')"));
        System.out.println(lastFoundElement);

    }

    @When("I find closest element {string}")
    public void iFindClosestElement(String selector) {
        // Requires JavaScriptExecutor to implement "closest" logic
        WebElement element = (WebElement) ((JavascriptExecutor) WebDriverUtils.getDriver())
                .executeScript("return arguments[0].closest(arguments[1]);", lastFoundElement, selector);
        lastFoundElement = element;
        System.out.println(element);

    }

    @When("I find element by label text {string}")
    public void iFindElementByLabelText(String labelText) {
        lastFoundElement = findElement(getLocator("label:contains('" + labelText + "') + *"));
        System.out.println(lastFoundElement);

    }

    @When("I find input by name {string}")
    public void iFindInputByName(String name) {
        lastFoundElement = findElement(By.name(name));
        System.out.println(lastFoundElement);

    }

    @When("I find input by placeholder text {string}")
    public void iFindInputByPlaceholderText(String placeholderText) {
        lastFoundElement = findElement(getLocator("input[placeholder='" + placeholderText + "']"));
        System.out.println(lastFoundElement);

    }

    @When("I find textarea by label text {string}")
    public void iFindTextareaByLabelText(String labelText) {
        lastFoundElement = findElement(
                getLocator("label:contains('" + labelText + "') + textarea"));
        System.out.println(lastFoundElement);

    }

    // --- Actions to Perform on the Last Found Element ---
    @When("I click")
    public void iClickTheElement() {
        Assert.assertNotNull(lastFoundElement, "No element found to click.");
        lastFoundElement.click();
        lastFoundElement = null; // Reset after interaction
        System.out.println(lastFoundElement);

    }

    @When("I click first element")
    public void iClickTheFirstElement() {
        Assert.assertNotNull(lastFoundElements, "No elements found to click.");
        Assert.assertFalse(lastFoundElements.isEmpty(), "No elements found to click.");
        lastFoundElements.get(0).click();
        lastFoundElements = null; // Reset after interaction
    }

    @When("I click {string} element")
    public void iClickNthElement(String ordinal) {
        Assert.assertNotNull(lastFoundElements, "No elements found to click.");

        // Parse the ordinal string into an integer
        int index = parseOrdinal(ordinal);
        Assert.assertTrue(index > 0, "Index must be greater than 0.");

        // Convert to zero-based index
        int zeroBasedIndex = index - 1;
        Assert.assertTrue(zeroBasedIndex < lastFoundElements.size(), "Element at index " + index + " not found.");

        // Click the nth element
        WebElement nthElement = lastFoundElements.get(zeroBasedIndex);
        nthElement.click();
    }

    @When("I click the last element")
    public void iClickTheLastElement() {
        Assert.assertNotNull(lastFoundElements, "No elements found to click.");
        Assert.assertFalse(lastFoundElements.isEmpty(), "No elements found to click.");
        WebElement lastElement = lastFoundElements.get(lastFoundElements.size() - 1);
        lastElement.click();
        lastFoundElements = null; // Reset after interaction
    }

    @When("I type {string}")
    public void iTypeIntoTheElement(String text) {
        Assert.assertNotNull(lastFoundElement, "No element found to type into.");
        lastFoundElement.sendKeys(text);
        lastFoundElement = null; // Reset after interaction
        System.out.println(lastFoundElement);

    }

    @When("I type {string} into the {string} element")
    public void iTypeIntoTheNthElement(String text, String ordinal) {
        Assert.assertNotNull(lastFoundElements, "No elements found to type into.");

        // Parse the ordinal string into an integer
        int index = parseOrdinal(ordinal);
        Assert.assertTrue(index > 0, "Index must be greater than 0.");

        // Convert to zero-based index
        int zeroBasedIndex = index - 1;
        Assert.assertTrue(zeroBasedIndex < lastFoundElements.size(), "Element at index " + index + " not found.");

        // Type into the nth element
        WebElement nthElement = lastFoundElements.get(zeroBasedIndex);
        nthElement.clear(); // Clear existing text if necessary
        nthElement.sendKeys(text);
    }

    @When("I clear")
    public void iClearTheElement() {
        Assert.assertNotNull(lastFoundElement, "No element found to clear.");
        lastFoundElement.clear();
        lastFoundElement = null; // Reset after interaction
        System.out.println(lastFoundElement);

    }

    @When("I check")
    public void iCheckTheElement() {
        Assert.assertNotNull(lastFoundElement, "No element found to check.");
        System.out.println(lastFoundElement);

        try {
            if (!lastFoundElement.isSelected()) {
                lastFoundElement.click();
            }
        } catch (Exception e) {
            throw new AssertionError("Failed to check the element.", e);
        } finally {
            lastFoundElement = null; // Reset after interaction
        }
    }

    @When("I check {string} element")
    public void iCheckTheNthElement(String ordinal) {
        Assert.assertNotNull(lastFoundElements, "No elements found to check.");

        // Parse the ordinal string into an integer
        int index = parseOrdinal(ordinal);
        Assert.assertTrue(index > 0, "Index must be greater than 0.");

        // Convert to zero-based index
        int zeroBasedIndex = index - 1;
        Assert.assertTrue(zeroBasedIndex < lastFoundElements.size(), "Element at index " + index + " not found.");

        // Check the nth element
        WebElement nthElement = lastFoundElements.get(zeroBasedIndex);
        if (!nthElement.isSelected()) {
            nthElement.click();
        }
    }

    @When("I uncheck")
    public void iUncheckTheElement() {
        Assert.assertNotNull(lastFoundElement, "No element found to uncheck.");
        try {
            if (lastFoundElement.isSelected()) {
                lastFoundElement.click();
            }
        } catch (Exception e) {
            throw new AssertionError("Failed to uncheck the element.", e);
        } finally {
            lastFoundElement = null; // Reset after interaction
        }
    }

    @When("I uncheck the {string} element")
    public void iUncheckTheNthElement(String ordinal) {
        Assert.assertNotNull(lastFoundElements, "No elements found to uncheck.");

        // Parse the ordinal string into an integer
        int index = parseOrdinal(ordinal);
        Assert.assertTrue(index > 0, "Index must be greater than 0.");

        // Convert to zero-based index
        int zeroBasedIndex = index - 1;
        Assert.assertTrue(zeroBasedIndex < lastFoundElements.size(), "Element at index " + index + " not found.");

        // Uncheck the nth element
        WebElement nthElement = lastFoundElements.get(zeroBasedIndex);
        if (nthElement.isSelected()) {
            nthElement.click();
        }
    }

    @When("I set value {string}")
    public void iSetValueToTheElement(String value) {
        Assert.assertNotNull(lastFoundElement, "No element found to set value.");
        try {
            lastFoundElement.clear(); // Clear existing text
            lastFoundElement.sendKeys(value); // Set new value
        } catch (Exception e) {
            throw new AssertionError("Failed to set value '" + value + "' to the element.", e);
        } finally {
            lastFoundElement = null; // Reset after interaction
        }
    }

    @Then("I see button {string}")
    public void iSeeButton(String selector) {
        try {
            WebElement button = driver.findElement(By.cssSelector(selector)); // Or By.xpath, etc.
            Assert.assertTrue(button.isDisplayed(), "Button not displayed: " + selector);
        } catch (NoSuchElementException e) {
            Assert.fail("Button not found: " + selector);
        }
    }

    @Then("I do not see button {string}")
    public void iDoNotSeeButton(String selector) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(selector)));
        } catch (org.openqa.selenium.TimeoutException e) {
            // Element might still be present or take longer to disappear
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                Assert.assertTrue(element.isDisplayed(), "Button is displayed: " + selector);
            } catch (NoSuchElementException noSuchElementException) {
                // Expected, element is not present
            }
        }
    }

    @When("I click on selector {string}")
    public void iClickOnSelector(String cssSelector) {
        try {
            WebElement element = WebDriverUtils.getDriver().findElement(By.cssSelector(cssSelector));
            ((JavascriptExecutor) WebDriverUtils.getDriver()).executeScript("arguments[0].scrollIntoView(true);",
                    element);
            element.click();
        } catch (NoSuchElementException e) {
            throw new AssertionError("Element with CSS selector '" + cssSelector + "' not found.", e);
        }
    }

    @When("I click on {string}")
    public void iClickOn(String locator) {
        try {
            WebElement element = WebDriverUtils.getDriver().findElement(getLocator(locator));
            ((JavascriptExecutor) WebDriverUtils.getDriver()).executeScript("arguments[0].scrollIntoView(true);",
                    element);
            element.click();
        } catch (NoSuchElementException e) {
            throw new AssertionError("Element with locator '" + locator + "' not found.", e);
        }
    }

    @When("I click on xpath {string}")
    public void iClickOnXpath(String xpath) {
        try {
            WebElement element = WebDriverUtils.getDriver().findElement(By.xpath(xpath));
            ((JavascriptExecutor) WebDriverUtils.getDriver()).executeScript("arguments[0].scrollIntoView(true);",
                    element);
            element.click();
        } catch (NoSuchElementException e) {
            throw new AssertionError("Element with XPath '" + xpath + "' not found.", e);
        }
    }

    @When("I click on text {string}")
    public void iClickOnText(String text) {
        try {
            // Determine whether to use CSS or XPath based on the prefix
            By locator = getLocatorForText(text);

            // Find the element
            WebElement element = WebDriverUtils.getDriver().findElement(locator);

            // Scroll into view if necessary
            ((JavascriptExecutor) WebDriverUtils.getDriver()).executeScript("arguments[0].scrollIntoView(true);",
                    element);

            // Click the element
            element.click();
        } catch (NoSuchElementException e) {
            // Fail the test with a meaningful error message
            throw new AssertionError("Element with text '" + text + "' not found.", e);
        }
    }

    @When("I type {string} into locator {string}")
    public void iTypeInto(String text, String selector) {
        WebElement element = WebDriverUtils.getDriver().findElement(getLocator(selector));
        element.sendKeys(text);
    }

    @When("I clear {string}")
    public void iClear(String selector) {
        WebElement element = WebDriverUtils.getDriver().findElement(getLocator(selector));
        element.clear();
    }

    @When("I check {string}")
    public void iCheck(String selector) {
        try {
            WebElement checkbox = WebDriverUtils.getDriver().findElement(getLocator(selector));
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
        } catch (NoSuchElementException e) {
            throw new AssertionError("Checkbox with selector '" + selector + "' not found.", e);
        }
    }

    @When("I uncheck {string}")
    public void iUncheck(String selector) {
        try {
            WebElement checkbox = WebDriverUtils.getDriver().findElement(getLocator(selector));
            if (checkbox.isSelected()) {
                checkbox.click();
            }
        } catch (NoSuchElementException e) {
            throw new AssertionError("Checkbox with selector '" + selector + "' not found.", e);
        }
    }

    @Then("I see heading {string}")
    public void iSeeHeading(String expectedText) {
        try {
            WebElement heading = WebDriverUtils.getDriver().findElement(By.xpath(
                    "//*[self::h1 or self::h2 or self::h3 or self::h4 or self::h5 or self::h6][contains(text(), '"
                            + expectedText + "')]"));
            Assert.assertTrue(heading.isDisplayed(), "Heading '" + expectedText + "' is not displayed.");
        } catch (NoSuchElementException e) {
            Assert.fail("Heading '" + expectedText + "' not found.");
        }
    }

    @Then("I do not see heading {string}")
    public void iDoNotSeeHeading(String selector) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(selector)));
        } catch (org.openqa.selenium.TimeoutException e) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                Assert.assertTrue(element.isDisplayed(), "Heading is displayed: " + selector);
            } catch (NoSuchElementException noSuchElementException) {
                // Expected, element is not present
            }
        }
    }

    @Then("I see label {string}")
    public void iSeeLabel(String selector) {
        try {
            WebElement label = driver.findElement(By.cssSelector(selector));
            Assert.assertTrue(label.isDisplayed(), "Label not displayed: " + selector);
        } catch (NoSuchElementException e) {
            Assert.fail("Label not found: " + selector);
        }
    }

    @Then("I do not see label {string}")
    public void iDoNotSeeLabel(String selector) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(selector)));
        } catch (org.openqa.selenium.TimeoutException e) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                Assert.assertTrue(element.isDisplayed(), "Label is displayed: " + selector);
            } catch (NoSuchElementException noSuchElementException) {
                // Expected, element is not present
            }
        }
    }

    @Then("I see link {string}")
    public void iSeeLink(String selector) {
        try {
            WebElement link = driver.findElement(By.cssSelector(selector));
            Assert.assertTrue(link.isDisplayed(), "Link not displayed: " + selector);
        } catch (NoSuchElementException e) {
            Assert.fail("Link not found: " + selector);
        }
    }

    @Then("I do not see link {string}")
    public void iDoNotSeeLink(String selector) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(selector)));
        } catch (org.openqa.selenium.TimeoutException e) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                Assert.assertTrue(element.isDisplayed(), "Link is displayed: " + selector);
            } catch (NoSuchElementException noSuchElementException) {
                // Expected, element is not present
            }
        }
    }

    @Then("I see text {string}")
    public void iSeeText(String expectedText) {
        try {
            // Use XPath to find an element containing the expected text
            WebElement element = WebDriverUtils.getDriver()
                    .findElement(By.xpath("//*[contains(text(), '" + expectedText + "')]"));

            // Assert that the element is displayed
            Assert.assertTrue(element.isDisplayed(),
                    "Expected text '" + expectedText + "' is not displayed on the page.");
        } catch (NoSuchElementException e) {
            // Fail the test if the text is not found
            Assert.fail("Expected text '" + expectedText + "' was not found on the page.");
        }
    }

    @Then("I do not see text {string}")
    public void iDoNotSeeText(String selector) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(selector)));
        } catch (org.openqa.selenium.TimeoutException e) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                Assert.assertTrue(element.isDisplayed(), "Text is displayed: " + selector);
            } catch (NoSuchElementException noSuchElementException) {
                // Expected, element is not present
            }
        }
    }

    @Then("I see input value {string} in {string}")
    public void iSeeInputValue(String expectedValue, String selector) {
        WebElement input = driver.findElement(By.cssSelector(selector));
        String actualValue = input.getDomAttribute("value");
        assertEquals(actualValue, expectedValue, "Incorrect input value");
    }

    @Then("I see textarea value {string} in {string}")
    public void iSeeTextareaValue(String expectedValue, String selector) {
        WebElement textarea = driver.findElement(By.cssSelector(selector));
        String actualValue = textarea.getDomAttribute("value");
        assertEquals(actualValue, expectedValue, "Incorrect textarea value");
    }

    @When("I click on button {string}")
    public void iClickOnButton(String selector) {
        driver.findElement(By.cssSelector(selector)).click();
    }

    @When("I click on label {string}")
    public void iClickOnLabel(String selector) {
        driver.findElement(By.cssSelector(selector)).click();
    }

    @When("I click on link {string}")
    public void iClickOnLink(String linkText) {
        try {
            // Use the getLocator method to allow both CSS and XPath
            WebElement link = WebDriverUtils.getDriver().findElement(getLocator("a:contains('" + linkText + "')"));

            // Scroll into view if necessary
            ((JavascriptExecutor) WebDriverUtils.getDriver()).executeScript("arguments[0].scrollIntoView(true);", link);

            // Click the link
            link.click();
        } catch (Exception e) {
            throw new AssertionError("Link with text '" + linkText + "' not found or could not be clicked.", e);
        }
    }

    @When("I double click {string}")
    public void iDoubleClick(String selector) {
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        WebElement element = driver.findElement(By.cssSelector(selector));
        actions.doubleClick(element).perform();
    }

    @Then("I see count elements is {int}")
    public void iSeeCountElementsIs(int expectedCount) {
        Assert.assertNotNull(lastFoundElements, "No elements found to count.");
        Assert.assertEquals(lastFoundElements.size(), expectedCount, "Incorrect element count.");
        lastFoundElements = null; // Reset after interaction
    }

    @When("I set value {string} in {string}")
    public void iSetValue(String value, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        element.clear();
        element.sendKeys(value);
    }

    @When("I submit form {string}")
    public void iSubmit(String selector) {
        driver.findElement(By.cssSelector(selector)).submit();
    }

    @When("I trigger event {string} on {string}")
    public void iTriggerEvent(String eventName, String selector) {
        try {
            WebElement element = WebDriverUtils.getDriver().findElement(By.cssSelector(selector));
            switch (eventName.toLowerCase()) {
                case "click":
                    element.click();
                    break;
                case "mouseover":
                    new org.openqa.selenium.interactions.Actions(WebDriverUtils.getDriver())
                            .moveToElement(element)
                            .perform();
                    break;
                case "keydown":
                    element.sendKeys(org.openqa.selenium.Keys.ENTER); // Example: Simulating Enter key
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported event: " + eventName);
            }
        } catch (NoSuchElementException e) {
            throw new AssertionError("Element with selector '" + selector + "' not found.", e);
        }
    }

    @When("I wait {int} milliseconds")
    public void iWaitMilliseconds(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while waiting.", e);
        }
    }

    @When("I wait {int} seconds")
    public void iWaitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("I see element attribute {string} contains {string} in {string}")
    public void iSeeElementAttributeContains(String attribute, String expectedValue, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        String actualValue = element.getDomAttribute(attribute);
        Assert.assertTrue(actualValue.contains(expectedValue),
                "Attribute '" + attribute + "' does not contain: " + expectedValue);
    }

    @Then("I see element attribute {string} equals {string} in {string}")
    public void iSeeElementAttributeEquals(String attribute, String expectedValue, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        String actualValue = element.getDomAttribute(attribute);
        assertEquals("Attribute '" + attribute + "' does not equal: " + expectedValue, expectedValue, actualValue);
    }

    @Then("I see element does not exist {string}")
    public void iSeeElementDoesNotExist(String selector) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(selector)));
        } catch (org.openqa.selenium.TimeoutException e) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                Assert.assertTrue(element.isDisplayed(), "Element is displayed: " + selector);
            } catch (NoSuchElementException noSuchElementException) {
                // Expected, element is not present
            }
        }
    }

    @Then("I see element exists {string}")
    public void iSeeElementExists(String selector) {
        try {
            driver.findElement(By.cssSelector(selector));
        } catch (NoSuchElementException e) {
            fail("Element does not exist: " + selector);
        }
    }

    @Then("I see element has attribute {string} in {string}")
    public void iSeeElementHasAttribute(String attribute, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        String actualValue = element.getDomAttribute(attribute);
        assertNotNull(actualValue, "Element does not have attribute: " + attribute);
    }

    @Then("I see element is not visible {string}")
    public void iSeeElementIsNotVisible(String selector) {
        try {
            WebElement element = driver.findElement(By.cssSelector(selector));
            Assert.assertFalse(element.isDisplayed(), "Element is visible: " + selector);
        } catch (NoSuchElementException e) {
            // Element doesn't exist, which also means it's not visible
        }
    }

    @Then("I see element is visible {string}")
    public void iSeeElementIsVisible(String selector) {
        try {
            WebElement element = driver.findElement(By.cssSelector(selector));
            Assert.assertTrue(element.isDisplayed(), "Element is not visible: " + selector);

        } catch (NoSuchElementException e) {
            Assert.fail("Element does not exist: " + selector);
        }
    }

    @When("I find form {string}")
    public void iFindForm(String selector) {
        driver.findElement(By.cssSelector(selector)); // Assuming selector targets the form
    }

    @When("I find heading by text {string}")
    public void iFindHeadingByText(String text) {
        driver.findElement(By.xpath("//h1[contains(text(), '" + text + "')] | //h2[contains(text(), '" + text
                + "')] | //h3[contains(text(), '" + text + "')] | //h4[contains(text(), '" + text
                + "')] | //h5[contains(text(), '" + text + "')] | //h6[contains(text(), '" + text + "')]"));
    }

    @When("I find headings by text {string}")
    public void iFindHeadingsByText(String text) {
        // Improved XPath to handle:
        // 1. All heading levels (h1-h6)
        // 2. Text anywhere in the heading (including nested elements)
        // 3. Case-insensitive matching
        // 4. Whitespace normalization
        String xpath = "//*[self::h1 or self::h2 or self::h3 or self::h4 or self::h5 or self::h6]" +
                "[contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), " +
                "'" + text.toLowerCase() + "')]";

        List<WebElement> headings = driver.findElements(By.xpath(xpath));

        assertFalse(
                headings.isEmpty(),
                "No headings found containing text (case-insensitive): '" + text + "'");
    }

    @When("I get children {string}")
    public void iGetChildren(String selector) {
        WebElement parentElement = driver.findElement(getLocator(selector));
        List<WebElement> children = parentElement.findElements(By.xpath("./*")); // Find direct children
        assertTrue(!children.isEmpty(), "No children found for selector: " + selector);

        // Store the children in lastFoundElements if needed
        lastFoundElements = children;
    }

    @When("I get element by display value {string}")
    public void iGetElementByDisplayValue(String displayValue) {
        lastFoundElement = driver.findElement(getLocator("[value='" + displayValue + "']"));
        System.out.println(lastFoundElement);

    }

    @When("I get element by selector {string}")
    public void iGetElementBySelector(String selector) {
        lastFoundElement = WebDriverUtils.getDriver().findElement(getLocator(selector));
        Assert.assertNotNull(lastFoundElement, "No element found for selector: " + selector);
    }

    @When("I get elements by selector {string}")
    public void iGetElementsBySelector(String selector) {
        lastFoundElements = WebDriverUtils.getDriver().findElements(getLocator(selector));
        Assert.assertFalse(lastFoundElements.isEmpty(), "No elements found for selector: " + selector);
    }

    @When("I get first element {string}")
    public void iGetFirstElement(String selector) {
        List<WebElement> elements = WebDriverUtils.getDriver().findElements(getLocator(selector));
        assertTrue(!elements.isEmpty(), "No elements found for selector: " + selector);
        lastFoundElement = elements.get(0); // Store the first element
        System.out.println(lastFoundElement);

    }

    @When("I get first element")
    public void iGetFirstElement() {
        Assert.assertNotNull(lastFoundElements, "No elements found to retrieve the first element.");
        Assert.assertFalse(lastFoundElements.isEmpty(), "No elements found to retrieve the first element.");

        // Store the first element in lastFoundElement
        lastFoundElement = lastFoundElements.get(0);
    }

    @When("I get last element {string}")
    public void iGetLastElement(String selector) {
        List<WebElement> elements = WebDriverUtils.getDriver().findElements(getLocator(selector));
        assertTrue(!elements.isEmpty(), "No elements found for selector: " + selector);
        lastFoundElement = elements.get(elements.size() - 1); // Store the last element
    }

    @When("I get last element")
    public void iGetLastElement() {
        Assert.assertNotNull(lastFoundElements, "No elements found to retrieve the last element.");
        Assert.assertFalse(lastFoundElements.isEmpty(), "No elements found to retrieve the last element.");

        // Store the last element in lastFoundElement
        lastFoundElement = lastFoundElements.get(lastFoundElements.size() - 1);
        System.out.println(lastFoundElement);

    }

    @When("I get nth element {string} at index {int}")
    public void iGetNthElement(String selector, int index) {
        List<WebElement> elements = WebDriverUtils.getDriver().findElements(getLocator(selector));
        assertTrue(elements.size() > index, "Element at index " + index + " not found for selector: " + selector);
        lastFoundElement = elements.get(index); // Store the nth element
    }

    @When("I get {string} element")
    public void iGetNthElement(String ordinal) {
        Assert.assertNotNull(lastFoundElements, "No elements found.");

        // Parse the ordinal string into an integer
        int index = parseOrdinal(ordinal);
        Assert.assertTrue(index > 0, "Index must be greater than 0.");

        // Convert to zero-based index
        int zeroBasedIndex = index - 1;
        Assert.assertTrue(zeroBasedIndex < lastFoundElements.size(), "Element at index " + index + " not found.");

        // Store the nth element in lastFoundElement
        lastFoundElement = lastFoundElements.get(zeroBasedIndex);
    }

    @When("I go back")
    public void iGoBack() {
        driver.navigate().back();
    }

    @When("I go forward")
    public void iGoForward() {
        driver.navigate().forward();
    }

    @When("I reload the page")
    public void iReloadThePage() {
        driver.navigate().refresh();
    }

    @When("I right click {string}")
    public void iRightClick(String selector) {
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        WebElement element = driver.findElement(By.cssSelector(selector));
        actions.contextClick(element).perform();
    }

    @When("I right click on text {string}")
    public void iRightClickOnText(String text) {
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        WebElement element = driver.findElement(By.xpath("//*[contains(text(), '" + text + "')]"));
        actions.contextClick(element).perform();
    }

    @When("I select {string} from {string}")
    public void iSelect(String value, String selector) {
        org.openqa.selenium.support.ui.Select dropdown = new org.openqa.selenium.support.ui.Select(
                driver.findElement(By.cssSelector(selector)));
        dropdown.selectByValue(value);
    }

    @When("I select {string} from the {int} element")
    public void iSelectFromTheNthElement(String optionText, int index) {
        WebElement nthElement = getNthElement(index);
        Select dropdown = new Select(nthElement);
        dropdown.selectByVisibleText(optionText);
    }

    @When("I upload file {string}")
    public void iUploadFile(String filePath) {
        assertNotNull(lastFoundElement, "No element found to upload file to.");
        lastFoundElement.sendKeys(filePath);
        System.out.println(lastFoundElement);

    }

    @When("I upload file {string} to the {string} element")
    public void iUploadFileToTheNthElement(String filePath, String ordinal) {
        Assert.assertNotNull(lastFoundElements, "No elements found to upload file to.");

        // Parse the ordinal string into an integer
        int index = parseOrdinal(ordinal);
        Assert.assertTrue(index > 0, "Index must be greater than 0.");

        // Convert to zero-based index
        int zeroBasedIndex = index - 1;
        Assert.assertTrue(zeroBasedIndex < lastFoundElements.size(), "Element at index " + index + " not found.");

        // Upload file to the nth element
        WebElement nthElement = lastFoundElements.get(zeroBasedIndex);
        nthElement.sendKeys(filePath);
    }

    @When("I select {string}")
    public void iSelect(String optionText) {
        assertNotNull(lastFoundElement, "No element found to select from.");
        Select dropdown = new Select(lastFoundElement);
        dropdown.selectByVisibleText(optionText);
    }

    @When("I set attribute {string} to {string} in {string}")
    public void iSetAttribute(String attribute, String value, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, attribute, value);
    }

    @When("I clear all cookies")
    public void iClearAllCookies() {
        driver.manage().deleteAllCookies();
    }

    @When("I clear cookie {string}")
    public void iClearCookie(String name) {
        driver.manage().deleteCookieNamed(name);
    }

    @When("I click position {string}")
    public void iClickPosition(String selector) {
        // Needs further implementation using Actions class and offset if needed
        driver.findElement(By.cssSelector(selector)).click();
    }

    @When("I double click position {string}")
    public void iDoubleClickPosition(String selector) {
        // Needs further implementation using Actions class and offset if needed
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        WebElement element = driver.findElement(By.cssSelector(selector));
        actions.doubleClick(element).perform();
    }

    @When("I get focused element")
    public void iGetFocusedElement() {
        driver.switchTo().activeElement();
    }

    @Given("I go to URL {string}")
    public void iGoToURL(String url) {
        // Check if the URL is relative (starts with "/")
        if (url.startsWith("/")) {
            // Fetch the base URL from config.properties
            String baseUrl = ConfigLoader.getUiBaseUrl();
            url = baseUrl + url; // Combine base URL and relative path
        }
        // Navigate to the resolved URL
        WebDriverUtils.getDriver().get(url);
    }

    @When("I pause")
    public void iPause() {
        // Cucumber doesn't have a direct "pause"
        // You can use a breakpoint for debugging or Thread.sleep for a hard pause (not
        // recommended for production tests)
        // For debugging, set a breakpoint in your IDE.
        // For a hard pause:
        try {
            Thread.sleep(5000); // 5 seconds (example - don't use in real tests)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // throw new UnsupportedOperationException("Pause is for debugging only. Use
        // breakpoints.");
    }

    @When("I scroll into view {string}")
    public void iScrollIntoView(String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    @When("I scroll to position {string}")
    public void iScrollToPosition(String selector) {
        // Selenium doesn't have a direct "scroll to position of element"
        // This would likely require JavaScriptExecutor to get element coordinates and
        // then scroll
        throw new UnsupportedOperationException(
                "Scroll to element position requires JavaScriptExecutor and specific implementation.");
    }

    @When("I scroll window to position x {int} y {int}")
    public void iScrollWindowToPosition(int x, int y) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(arguments[0], arguments[1]);",
                x, y);
    }

    @When("I select file {string} to {string}")
    public void iSelectFile(String filePath, String selector) {
        driver.findElement(By.cssSelector(selector)).sendKeys(filePath); // Send keys to file input
    }

    @When("I select option {string} from {string}")
    public void iSelectOption(String optionText, String selector) {
        org.openqa.selenium.support.ui.Select dropdown = new org.openqa.selenium.support.ui.Select(
                driver.findElement(By.cssSelector(selector)));
        dropdown.selectByVisibleText(optionText);
    }

    @When("I set Cypress config {string} to {string}")
    public void iSetCypressConfig(String key, String value) {
        // Selenium doesn't have Cypress config, this is not applicable
        throw new UnsupportedOperationException("Setting Cypress config is not applicable in Selenium.");
    }

    @When("I set environment variable {string} to {string}")
    public void iSetEnvironmentVariable(String key, String value) {
        // Selenium doesn't directly set environment variables. This is a system-level
        // operation.
        // You might use Java's System.setProperty() if absolutely necessary (but avoid
        // this in tests if possible).
        System.setProperty(key, value);
        // throw new UnsupportedOperationException("Setting environment variables should
        // be avoided in tests if possible.");
    }

    @When("I set local storage item {string} to {string}")
    public void iSetLocalStorageItem(String key, String value) {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript(String.format("window.localStorage.setItem('%s','%s')", key, value));
    }

    @When("I set session storage item {string} to {string}")
    public void iSetSessionStorageItem(String key, String value) {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript(String.format("window.sessionStorage.setItem('%s','%s')", key, value));
    }

    @When("I set system time {string}")
    public void iSetSystemTime(String time) {
        // Selenium doesn't directly control system time. This is OS-level.
        throw new UnsupportedOperationException("Setting system time is not possible with Selenium.");
    }

    @When("I set viewport width {int} height {int}")
    public void iSetViewportWidthHeight(int width, int height) {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
    }

    @When("I trigger event {string} on element {string}")
    public void iTriggerEventOnElement(String event, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        String script = String.format("$(arguments[0]).trigger('%s')", event);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script, element);
    }

    @When("I find input by label text {string}")
    public void iFindInputByLabelText(String labelText) {
        By locator = By.xpath("//label[contains(text(), '" + labelText + "')]/following-sibling::input");
        lastFoundElement = driver.findElement(locator);
    }

    @When("I find element by alt text {string}")
    public void iFindElementByAltText(String altText) {
        lastFoundElement = driver.findElement(getLocator("[alt='" + altText + "']"));
    }

    @When("I find element by name {string}")
    public void iFindElementByName(String name) {
        lastFoundElement = driver.findElement(getLocator("[name='" + name + "']"));
    }

    @When("I find element by placeholder text {string}")
    public void iFindElementByPlaceholderText(String placeholderText) {
        lastFoundElement = driver.findElement(getLocator("[placeholder='" + placeholderText + "']"));
    }

    @When("I find element by role {string}")
    public void iFindElementByRole(String role) {
        lastFoundElement = driver.findElement(getLocator("[role='" + role + "']"));
    }

    @When("I find element by selector {string}")
    public void iFindElementBySelector(String selector) {
        lastFoundElement = driver.findElement(getLocator(selector));
    }

    @When("I find element by testid {string}")
    public void iFindElementByTestid(String testid) {
        lastFoundElement = driver.findElement(getLocator("[data-testid='" + testid + "']"));
    }

    @When("I find element by text {string}")
    public void iFindElementByText(String text) {
        lastFoundElement = driver.findElement(By.xpath("//*[contains(text(), '" + text + "')]"));
    }

    @When("I find elements by text {string}")
    public void iFindElementsByText(String text) {
        List<WebElement> elements = driver.findElements(By.xpath("//*[contains(text(), '" + text + "')]"));
        System.out.println(elements);
        Assert.assertFalse(elements.isEmpty(), "No elements found containing text: " + text);

        // Store the elements in lastFoundElements for subsequent interactions
        lastFoundElements = elements;

    }

    @When("I find element by title {string}")
    public void iFindElementByTitle(String title) {
        lastFoundElement = driver.findElement(getLocator("[title='" + title + "']"));
    }

    @When("I find image by alt text {string}")
    public void iFindImageByAltText(String altText) {
        lastFoundElement = driver.findElement(getLocator("img[alt='" + altText + "']"));
    }

    @When("I find input by display value {string}")
    public void iFindInputByDisplayValue(String displayValue) {
        lastFoundElement = driver.findElement(getLocator("input[value='" + displayValue + "']"));
    }

    @When("I find link by text {string}")
    public void iFindLinkByText(String text) {
        lastFoundElement = driver.findElement(By.xpath("//a[contains(text(), '" + text + "')]"));
    }

    @When("I find select by display value {string}")
    public void iFindSelectByDisplayValue(String displayValue) {
        lastFoundElement = driver
                .findElement(By.xpath("//select/option[text()='" + displayValue + "']/parent::select"));
    }

    @When("I find textarea by display value {string}")
    public void iFindTextareaByDisplayValue(String displayValue) {
        lastFoundElement = driver.findElement(getLocator("textarea[value='" + displayValue + "']"));
    }

    @When("I find textarea by placeholder text {string}")
    public void iFindTextareaByPlaceholderText(String placeholderText) {
        lastFoundElement = driver.findElement(getLocator("textarea[placeholder='" + placeholderText + "']"));
    }

    @When("I find buttons by text {string}")
    public void iFindButtonsByText(String text) {
        lastFoundElements = driver.findElements(By.xpath("//button[contains(., '" + text + "')]"));
    }

    @When("I find elements by alt text {string}")
    public void iFindElementsByAltText(String altText) {
        lastFoundElements = driver.findElements(getLocator("[alt='" + altText + "']"));
    }

    @When("I find elements by label text {string}")
    public void iFindElementsByLabelText(String labelText) {
        lastFoundElements = driver.findElements(By.xpath("//label[normalize-space()='" + labelText + "']" +
                "| //label[normalize-space()='" + labelText + "']//input" +
                "| //*[@id=//label[normalize-space()='" + labelText + "']/@for]"));
    }

    @When("I find elements by name {string}")
    public void iFindElementsByName(String name) {
        lastFoundElements = driver.findElements(getLocator("[name='" + name + "']"));
    }

    @When("I find elements by placeholder text {string}")
    public void iFindElementsByPlaceholderText(String placeholderText) {
        lastFoundElements = driver.findElements(getLocator("[placeholder='" + placeholderText + "']"));
    }

    @When("I find elements by role {string}")
    public void iFindElementsByRole(String role) {
        lastFoundElements = driver.findElements(getLocator("[role='" + role + "']"));
    }

    @When("I find elements by testid {string}")
    public void iFindElementsByTestid(String testid) {
        lastFoundElements = driver.findElements(getLocator("[data-testid='" + testid + "']"));
    }

    @When("I find elements by title {string}")
    public void iFindElementsByTitle(String title) {
        lastFoundElements = driver.findElements(getLocator("[title='" + title + "']"));
    }

    @When("I find images by alt text {string}")
    public void iFindImagesByAltText(String altText) {
        lastFoundElements = driver.findElements(getLocator("img[alt='" + altText + "']"));
    }

    @When("I find inputs by name {string}")
    public void iFindInputsByName(String name) {
        lastFoundElements = driver.findElements(getLocator("input[name='" + name + "']"));
    }

    @When("I find inputs by placeholder text {string}")
    public void iFindInputsByPlaceholderText(String placeholderText) {
        lastFoundElements = driver.findElements(getLocator("input[placeholder='" + placeholderText + "']"));
    }

    @When("I find links by text {string}")
    public void iFindLinksByText(String text) {
        lastFoundElements = driver.findElements(By.xpath("//a[contains(text(), '" + text + "')]"));
    }

    @When("I find textareas by placeholder text {string}")
    public void iFindTextareasByPlaceholderText(String placeholderText) {
        lastFoundElements = driver.findElements(getLocator("textarea[placeholder='" + placeholderText + "']"));
    }

    @Then("I see count elements {string} is {int}")
    public void iSeeCountElements(String selector, int count) {
        List<WebElement> elements = driver.findElements(By.cssSelector(selector));
        assertEquals(count, elements.size(), "Incorrect element count for selector: " + selector);
    }

    @Then("I see document title {string}")
    public void iSeeDocumentTitle(String title) {
        assertEquals("Incorrect document title", title, driver.getTitle());
    }

    @Then("I see document title contains {string}")
    public void iSeeDocumentTitleContains(String titlePart) {
        assertTrue(driver.getTitle().contains(titlePart), "Document title does not contain: " + titlePart);
    }

    @Then("I see input value contains {string} in {string}")
    public void iSeeInputValueContains(String expectedValuePart, String selector) {
        WebElement input = driver.findElement(By.cssSelector(selector));
        String actualValue = input.getDomAttribute("value");
        assertTrue(actualValue.contains(expectedValuePart), "Input value does not contain: " + expectedValuePart);
    }

    @Then("I see pathname {string}")
    public void iSeePathname(String expectedPathname) {
        java.net.URL url;
        try {
            url = new java.net.URL(driver.getCurrentUrl());
            String actualPathname = url.getPath();
            assertEquals("Incorrect pathname", expectedPathname, actualPathname);
        } catch (java.net.MalformedURLException e) {
            fail("Malformed URL: " + driver.getCurrentUrl());
        }
    }

    @Then("I see pathname contains {string}")
    public void iSeePathnameContains(String expectedPathnamePart) {
        java.net.URL url;
        try {
            url = new java.net.URL(driver.getCurrentUrl());
            String actualPathname = url.getPath();
            assertTrue(actualPathname.contains(expectedPathnamePart),
                    "Pathname does not contain: " + expectedPathnamePart);
        } catch (java.net.MalformedURLException e) {
            fail("Malformed URL: " + driver.getCurrentUrl());
        }
    }

    @Then("I see URL {string}")
    public void iSeeURL(String expectedUrl) {
        assertEquals("Incorrect URL", expectedUrl, driver.getCurrentUrl());
    }

    @Then("I see URL contains {string}")
    public void iSeeURLContains(String expectedUrlPart) {
        assertTrue(driver.getCurrentUrl().contains(expectedUrlPart), "URL does not contain: " + expectedUrlPart);
    }

    @Then("I see value {string} in {string}")
    public void iSeeValue(String expectedValue, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        String actualValue = element.getDomAttribute("value");
        assertEquals("Incorrect value", expectedValue, actualValue);
    }

    @Then("I see visible text {string} in {string}")
    public void iSeeVisibleText(String expectedText, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        String actualText = element.getText();
        assertEquals("Incorrect visible text", expectedText, actualText);
    }

    @Then("I do not see value {string} in {string}")
    public void iDoNotSeeValue(String expectedValue, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        String actualValue = element.getDomAttribute("value");
        assertNotEquals("Value should not be: " + expectedValue, expectedValue, actualValue);
    }

    @Then("I do not see visible text {string} in {string}")
    public void iDoNotSeeVisibleText(String expectedText, String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        String actualText = element.getText();
        assertNotEquals("Visible text should not be: " + expectedText, expectedText, actualText);
    }
}