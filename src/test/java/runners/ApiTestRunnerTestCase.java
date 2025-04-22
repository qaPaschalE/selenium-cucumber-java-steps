// src/test/java/runners/ApiTestRunnerTestCase.java
package runners;

import com.github.qaPaschalE.util.ConfigLoader;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeClass;

@CucumberOptions(
        features = "src/test/resources/features/api",
        glue = {"com.github.qaPaschalE.stepDefinitions.api", "com.github.qaPaschalE.util"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/api",
                "json:target/cucumber-reports/api.json"
        },
        monochrome = true
)
public class ApiTestRunnerTestCase extends AbstractTestNGCucumberTests {

    @BeforeClass
    public void setup() {
        // Dynamically set the Cucumber tags using ConfigLoader
        String tags = ConfigLoader.getTags();
        System.setProperty("cucumber.tags", tags);
    }
}