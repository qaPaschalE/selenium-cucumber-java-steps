// src/test/java/runners/DbTestRunnerTestCase.java
package runners;

import com.github.qaPaschalE.util.ConfigLoader;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeClass;

@CucumberOptions(
        features = "src/test/resources/features/db",
        glue = {"com.github.qaPaschalE.stepDefinitions.db", "com.github.qaPaschalE.util"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/db",
                "json:target/cucumber-reports/db.json"
        },
        monochrome = true
)
public class DbTestRunnerTestCase extends AbstractTestNGCucumberTests {

    @BeforeClass
    public void setup() {
        // Dynamically set the Cucumber tags using ConfigLoader
        String tags = ConfigLoader.getTags();
        System.setProperty("cucumber.tags", tags);
    }
}